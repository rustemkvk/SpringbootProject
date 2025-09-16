package com.example.services;


import com.example.dto.StudentDTO;
import com.example.entity.Student;
import com.example.repositories.StudentRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private StudentDTO convertToDTO(Student student) {
        return StudentDTO.builder()
                .name(student.getName())
                .surname(student.getSurname())
                .department(student.getDepartment())
                .email(student.getEmail())
                .dateOfBirth(student.getDateOfBirth())
                .build();
    }

    private Student convertToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setSurname(dto.getSurname());
        student.setDepartment(dto.getDepartment());
        student.setEmail(dto.getEmail());
        student.setDateOfBirth(dto.getDateOfBirth());
        return student;
    }

    public StudentDTO saveStudent(StudentDTO studentDto) {
        Optional<Student> existingStudent = studentRepository.findByEmail(studentDto.getEmail());
        if (existingStudent.isPresent()) {
            throw new RuntimeException("Duplicate email address: " + studentDto.getEmail());
        }

        Student student = convertToEntity(studentDto);
        Student savedStudent = studentRepository.save(student);
        return convertToDTO(savedStudent);
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<StudentDTO> getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(studentDTO.getName());
                    existingStudent.setSurname(studentDTO.getSurname());
                    existingStudent.setDepartment(studentDTO.getDepartment());
                    existingStudent.setEmail(studentDTO.getEmail());
                    existingStudent.setDateOfBirth(studentDTO.getDateOfBirth());
                    Student updatedStudent = studentRepository.save(existingStudent);
                    return convertToDTO(updatedStudent);
                })
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Optional<StudentDTO> findByEmail(String email) {
        return studentRepository.findByEmail(email).map(this::convertToDTO);
    }
}
