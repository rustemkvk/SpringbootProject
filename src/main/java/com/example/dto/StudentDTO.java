package com.example.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class StudentDTO {

    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private  String name;

    @NotBlank(message = "Surname cannot be blank")
    @Size(min = 1, max = 255, message = "Surname must be between 1 and 255 characters")
    private  String surname;

    @NotBlank(message = "Department cannot be blank")
    @Size(min = 1, max = 255, message = "Department must be between 1 and 255 characters")
    private  String department;

    @NotBlank(message = "Email cannot be blank")
    @Size(min = 1, max = 255, message = "Department must be between 1 and 255 characters")
    @Email(message = "Email should be valid, e.g. example@gmail.com")
    private  String email;

    @NotNull(message = "Date of birth cannot be null")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private  LocalDate dateOfBirth;

    public StudentDTO() {
    }

    // Private constructor for Builder, gets a Builder object
    private StudentDTO(Builder builder) {
        this.id = null;
        this.name = builder.name;
        this.surname = builder.surname;
        this.department = builder.department;
        this.email = builder.email;
        this.dateOfBirth = builder.dateOfBirth;
    }

    public StudentDTO(Long id, String name, String surname, String department, String email, LocalDate dateOfBirth) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.department = department;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public Long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    // Builder class
    public static class Builder {
        private String name;
        private String surname;
        private String department;
        private String email;
        private LocalDate dateOfBirth;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public StudentDTO build() {
            return new StudentDTO(this);
        }
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }



}
