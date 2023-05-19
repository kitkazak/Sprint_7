package org.example.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.paths.Paths;
import org.json.simple.JSONObject;

public class Orders {

    public static Response Create(JSONObject body) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body((new JSONObject(body)).toJSONString())
                .when().post(Paths.orders);

    }

    public static Response GetAll() {
        return RestAssured
                .get(Paths.orders);
    }
}
