package org.example.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.example.paths.Paths;

public class Courier {

    public static Response create(JSONObject body) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body.toJSONString())
                .when().post(Paths.COURIER);
    }

    public static Response login(JSONObject body) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body.toJSONString())
                .when().post(Paths.COURIER_LOGIN);
    }

    public static void delete(int id) {
        RestAssured
                .delete(String.format(Paths.COURIER + "%d", id));
    }
}
