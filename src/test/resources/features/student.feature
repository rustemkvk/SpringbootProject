Feature: Student API tests

  Background:
    Given a new student has the following details:
      | name        | James                   |
      | surname     | Smith                   |
      | department  | Computer Science        |
      | email       | james.smith@example.com |
      | dateOfBirth | 2000-01-01              |

  Scenario Outline: Register a new student successfully
    When the student is registered
    Then the student should be registered successfully
    And the registered student should have name "<name>"
    And the registered student should have surname "<surname>"
    And the registered student should have email "<email>"
    And the registered student should have department "<department>"
    And the registered student should have date of birth "<dob>"
    Examples:
      | name  | surname | department       | email                   | dob        |
      | James | Smith   | Computer Science | james.smith@example.com | 2000-01-01 |

  Scenario: Find a registered student
    When the student is registered
    And the registered student is searched for
    Then the student should be found
    And the student's details should match the registered information

  Scenario Outline: Update a registered student's details
    When the student is registered
    And the registered student has the following updated details:
      | name        | <updated_name>       |
      | surname     | <updated_surname>    |
      | department  | <updated_department> |
      | email       | <updated_email>      |
      | dateOfBirth | <updated_dob>        |
    And the student's details are updated
    Then the student's details should be updated successfully
    And the updated student should have name "<updated_name>"
    And the updated student should have surname "<updated_surname>"
    And the updated student should have email "<updated_email>"
    And the updated student should have department "<updated_department>"
    Examples:
      | updated_name | updated_surname | updated_department | updated_email           | updated_dob |
      | Jane         | Shannon         | Computer Science   | james.smith@example.com | 2000-02-01  |