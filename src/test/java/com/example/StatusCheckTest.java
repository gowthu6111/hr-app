package com.example;

import com.example.utils.Polling;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCheckTest {

    @Test
    public void checkStatusEndpoint() throws Exception {
        String url = "https://apisforemployeecatalogmanagementsystem.onrender.com/status";
        Response response = Polling.executeWithRetry(() -> RestAssured.get(url));
        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertEquals("Server is running", response.jsonPath().getString("status"), "Unexpected status message");
    }
}
