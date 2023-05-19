package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import java.io.FileReader;
import java.io.IOException;

import org.example.api.Courier;

import static org.hamcrest.Matchers.*;

public class CreateCourierTests {

    JSONObject createCourierValidObj;
    JSONObject loginCourierValidObj;
    JSONObject createCourierNotAllFieldsObj;

    @Before
    public void setUp() throws IOException, ParseException {
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
        Courier.Create(createCourierValidObj);
        Response loginRes = Courier.Login(createCourierValidObj);

        loginRes.then().statusCode(200);
        int courierId = loginRes.jsonPath().get("id");

        Courier.Delete(courierId);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void createCourierDuplicateIsNotPossible() {
        Courier.Create(createCourierValidObj);

        // Create new courier with the same data
        Response createRes = Courier.Create(createCourierValidObj);
        createRes.then()
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        Response loginRes = Courier.Login(createCourierValidObj);

        loginRes.then().statusCode(200);
        int courierId = loginRes.jsonPath().get("id");

        Courier.Delete(courierId);
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    @Description("Если одного из полей нет, запрос возвращает ошибку")
    public void createCourierAllFieldsRequired() {
        Response createRes = Courier.Create(createCourierNotAllFieldsObj);
        createRes.then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
