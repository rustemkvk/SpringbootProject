
import com.example.controller_impl.StudentControllerImpl;
import com.example.dto.StudentDTO;

import com.example.services.StudentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
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

    @Test
    public void testCreateStudent_Success() throws Exception {
        // Arrange
        StudentDTO studentDTO = StudentDTO.builder()
                .name("Ali")
                .surname("Yılmaz")
                .department("Kimya")
                .email("ali_yilmaz@gmail.com")
                .dateOfBirth(LocalDate.of(2001, 1, 1))
                .build();

        when(studentService.saveStudent(any(StudentDTO.class))).thenReturn(studentDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ali"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Yılmaz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.department").value("Kimya"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("ali_yilmaz@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth").value("2001-01-01"));

        verify(studentService, times(1)).saveStudent(any(StudentDTO.class));
    }

//    @Test
//    //fail
//    public void testCreateStudent_ValidationFailure() throws Exception {
//        // Arrange
//        StudentDTO studentDTO = StudentDTO.builder()
//                .name("") // Boş isim, validasyon hatası
//                .surname("Yılmaz")
//                .department("Kimya")
//                .email("invalid") // Geçersiz e-posta
//                .dateOfBirth(LocalDate.of(2026, 1, 1)) // Gelecek tarih
//                .build();
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/students")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(studentDTO)))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Name must not be blank"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("Email must be a valid email address"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth").value("Date of birth must be in the past"));
//
//        verify(studentService, never()).saveStudent(any(StudentDTO.class));
//    }

//    @Test
    //fail
//    public void testCreateStudent_DuplicateEmail() throws Exception {
//        // Arrange
//        StudentDTO studentDTO = StudentDTO.builder()
//                .name("Ali")
//                .surname("Yılmaz")
//                .department("Kimya")
//                .email("ali_yilmaz@gmail.com")
//                .dateOfBirth(LocalDate.of(2001, 1, 1))
//                .build();
//
//        when(studentService.saveStudent(any(StudentDTO.class)))
//                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate key value violates unique constraint"));
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/students")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(studentDTO)))
//                .andExpect(MockMvcResultMatchers.status().isConflict())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Duplicate email address"));
//
//        verify(studentService, times(1)).saveStudent(any(StudentDTO.class));
//    }

    @Test
    //pass
    public void testGetAllStudents_Success() throws Exception {
        // Arrange
        StudentDTO student1 = StudentDTO.builder()
                .name("Ali")
                .surname("Yılmaz")
                .department("Kimya")
                .email("ali_yilmaz@gmail.com")
                .dateOfBirth(LocalDate.of(2001, 1, 1))
                .build();

        StudentDTO student2 = StudentDTO.builder()
                .name("Veli")
                .surname("Demir")
                .department("Yazılım Mühendisliği")
                .email("veli.demir@example.com")
                .dateOfBirth(LocalDate.of(1999, 1, 1))
                .build();

        List<StudentDTO> students = Arrays.asList(student1, student2);
        when(studentService.getAllStudents()).thenReturn(students);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Ali"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].surname").value("Yılmaz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Veli"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].surname").value("Demir"));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    //pass
    public void testGetStudentById_Success() throws Exception {
        // Arrange
        StudentDTO studentDTO = StudentDTO.builder()
                .name("Ali")
                .surname("Yılmaz")
                .department("Kimya")
                .email("ali_yilmaz@gmail.com")
                .dateOfBirth(LocalDate.of(2001, 1, 1))
                .build();

        when(studentService.getStudentById(1L)).thenReturn(Optional.of(studentDTO));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ali"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Yılmaz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("ali_yilmaz@gmail.com"));

        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    //pass
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
        StudentDTO updatedStudentDTO = StudentDTO.builder()
                .name("Ali Günay")
                .surname("Günay")
                .department("Yazılım Mühendisliği")
                .email("ali.gunay@example.com")
                .dateOfBirth(LocalDate.of(2001, 1, 1))
                .build();

        when(studentService.updateStudent(eq(1L), any(StudentDTO.class))).thenReturn(updatedStudentDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudentDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ali Günay"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Günay"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("ali.gunay@example.com"));

        verify(studentService, times(1)).updateStudent(eq(1L), any(StudentDTO.class));
    }

    @Test
    public void testUpdateStudent_NotFound() throws Exception {
        // Arrange
        StudentDTO studentDTO = StudentDTO.builder()
                .name("Ali Günay")
                .surname("Günay")
                .department("Yazılım Mühendisliği")
                .email("ali.gunay@example.com")
                .dateOfBirth(LocalDate.of(2001, 1, 1))
                .build();

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
    //pass
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
