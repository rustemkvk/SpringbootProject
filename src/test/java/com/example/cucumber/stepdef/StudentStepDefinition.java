package com.example.cucumber.stepdef;


import com.example.dto.StudentDTO;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.time.LocalDate;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;


public class StudentStepDefinition {


    private StudentDTO newStudentInfos;
    private StudentDTO updatedStudentInfo;
    private ResponseEntity<StudentDTO> response;
    private TestRestTemplate restTemplate;
    private RequestHeaderHelper headers;


    public StudentStepDefinition(TestRestTemplate restTemplate, RequestHeaderHelper headers) {
        this.restTemplate = restTemplate;
        this.headers = headers;
    }

    @Given("a new student has the following details:")
    public void aNewStudentHasTheFollowingDetails(DataTable dataTable) {
        Map<String, String> studentDetails = dataTable.asMap(String.class, String.class);
        newStudentInfos = StudentDTO.builder()
                .name(studentDetails.get("name"))
                .surname(studentDetails.get("surname"))
                .department(studentDetails.get("department"))
                .email(studentDetails.get("email"))
                .dateOfBirth(LocalDate.parse(studentDetails.get("dateOfBirth")))
                .build();
    }

    @When("the student is registered")
    public void theStudentIsRegistered() {
        HttpEntity<StudentDTO> request = new HttpEntity<>(newStudentInfos, headers.createHeaders());
        response = restTemplate.postForEntity("/api/students", request, StudentDTO.class);
    }

    @Then("the student should be registered successfully")
    public void theStudentShouldBeRegisteredSuccessfully() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Then("the registered student should have name {string}")
    public void theRegisteredStudentShouldHaveName(String expectedName) {
        assertThat(response.getBody().getName()).isEqualTo(expectedName);
    }

    @Then("the registered student should have surname {string}")
    public void theRegisteredStudentShouldHaveSurname(String expectedSurname) {
        assertThat(response.getBody().getSurname()).isEqualTo(expectedSurname);
    }

    @Then("the registered student should have email {string}")
    public void theRegisteredStudentShouldHaveEmail(String expectedEmail) {
        assert response.getBody() != null;
        assertThat(response.getBody().getEmail()).isEqualTo(expectedEmail);
    }

    @When("the registered student is searched for")
    public void theRegisteredStudentIsSearchedFor() {
        HttpEntity<StudentDTO> request = new HttpEntity<>(newStudentInfos, headers.createHeaders());
        response = restTemplate.getForEntity("/api/students/" + response.getBody().getId(), StudentDTO.class);
    }

    @Then("the student should be found")
    public void theStudentShouldBeFound() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Then("the student's details should match the registered information")
    public void theStudentsDetailsShouldMatchTheRegisteredInformation() {
        assertThat(response.getBody().getName()).isEqualTo(newStudentInfos.getName());
        assertThat(response.getBody().getSurname()).isEqualTo(newStudentInfos.getSurname());
        assertThat(response.getBody().getDepartment()).isEqualTo(newStudentInfos.getDepartment());
        assertThat(response.getBody().getEmail()).isEqualTo(newStudentInfos.getEmail());
        assertThat(response.getBody().getDateOfBirth()).isEqualTo(newStudentInfos.getDateOfBirth());
    }

    @And("the registered student should have department {string}")
    public void theRegisteredStudentShouldHaveDepartment(String expectedDepartment) {
        assertThat(response.getBody().getDepartment()).isEqualTo(expectedDepartment);
    }

    @When("the registered student has the following updated details:")
    public void theRegisteredStudentHasTheFollowingUpdatedDetails(DataTable dataTable) {
        Map<String, String> studentDetails = dataTable.asMap(String.class, String.class);
        updatedStudentInfo = StudentDTO.builder()
                .name(studentDetails.get("name"))
                .surname(studentDetails.get("surname"))
                .department(studentDetails.get("department"))
                .email(studentDetails.get("email"))
                .dateOfBirth(LocalDate.parse(studentDetails.get("dateOfBirth")))
                .build();
    }

    @And("the student's details are updated")
    public void theStudentSDetailsAreUpdated() {
        HttpEntity<StudentDTO> request = new HttpEntity<>(updatedStudentInfo, headers.createHeaders());
        response = restTemplate.exchange(
                "/api/students/" + response.getBody().getId(),
                HttpMethod.PUT,
                request,
                StudentDTO.class);
    }
    //since restTemplate.put returns void, eed to use restTemplate.exhange() method.

    @Then("the student's details should be updated successfully")
    public void theStudentSDetailsShouldBeUpdatedSuccessfully() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @And("the updated student should have name {string}")
    public void theUpdatedStudentShouldHaveName(String updatedName) {
        assertThat(response.getBody().getName()).isEqualTo(updatedName);
    }

    @And("the updated student should have surname {string}")
    public void theUpdatedStudentShouldHaveSurname(String updatedSurname) {
        assertThat(response.getBody().getSurname()).isEqualTo(updatedSurname);
    }

    @And("the updated student should have email {string}")
    public void theUpdatedStudentShouldHaveEmail(String updatedEmail) {
        assertThat(response.getBody().getEmail()).isEqualTo(updatedEmail);
    }

    @And("the updated student should have department {string}")
    public void theUpdatedStudentShouldHaveDepartment(String updatedDepartment) {
        assertThat(response.getBody().getDepartment()).isEqualTo(updatedDepartment);
    }

    @And("the registered student should have date of birth {string}")
    public void theRegisteredStudentShouldHaveDateOfBirth(String arg0) {
        assertThat(response.getBody().getDateOfBirth()).isEqualTo(LocalDate.parse(arg0));
    }
}
