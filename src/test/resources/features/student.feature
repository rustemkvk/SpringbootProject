Feature: Student API tests

  Scenario Outline: Record a student successfully
    Given a student recorded with name "<name>", surname "<surname>", department "<department>", email "<email>", and dateOfBirth "<dob>"
    When the request sent
    Then the response status should be OK
    And the response should contain name "<name>", surname "<surname>", email "<email>"
    And the id is not null
    Examples:
      | name | surname | department       | email                  | dob        |
      | Ali  | Yilmaz  | Computer Science | ali.yilmaz@example.com | 2000-01-01 |

  Scenario Outline: Get a student with id
    Given a student recorded with name "<name>", surname "<surname>", department "<department>", email "<email>", and dateOfBirth "<dob>"
    When the request sent
    When the student searched by id
    Then the id is not null
    And the response status should be OK
    And the student is in records
    Examples:
      | name | surname | department       | email                  | dob        |
      | Ali  | Yilmaz  | Computer Science | ali.yilmaz@example.com | 2000-01-01 |
