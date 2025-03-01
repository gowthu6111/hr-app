package com.example;

import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.example.utils.Polling;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class StatusCheckTest {

    @Test
    public void checkStatusEndpoint() throws Exception {
        String url = "https://apisforemployeecatalogmanagementsystem.onrender.com/status";
        Response response = Polling.executeWithRetry(() -> RestAssured.get(url));
        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertEquals("Server is running", response.jsonPath().getString("status"), "Unexpected status message");
    }
}
