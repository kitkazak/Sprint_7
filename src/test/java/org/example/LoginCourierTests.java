package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class LoginCourierTests {

    JSONObject createCourierValidObj;
    JSONObject loginCourierValidObj;
    JSONObject loginCourierNotAllFieldsObj;

    JSONObject loginCourierNotExistingObj;

    @Before
    public void setUp() throws IOException, ParseException {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
        createCourierValidObj = (JSONObject)
                (new JSONParser()).parse(new FileReader("src/test/data/CreateCourierValid.json"));
        loginCourierValidObj = (JSONObject)
                (new JSONParser()).parse(new FileReader("src/test/data/LoginCourierValid.json"));
        loginCourierNotAllFieldsObj = (JSONObject)
                (new JSONParser()).parse(new FileReader("src/test/data/LoginCourierNotAllFields.json"));
        loginCourierNotExistingObj = (JSONObject)
                (new JSONParser()).parse(new FileReader("src/test/data/LoginCourierNotExisting.json"));

        // Create a courier
        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(createCourierValidObj.toJSONString())
                .when().post("/courier")
                .then().statusCode(201).and().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    @Description("Запрос возвращает правильный код ответа — 200. Успешный запрос возвращает \"id\", " +
            "который необходим для удаления курьера")
    public void courierCouldLogin() {
        // Login
        Response loginRes = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(loginCourierValidObj.toJSONString())
                .when().post("/courier/login");

        loginRes.then().statusCode(200).and().body("$", hasKey("id"));
        Assert.assertTrue("id's got to be a number", loginRes.body().jsonPath().get("id") instanceof Number);
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля")
    @Description("Если какого-то поля нет, запрос возвращает ошибку")
    public void allFieldsRequired() {
        // Login
        Response loginRes = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(loginCourierNotAllFieldsObj.toJSONString())
                .when().post("/courier/login");

        loginRes.then().statusCode(400).and().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Несуществующий пользователь")
    @Description("Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void notExistingCourierReturnsError() {
        // Login
        Response loginRes = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(loginCourierNotExistingObj.toJSONString())
                .when().post("/courier/login");

        loginRes.then().statusCode(404).and().body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        // Login
        Response loginRes = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(loginCourierValidObj.toJSONString())
                .when().post("/courier/login");

        loginRes.then().statusCode(200);

        // Delete new courier
        int courierId = loginRes.jsonPath().get("id");
        RestAssured
                .when().delete(String.format("/courier/%d", courierId))
                .then().statusCode(200).and().body("ok", equalTo(true));
    }
}
