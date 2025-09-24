package com.example.cucumber.stepdef;


import com.example.controller_impl.StudentControllerImpl;
import com.example.dto.StudentDTO;
import com.example.entity.Student;
import com.example.repositories.StudentRepository;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class StudentStepDefinition {


    private StudentDTO studentDTO;
    private ResponseEntity<StudentDTO> response;
    private Optional<Student> savedStudent;
    //   @Autowired
    private final StudentControllerImpl studentController;
    //   @Autowired
    private final StudentRepository studentRepository;


    public StudentStepDefinition(StudentControllerImpl studentController, StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.studentController = studentController;
    }

    @Given("a student recorded with name {string}, surname {string}, department {string}, email {string}, and dateOfBirth {string}")
    public void createStudentPayload(String name, String surname, String department, String email, String dateOfBirth) {

        studentDTO = StudentDTO.builder()
                .name(name)
                .surname(surname)
                .department(department)
                .email(email)
                .dateOfBirth(LocalDate.parse(dateOfBirth))
                .build();
    }

    @When("the request sent")
    public void iSendAPOSTRequestTo() {
        response = studentController.createStudent(studentDTO);
    }

    @And("the response should contain name {string}, surname {string}, email {string}")
    public void theResponseShouldContain(String name, String surname, String email) {
        assertThat(response.getBody().getName()).isEqualTo(name);
        assertThat(response.getBody().getSurname()).isEqualTo(surname);
        assertThat(response.getBody().getEmail()).isEqualTo(email);
    }

    @And("the id is not null")
    public void theIdIsNotNull() {
        Optional<Student> savedStudent = studentRepository.findByEmail(studentDTO.getEmail());
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();

        ResponseEntity<List<StudentDTO>> getResponse = studentController.getAllStudents();
        assertThat(getResponse.getBody()).hasSize(1);
    }

    @When("the student searched by id")
    public void theStudentSearchedById() {
        savedStudent = studentRepository.findByEmail(studentDTO.getEmail());

    }

    @And("the student is in records")
    public void theStudentIsInRecords() {
        assertThat(savedStudent).isPresent();
    }

    @And("the response status should be OK")
    public void theResponseStatusShouldBeOK() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
