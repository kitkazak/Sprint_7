package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.api.Orders;
import org.junit.Test;

import static org.hamcrest.Matchers.hasKey;

public class GetOrdersTests {
    @Test
    @DisplayName("Получение списка заказов")
    @Description("В тело ответа должен возвращаться список заказов.")
    public void getOrdersTest() {
        Response res = Orders.getAll();
        res.then().statusCode(200).and().body("$", hasKey("orders"));
    }
}
