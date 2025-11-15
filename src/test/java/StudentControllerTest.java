
import com.example.controller_impl.StudentControllerImpl;
import com.example.dto.StudentDTO;

import com.example.services.StudentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(StudentControllerImpl.class)
@ContextConfiguration(classes = com.example.SpringFullProjectApplication.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentServiceImpl studentService;

    @Autowired
    private ObjectMapper objectMapper;

    Faker faker = new Faker();

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
                                .birthday(18, 30)
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                )
                .build();
    }

    @Test
    public void testCreateStudent_Success() throws Exception {
        // Arrange
        StudentDTO studentDTO = buildStudentDTO();

        when(studentService.saveStudent(any(StudentDTO.class)))
                .thenReturn(studentDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(studentDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(studentDTO.getSurname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.department").value(studentDTO.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(studentDTO.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth").value(studentDTO.getDateOfBirth().toString()));

        verify(studentService, times(1))
                .saveStudent(any(StudentDTO.class));
    }

    @Test
    public void testGetAllStudents_Success() throws Exception {
        // Arrange
        StudentDTO student1 = buildStudentDTO();
        StudentDTO student2 = buildStudentDTO();
        List<StudentDTO> students = Arrays.asList(student1, student2);
        when(studentService.getAllStudents())
                .thenReturn(students);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(student1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].surname").value(student1.getSurname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(student2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].surname").value(student2.getSurname()));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    public void testGetStudentById_Success() throws Exception {
        // Arrange
        StudentDTO studentDTO = buildStudentDTO();

        when(studentService.getStudentById(1L)).thenReturn(Optional.of(studentDTO));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(studentDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(studentDTO.getSurname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(studentDTO.getEmail()));

        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    public void testGetStudentById_NotFound() throws Exception {
        // Arrange
        when(studentService.getStudentById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    public void testUpdateStudent_Success() throws Exception {
        // Arrange
        StudentDTO updatedStudentDTO = buildStudentDTO();

        when(studentService.updateStudent(eq(1L), any(StudentDTO.class))).thenReturn(updatedStudentDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudentDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updatedStudentDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(updatedStudentDTO.getSurname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updatedStudentDTO.getEmail()));

        verify(studentService, times(1)).updateStudent(eq(1L), any(StudentDTO.class));
    }

    @Test
    public void testUpdateStudent_NotFound() throws Exception {
        // Arrange
        StudentDTO studentDTO = buildStudentDTO();

        when(studentService.updateStudent(eq(1L), any(StudentDTO.class)))
                .thenThrow(new RuntimeException("Student not found with id: 1"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(studentService, times(1)).updateStudent(eq(1L), any(StudentDTO.class));
    }

    @Test
    public void testDeleteStudent_Success() throws Exception {
        // Arrange
        doNothing().when(studentService).deleteStudent(1L);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(studentService, times(1)).deleteStudent(1L);
    }
}
