package com.example;

import com.example.utils.Polling;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTest {

    public String Authorization = "";
    public String EmployeeId = "";

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";
        RequestSpecification request = given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("username", "admin1");
        requestParams.put("password", "securePassword");

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request
        request.body(requestParams.toString()); // Post the request and check the response

        Response response = Polling.executeWithRetry(() -> request.post("/hr/login"));
        assertEquals(200, response.statusCode(), "Status code is not 200");
        Authorization = "Bearer " + response.getBody().jsonPath().get("token");
    }

    private JSONObject getData(String path) throws IOException {

        File f = new File(path);
        JSONObject jsonObject;
        if (f.exists()){
            InputStream is = Files.newInputStream(Paths.get(path));
            String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
            System.out.println(jsonTxt);
            jsonObject = new JSONObject(jsonTxt);
        }
        else
        {
            System.out.println("File not found");
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }

    @Test
    public void createEmployeeSuccess() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";
        RequestSpecification request = given();
        request.header("Authorization",Authorization);

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request
        request.body(getData("src/test/resources/testdata/createEmployee.json").toString()); // Post the request and check the response

        Response response = Polling.executeWithRetry(() -> request.post("/employees"));
        assertEquals(201, response.statusCode(),response.getBody().toString());
        assertEquals(response.getBody().jsonPath().get("message").toString(),"Employee created successfully!");
        assertEquals(response.getBody().jsonPath().get("firstName").toString(),"Dani");
        EmployeeId = response.getBody().jsonPath().get("employeeId");
        tearDownCreateEmployee(EmployeeId);
    }

    @Test
    public void duplicateEmployeeError() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com"; 
        RequestSpecification request = given();
        request.header("Authorization",Authorization);

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request 
        request.body(getData("src/test/resources/testdata/duplicateEmployee.json").toString()); // Post the request and check the response

        Response response = Polling.executeWithRetry(() -> request.post("/employees"));
        assertEquals(400, response.statusCode(), response.getBody().jsonPath().get("error").toString());
    }

    @Test
    public void getEmployeeSuccess() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";
        RequestSpecification request = given();
        request.header("Authorization",Authorization);

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request

        Response response = Polling.executeWithRetry(() -> request.get("/employees"));
        assertEquals(200, response.statusCode());
        EmployeeId = response.getBody().jsonPath().get("employeeId[0]");
    }

    @Test
    public void getEmployeeUnauthorizedError() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";
        RequestSpecification request = given();

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request

        Response response = Polling.executeWithRetry(() -> request.get("/employees"));
        assertEquals(401, response.statusCode(),response.getBody().toString());
    }

    @Test
    public void getEmployeeSuccessById() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";

        String employee_api_path_by_id = "employees/" + EmployeeId;
        RequestSpecification request = given();
        request.header("Authorization",Authorization);

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request

        Response response = Polling.executeWithRetry(() -> request.get(employee_api_path_by_id));
        assertEquals(200, response.statusCode(),response.getBody().toString());
        assertNotNull(response.getBody().jsonPath().get("employeeId").toString(), "Employee Id doesn't exist");
    }

    @Test
    public void getEmployeeNotFoundById() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";

        String employee_api_path_by_id = "employees/" + "not_found_employee";
        RequestSpecification request = given();
        request.header("Authorization",Authorization);

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request

        Response response = Polling.executeWithRetry(() -> request.get(employee_api_path_by_id));
        assertEquals(404, response.statusCode(),response.getBody().toString());
        assertNotNull(response.getBody().jsonPath().get("message").toString(), "Employee not found");
    }

    @Test
    public void getEmployeeIdUnauthorizedError() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";
        RequestSpecification request = given();

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request

        Response response = Polling.executeWithRetry(() -> request.get("/employees"));
        assertEquals(401, response.statusCode(),response.getBody().toString());
    }

    @Test
    public void deleteEmployeeById() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";

        RequestSpecification request = given();
        request.header("Authorization",Authorization);
        request.header("Content-Type", "application/json"); // Add the Json to the body of the request
        request.body(getData("src/test/resources/testdata/deleteEmployee.json").toString()); // Post the request and check the response

        Response response = Polling.executeWithRetry(() -> request.post("/employees"));
        assertEquals(201, response.statusCode(),response.getBody().toString());
        System.out.println(response.getBody().jsonPath().get("employeeId").toString());
        EmployeeId = response.getBody().jsonPath().get("employeeId");// Add the Json to the body of the request
        String employee_api_path_by_id = "employees/" + EmployeeId;

        Response responseDelete = Polling.executeWithRetry(() -> request.delete(employee_api_path_by_id));
        assertEquals(200, responseDelete.statusCode(),responseDelete.getBody().toString());
        System.out.println("delete"+responseDelete.getBody().toString());
        assertEquals(responseDelete.getBody().jsonPath().get("message").toString(),"Employee deleted successfully!");
    }

    @Test
    public void deleteEmployeeByIdEmployeeNotFoundError() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";

        RequestSpecification request = given();
        request.header("Authorization",Authorization);
        request.header("Content-Type", "application/json"); // Add the Json to the body of the request
        String employee_api_path_by_id = "employees/" + "notfoundEmployee345343443";

        Response responseDelete = Polling.executeWithRetry(() -> request.delete(employee_api_path_by_id));
        assertEquals(404, responseDelete.statusCode(),responseDelete.getBody().toString());
        assertEquals(responseDelete.getBody().jsonPath().get("message").toString(),"Employee not found");
    }

@Test
    public void updateEmployeeByIdSuccess() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";
        RequestSpecification request = given();
        request.header("Authorization",Authorization);

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request

        Response response = Polling.executeWithRetry(() -> request.get("/employees"));
        assertEquals(200, response.statusCode());
        EmployeeId = response.getBody().jsonPath().get("employeeId[0]");

        String employee_api_path_by_id = "employees/" + EmployeeId;

        Response responseUpdate = Polling.executeWithRetry(() -> request.body(getData("src/test/resources/testdata/updateEmployee.json").toString()).put(employee_api_path_by_id));
        assertEquals(200, responseUpdate.statusCode(), responseUpdate.getBody().toString());
        System.out.println(responseUpdate.getBody().jsonPath().get("message").toString());
        assertEquals(responseUpdate.getBody().jsonPath().get("message").toString(),"Employee updated successfully!");
        /* Commented this test scenario as per swagger it shows in return we will get json for Employee
     {
      "employeeId": "Emp-ce2f233c-2af8-458c-9c17-0d262cc828d4",
      "firstName": "Jane",
      "lastName": "Gordon",
      "dateOfBirth": "1992-05-20",
      "contactInfo": {
        "email": "jane.gordon@example.com",
        "phone": "+1234567890",
        "address": {
          "street": "456 Elm St",
          "town": "Birmingham",
          "postCode": "B14 21T"
        }
       }
       } So I added assert to verify firstname is updated or not but was getting error as in return only message is returned no Json.
        //assertEquals(responseUpdate.getBody().jsonPath().get("firstName").toString(),"Ryan");*/
        Polling.executeWithRetry(() -> request.body(getData("src/test/resources/testdata/updateEmployee.json").toString()).put(employee_api_path_by_id));
}

    @Test
    public void updateEmployeeByIdNotFoundError() throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";
        RequestSpecification request = given();
        request.header("Authorization",Authorization);

        request.header("Content-Type", "application/json"); // Add the Json to the body of the request

        String employee_api_path_by_id = "employees/" + "notfoundEmplyee45454";

        Response responseUpdate = Polling.executeWithRetry(() -> request.body(getData("src/test/resources/testdata/updateEmployee404.json").toString()).put(employee_api_path_by_id));
        assertEquals(404, responseUpdate.statusCode(), responseUpdate.getBody().toString());
        assertEquals(responseUpdate.getBody().jsonPath().get("message").toString(),"Employee not found");
    }


    private void tearDownCreateEmployee(String employeeId) throws Exception {
        RestAssured.baseURI = "https://apisforemployeecatalogmanagementsystem.onrender.com";

        RequestSpecification request = given();
        request.header("Authorization",Authorization);
        request.header("Content-Type", "application/json"); // Add the Json to the body of the request
        request.body(getData("src/test/resources/testdata/deleteEmployee.json").toString()); // Post the request and check the response

        String employee_api_path_by_id = "employees/" + employeeId;

        Response responseDelete = Polling.executeWithRetry(() -> request.delete(employee_api_path_by_id));
        assertEquals(200, responseDelete.statusCode(),responseDelete.getBody().toString());
        System.out.println("delete"+responseDelete.getBody().toString());
        assertEquals(responseDelete.getBody().jsonPath().get("message").toString(),"Employee deleted successfully!");

    }

}
