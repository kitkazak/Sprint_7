package org.example.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.example.paths.Paths;

public class Courier {

    public static Response Create(JSONObject body) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body.toJSONString())
                .when().post(Paths.courier);
    }

    public static Response Login(JSONObject body) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body.toJSONString())
                .when().post(Paths.courierLogin);
    }

    public static Response Delete(int id) {
        return RestAssured
                .delete(String.format(Paths.courier + "%d", id));
    }
}
