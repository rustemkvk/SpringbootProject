package com.example.system;

import com.example.SpringFullProjectApplication;
import com.example.controller_impl.StudentControllerImpl;
import com.example.dto.StudentDTO;
import com.example.entity.Student;
import com.example.repositories.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = SpringFullProjectApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")

public class StudentSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentControllerImpl studentController;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "http://localhost:8080/api/students";

    @Test
    public void testGetAllStudents_Empty() {
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl, List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void testCreateStudent_Success() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("Ali")
                .surname("Yılmaz")
                .department("Bilgisayar Mühendisliği")
                .email("ali.yilmaz@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<StudentDTO> response = studentController.createStudent(studentDTO);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Ali");
        assertThat(response.getBody().getEmail()).isEqualTo("ali.yilmaz@example.com");

        Optional<Student> savedStudent = studentRepository.findByEmail(studentDTO.getEmail());
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();

        assertThat(id).isNotNull();
        ResponseEntity<List<StudentDTO>> getResponse = studentController.getAllStudents();
        assertThat(getResponse.getBody()).hasSize(1);
    }

    @Test
    public void testCreateStudent_ValidationFailure() throws Exception {
        StudentDTO studentDTO = StudentDTO.builder()
                .name(" ")
                .surname("Yılmaz")
                .department("Bilgisayar Mühendisliği")
                .email("invalid")
                .dateOfBirth(LocalDate.of(2026, 1, 1))
                .build();

     //   ResponseEntity<StudentDTO> response1 = studentController.createStudent(studentDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<StudentDTO> request = new HttpEntity<>(studentDTO, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Map<String, String> errors = objectMapper.readValue(response.getBody(), Map.class);
        assertThat(errors).containsKey("name");
        assertThat(errors.get("name")).contains("must not be blank");
        assertThat(errors).containsKey("email");
        assertThat(errors.get("email")).contains("must be a well-formed email address");
        assertThat(errors).containsKey("dateOfBirth");
        assertThat(errors.get("dateOfBirth")).contains("must be a past date");

        ResponseEntity<List> getResponse = restTemplate.getForEntity(baseUrl, List.class);
        assertThat(getResponse.getBody()).isEmpty();
//        ResponseEntity<StudentDTO> response = studentController.createStudent(studentDTO);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//
//        Map<String, String> errors = objectMapper.convertValue(response.getBody(), Map.class);
//        assertThat(errors).containsKey("name");
//        assertThat(errors).containsKey("email");
//        assertThat(errors).containsKey("dateOfBirth");
    }

    @Test
    public void testCreateStudent_DuplicateEmail() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("Ali")
                .surname("Yılmaz")
                .department("Bilgisayar Mühendisliği")
                .email("ali.yilmaz@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);

        StudentDTO studentDTO2 = StudentDTO.builder()
                .name("Alim")
                .surname("Yılmaz")
                .department("Bilgisayar Mühendisliği")
                .email(studentDTO.getEmail())
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, studentDTO2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("Duplicate email address");

        List<StudentDTO> students = restTemplate.getForEntity(baseUrl, List.class).getBody();
        assertThat(students).hasSize(1);
    }

    @Test
    public void testGetStudentById_Success() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("Alis")
                .surname("Yılmaz")
                .department("Bilgisayar Mühendisliği")
                .email("ali.yilmaz@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<StudentDTO> postResponse = studentController.createStudent(studentDTO);
    //    ResponseEntity<StudentDTO> postResponse = restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);

        Optional<Student> savedStudent = studentRepository.findByEmail("ali.yilmaz@example.com");
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();

        ResponseEntity<StudentDTO> getResponse = studentController.getStudentById(id);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Alis");
        assertThat(getResponse.getBody().getEmail()).isEqualTo("ali.yilmaz@example.com");
    }

    @Test
    public void testGetStudentById_NotFound() {

        StudentDTO studentDTO = StudentDTO.builder()
                .name("Alis")
                .surname("Yılmaz")
                .department("Bilgisayar Mühendisliği")
                .email("ali.yilmaz@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<StudentDTO> postResponse = studentController.createStudent(studentDTO);

        Optional<Student> savedStudent = studentRepository.findByEmail("ali.yilmaz@example.com");
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();

        ResponseEntity<StudentDTO> response = studentController.getStudentById(2L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateStudent_Success() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("Alim")
                .surname("Yılmaz")
                .department("Bilgisayar Mühendisliği")
                .email("ali.yilmaz@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();


        ResponseEntity<StudentDTO> postResponse = studentController.createStudent(studentDTO);
    //    ResponseEntity<StudentDTO> postResponse = restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<Student> savedStudent = studentRepository.findByEmail("ali.yilmaz@example.com");
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();


        StudentDTO updatedStudentDTO = StudentDTO.builder()
                .name("Ali")
                .surname("Günay")
                .department("Yazılım Mühendisliği")
                .email("ali.gunay@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<StudentDTO> putResponse = studentController.updateStudent(id, updatedStudentDTO);

//        ResponseEntity<StudentDTO> putResponse = restTemplate.exchange(
//                baseUrl + "/" + id,
//                HttpMethod.PUT,
//                new HttpEntity<>(updatedStudentDTO),
//                StudentDTO.class
//        );

        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().getName()).isEqualTo("Ali");
        assertThat(putResponse.getBody().getEmail()).isEqualTo("ali.gunay@example.com");

        ResponseEntity<StudentDTO> getResponse = studentController.getStudentById(id);
    //    ResponseEntity<StudentDTO> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, StudentDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Ali");
        assertThat(getResponse.getBody().getEmail()).isEqualTo("ali.gunay@example.com");
    }

    @Test
    public void testDeleteStudent_Success() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("Ali")
                .surname("Yılmaz")
                .department("Bilgisayar Mühendisliği")
                .email("ali.yilmaz@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<StudentDTO> postResponse = restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);

        Optional<Student> savedStudent = studentRepository.findByEmail("ali.yilmaz@example.com");
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();


        restTemplate.delete(baseUrl + "/" + id);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        List<StudentDTO> students = restTemplate.getForEntity(baseUrl, List.class).getBody();
        assertThat(students).isEmpty();
    }

    @Test
    public void getStudentPagination_Success() {
        for (int i = 1; i <= 15; i++) {
            StudentDTO studentDTO = StudentDTO.builder()
                    .name("Student" + i)
                    .surname("Surname" + i)
                    .department("Department" + i)
                    .email("student" + i + "@example.com")
                    .dateOfBirth(LocalDate.of(2000, 1, 1))
                    .build();
            restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);
        }

        String url = baseUrl + "?page=0&size=10";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

