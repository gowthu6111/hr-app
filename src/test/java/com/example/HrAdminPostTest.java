package com.example;

import com.example.utils.Polling;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HrAdminPostTest {

    @Test
    public void checkStatusEndpoint() throws Exception {
        String url = "https://apisforemployeecatalogmanagementsystem.onrender.com/status";
        Response response = Polling.executeWithRetry(() -> RestAssured.get(url));
        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertEquals("Server is running", response.jsonPath().getString("status"), "Unexpected status message");
    }

    @Test
    public void checkLoginSuccess() throws Exception {

        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com"; 
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject(); 
        requestParams.put("username", "admin1"); 
        requestParams.put("password", "securePassword"); 

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request 
        request.body(requestParams.toString()); // Post the request and check the response
         
        Response response = Polling.executeWithRetry(() -> request.post("/hr/login"));
        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(response.getBody().jsonPath().get("token"), "token value doesn't exist");
    }

    @Test
    public void checkInvalidLogin() throws Exception {

        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com"; 
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject(); 
        requestParams.put("username", "Invalid"); 
        requestParams.put("password", "Invalid"); 

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request 
        request.body(requestParams.toString()); // Post the request and check the response
         
        Response response = Polling.executeWithRetry(() -> request.post("/hr/login"));
        assertEquals(401, response.statusCode(), "Status code is not 401");
        assertEquals("Invalid credentials", response.getBody().jsonPath().get("error"), "Test failed for invalid login");
    }
}
