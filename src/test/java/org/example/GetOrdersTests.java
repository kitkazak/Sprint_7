package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.hasKey;

public class GetOrdersTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("В тело ответа должен возвращаться список заказов.")
    public void getOrdersTest() {
        Response res = RestAssured
                .get("/orders");

        res.then().statusCode(200).and().body("$", hasKey("orders"));
    }

}
