package com.bookit.step_definitions;

import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;

public class EnvStepDefs {

    @Given("I get related environment information")
    public void i_get_related_environment_information() {
        System.out.println("Environment.URL = " + Environment.URL);
        System.out.println("Environment.BASE_URL = " + Environment.BASE_URL);
        System.out.println("Environment.TEACHER_EMAIL = " + Environment.TEACHER_EMAIL);
    }
}
