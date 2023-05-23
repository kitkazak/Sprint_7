package org.example.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.paths.Paths;
import org.json.simple.JSONObject;

public class Orders {

    public static Response create(JSONObject body) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body((new JSONObject(body)).toJSONString())
                .when().post(Paths.ORDERS);

    }

    public static Response getAll() {
        return RestAssured
                .get(Paths.ORDERS);
    }
}
