package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookitUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DB_Util;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.it.Ma;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {

    String token;
    Response response;
    String emailGlobal;


    @Given("I logged Bookit api as a {string}")
    public void i_logged_bookit_api_as_a(String role) { //role comes from feature file and will be assigned to String role

        token = BookitUtils.generateTokenByRole(role);
        System.out.println("token = " + token);

        Map<String, String> credentialsMap = BookitUtils.returnCredentials(role);
        emailGlobal = credentialsMap.get("email");
    }
    @When("I sent get request to {string} endpoint")
    public void i_sent_get_request_to_endpoint(String endpoint) { //this endpoint comes from feature file
        response = given().accept(ContentType.JSON)
                .header("Authorization", token)
                .when().get(Environment.BASE_URL+ endpoint);
    }
    @Then("status code should be {int}")
    public void status_code_should_be(int expectedStatusCode) {

        System.out.println("response.statusCode() = " + response.statusCode());

        //verify status code is matching with the feature file
        Assert.assertEquals(expectedStatusCode, response.statusCode());

    }
    @Then("content type is {string}")
    public void content_type_is(String expectedContentType) {
        System.out.println("response.contentType() = " + response.contentType());
        Assert.assertEquals(expectedContentType, response.contentType());
    }
    @Then("role is {string}")
    public void role_is(String expectedRole) {
        response.prettyPrint();

        String actualRole = response.path("role");

        Assert.assertEquals(expectedRole,actualRole);

    }

    @Then("the information about current user from api and database should match")
    public void the_information_about_current_user_from_api_and_database_should_match() {

        response.prettyPrint();

        //GET DATA FROM API
        JsonPath jsonPath = response.jsonPath();

        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        //GET DATA FROM DATABASE
        //first we need to create a connection with DB which is handled by custom hooks class
        String query = "select firstname, lastname, role from users\n" +
                "where email ='"+emailGlobal+"'";

        //run query using runQuery method from DB_Util
        DB_Util.runQuery(query);

        //get result to map
        Map<String, String> dbMap = DB_Util.getRowMap(1);
        System.out.println("dbMap = " + dbMap);

        String expectedFirstName = dbMap.get("firstname");
        String expectedLastName = dbMap.get("lastname");
        String expectedRole = dbMap.get("role");

        //COMPARE API VS DB
        Assert.assertEquals(expectedFirstName,actualFirstName);
        Assert.assertEquals(expectedLastName, actualLastName);
        Assert.assertEquals(expectedRole,actualRole);

    }

    @Then("UI,API and Database user information must be match")
    public void ui_API_And_Database_User_Information_Must_Be_Match() {

        response.prettyPrint();
        //GET DATA FROM API
        JsonPath jsonPath = response.jsonPath();
        /*
        {
            "id": 17381,
            "firstName": "Raymond",
            "lastName": "Reddington",
            "role": "student-team-member"
        }
         */
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        //GET DATA FROM DATABASE
        //first we need to create database connection which will handle by custom hooks
        String query = "select firstname,lastname,role from users\n" +
                "where email ='"+emailGlobal+"'";
        //run your query
        DB_Util.runQuery(query);

        //get the result to map
        Map<String, String> dbMap = DB_Util.getRowMap(1);
        System.out.println("dbMap = " + dbMap);

        String expectedFirstName = dbMap.get("firstname");
        String expectedLastName = dbMap.get("lastname");
        String expectedRole = dbMap.get("role");

        //COMPARE API vs DB

        Assert.assertEquals(expectedFirstName,actualFirstName);
        Assert.assertEquals(expectedLastName,actualLastName);
        Assert.assertEquals(expectedRole,actualRole);

        //GET DATA FROM UI
        SelfPage selfPage = new SelfPage();

        String actualFullNameUI = selfPage.name.getText();
        String actualRoleUI = selfPage.role.getText();

        //UI vs DB
        String expectedFullName = expectedFirstName+" "+expectedLastName;

        Assert.assertEquals(actualFullNameUI, expectedFullName);
        Assert.assertEquals(actualRoleUI,expectedRole);


        //UI vs API
        String expectedNameFromAPI = actualFirstName+" "+actualLastName;

        Assert.assertEquals( expectedNameFromAPI, actualFullNameUI);
        Assert.assertEquals(actualRole, actualRoleUI);

    }

    @When("I send POST request {string} endpoint with following information")
    public void i_send_post_request_endpoint_with_following_information(String endpoint, Map<String,String> studentInfo) {

        given().accept(ContentType.JSON)
                .header("Authorization", token);
    }
    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {

    }
}
