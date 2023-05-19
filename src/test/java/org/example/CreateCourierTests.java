package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import java.io.FileReader;
import java.io.IOException;

import static org.hamcrest.Matchers.*;

public class CreateCourierTests {

    JSONObject createCourierValidObj;
    JSONObject loginCourierValidObj;
    JSONObject createCourierNotAllFieldsObj;

    @Before
    public void setUp() throws IOException, ParseException {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
        createCourierValidObj = (JSONObject)
                (new JSONParser()).parse(new FileReader("src/test/data/CreateCourierValid.json"));
        loginCourierValidObj = (JSONObject)
                (new JSONParser()).parse(new FileReader("src/test/data/LoginCourierValid.json"));
        createCourierNotAllFieldsObj = (JSONObject)
                (new JSONParser()).parse(new FileReader("src/test/data/CreateCourierNotAllFields.json"));
    }

    @Test
    @DisplayName("Курьера можно создать")
    @Description("Запрос возвращает правильный код ответа — 201. Успешный запрос возвращает \"ok\": true")
    public void createCourierIsPossible() {
        // Create new courier
        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(createCourierValidObj.toJSONString())
                .when().post("/courier")
                .then().statusCode(201).and().body("ok", equalTo(true));

        // Login to get new courier's id
        Response loginRes = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(loginCourierValidObj.toJSONString())
                .when().post("/courier/login");

        loginRes.then().statusCode(200);
        int courierId = loginRes.jsonPath().get("id");

        // Delete new courier
        RestAssured
                .when().delete(String.format("/courier/%d", courierId))
                .then().statusCode(200).and().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void createCourierDuplicateIsNotPossible() {
        // Create new courier
        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(createCourierValidObj.toJSONString())
                .when().post("/courier")
                .then().statusCode(201).and().body("ok", equalTo(true));

        // Create new courier with the same data
        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(createCourierValidObj.toJSONString())
                .when().post("/courier")
                .then().statusCode(409)
                .and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        // Login to get new courier's id
        Response loginRes = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(loginCourierValidObj.toJSONString())
                .when().post("/courier/login");

        loginRes.then().statusCode(200);
        int courierId = loginRes.jsonPath().get("id");

        // Delete new courier
        RestAssured
                .when().delete(String.format("/courier/%d", courierId))
                .then().statusCode(200).and().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    @Description("Если одного из полей нет, запрос возвращает ошибку")
    public void createCourierAllFieldsRequired() {
        // Create new courier without all fields
        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(createCourierNotAllFieldsObj.toJSONString())
                .when().post("/courier")
                .then().statusCode(400)
                .and().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
