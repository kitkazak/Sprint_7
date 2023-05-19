package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.api.Orders;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static org.hamcrest.Matchers.hasKey;

@RunWith(Parameterized.class)
public class CreateOrderTests {

    private final String[] color;

    public CreateOrderTests(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { new String[] {
                        "BLACK"
                } },
                { new String[] {
                        "GRAY"
                } },
                { new String[] {
                        "BLACK",
                        "GRAY"
                } },
                { new String[] {} },
        });
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Можно указать один из цветов — BLACK или GREY, указать оба цвета или не указывать их вообще. " +
        "При создании заказа должен возвращаться его трекинговый номер — track.")
    public void createOrderTest() {
        HashMap<String, Object> createOrderRequestBody = new HashMap<>();

        createOrderRequestBody.put("firstName", "Naruto");
        createOrderRequestBody.put("lastName", "Uchiha");
        createOrderRequestBody.put("address", "Konoha, 142 apt.");
        createOrderRequestBody.put("metroStation", 4);
        createOrderRequestBody.put("phone", "+7 800 355 35 35");
        createOrderRequestBody.put("rentTime", 5);
        createOrderRequestBody.put("deliveryDate", "2020-06-06");
        createOrderRequestBody.put("comment", "Saske, come back to Konoha");

            JSONArray colorJSONArray = new JSONArray();
            colorJSONArray.addAll(Arrays.asList(color));

        createOrderRequestBody.put("color", colorJSONArray.toJSONString());

        Response res = Orders.Create(new JSONObject(createOrderRequestBody));
        res.then().statusCode(201).and().body("$", hasKey("track"));
    }

}
