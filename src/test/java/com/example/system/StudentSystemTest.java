package com.example.system;

import com.example.SpringFullProjectApplication;
import com.example.controller_impl.StudentControllerImpl;
import com.example.dto.StudentDTO;
import com.example.entity.Student;
import com.example.repositories.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = SpringFullProjectApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    private StudentControllerImpl studentControllerImpl;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/students";
        studentRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
    }

    private StudentDTO buildStudentDTO() {
        return StudentDTO.builder()
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .department(faker.options().option(
                        "Chemistry",
                        "Physics",
                        "Mathematics",
                        "Software Engineering"
                ))
                .email(faker.internet().emailAddress())
                .dateOfBirth(
                        faker.date()
                                .birthday(16, 30)
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                )
                .build();
    }


    @Test
    public void testGetAllStudents_Empty() {
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl, List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void testCreateStudent_Success() {
        StudentDTO studentDTO = buildStudentDTO();

        ResponseEntity<StudentDTO> response = studentControllerImpl.createStudent(studentDTO);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo(studentDTO.getName());
        assertThat(response.getBody().getEmail()).isEqualTo(studentDTO.getEmail());

        Optional<Student> savedStudent = studentRepository.findByEmail(studentDTO.getEmail());
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();

        assertThat(id).isNotNull();
        ResponseEntity<List<StudentDTO>> getResponse = studentControllerImpl.getAllStudents();
        assertThat(getResponse.getBody()).hasSize(1);
    }

    @Test
    public void testCreateStudent_ValidationFailure() throws Exception {
        StudentDTO studentDTO = StudentDTO.builder()
                .name(" ")
                .surname(faker.name().lastName())
                .department("Maths")
                .email(faker.color().name())
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
        assertThat(errors.get("name")).contains("not be blank");
        assertThat(errors).containsKey("email");
        assertThat(errors.get("email")).contains("Email should be valid, e.g. example@gmail.com");

        ResponseEntity<List> getResponse = restTemplate.getForEntity(baseUrl, List.class);
        assertThat(getResponse.getBody()).isEmpty();
    }

    @Test
    public void testCreateStudent_DuplicateEmail() {
        StudentDTO studentDTO = buildStudentDTO();
        studentDTO.setEmail("james.silver@gmail.com");

        restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);

        StudentDTO studentDTO2 = buildStudentDTO();
        studentDTO2.setEmail("james.silver@gmail.com");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, studentDTO2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        List<StudentDTO> students = restTemplate.getForEntity(baseUrl, List.class).getBody();
        assertThat(students).hasSize(1);
    }

    @Test
    public void testGetStudentById_Success() {
        StudentDTO studentDTO = buildStudentDTO();

        studentControllerImpl.createStudent(studentDTO);
    //    ResponseEntity<StudentDTO> postResponse = restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);

        Optional<Student> savedStudent = studentRepository.findByEmail(studentDTO.getEmail());
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();

        ResponseEntity<StudentDTO> getResponse = studentControllerImpl.getStudentById(id);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo(studentDTO.getName());
        assertThat(getResponse.getBody().getEmail()).isEqualTo(studentDTO.getEmail());
    }

    @Test
    public void testGetStudentById_NotFound() {

        StudentDTO studentDTO = buildStudentDTO();

        ResponseEntity<StudentDTO> postResponse = studentControllerImpl.createStudent(studentDTO);

        Optional<Student> savedStudent = studentRepository.findByEmail(studentDTO.getEmail());
        assertThat(savedStudent).isPresent();
        Long id = savedStudent.get().getId();
        assertThat(id).isNotNull();

        ResponseEntity<StudentDTO> getResponse = studentControllerImpl.getStudentById(id);
        ResponseEntity<StudentDTO> response = studentControllerImpl.getStudentById((getResponse.getBody().getId()) +1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateStudent_Success() {
        StudentDTO studentDTO = buildStudentDTO();

        ResponseEntity<StudentDTO> postResponse = studentControllerImpl.createStudent(studentDTO);
    //    ResponseEntity<StudentDTO> postResponse = restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Optional<Student> savedStudent = studentRepository.findByEmail(studentDTO.getEmail());
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

        ResponseEntity<StudentDTO> putResponse = studentControllerImpl.updateStudent(id, updatedStudentDTO);

//        ResponseEntity<StudentDTO> putResponse = restTemplate.exchange(
//                baseUrl + "/" + id,
//                HttpMethod.PUT,
//                new HttpEntity<>(updatedStudentDTO),
//                StudentDTO.class
//        );

        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().getName()).isEqualTo("Ali");
        assertThat(putResponse.getBody().getEmail()).isEqualTo("ali.gunay@example.com");

        ResponseEntity<StudentDTO> getResponse = studentControllerImpl.getStudentById(id);
    //    ResponseEntity<StudentDTO> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, StudentDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Ali");
        assertThat(getResponse.getBody().getEmail()).isEqualTo("ali.gunay@example.com");
    }

    @Test
    public void testDeleteStudent_Success() {
        StudentDTO studentDTO = buildStudentDTO();

        ResponseEntity<StudentDTO> postResponse = restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);

        Optional<Student> savedStudent = studentRepository.findByEmail(studentDTO.getEmail());
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
        for (int i = 1; i <= 10; i++) {
            StudentDTO studentDTO = buildStudentDTO();
            restTemplate.postForEntity(baseUrl, studentDTO, StudentDTO.class);
        }

        String url = baseUrl + "?page=0&size=10";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
