package br.studyleague.api.controller;

import br.studyleague.api.TestUtils;
import dtos.signin.CredentialDTO;
import dtos.signin.SignUpStudentData;
import dtos.student.StudentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SignInTest {

    @Autowired
    private StudentController studentController;

    @Test
    void shouldLoginWhenRegistered() {
        StudentDTO studentDto = TestUtils.createStudentInfo();
        CredentialDTO credentialDto = TestUtils.createCredential();

        SignUpStudentData signUpStudent = TestUtils.createSignUpStudentData(credentialDto, studentDto);

        studentController.createStudent(signUpStudent);

        StudentDTO matchedStudent = studentController.login(credentialDto).getBody();
        assertNotNull(matchedStudent);

        assertEquals(studentDto.getName(), matchedStudent.getName());
        assertEquals(studentDto.getGoal(), matchedStudent.getGoal());
        assertEquals(studentDto.getStudyArea(), matchedStudent.getStudyArea());
    }

    @Test
    void shouldNotLoginWhenNotRegistered() {
        CredentialDTO credentialDto = TestUtils.createCredential();

        assertThrows(ResponseStatusException.class, () -> studentController.login(credentialDto));
    }

    @Test
    void shouldNotLoginWhenPasswordIsIncorrect() {
        StudentDTO studentDto = TestUtils.createStudentInfo();
        CredentialDTO credentialDto = TestUtils.createCredential();

        SignUpStudentData signUpStudent = TestUtils.createSignUpStudentData(credentialDto, studentDto);

        studentController.createStudent(signUpStudent);

        CredentialDTO wrongPasswordCredential = TestUtils.createCredential();
        wrongPasswordCredential.setPassword("wrongPassword");

        assertThrows(ResponseStatusException.class, () -> studentController.login(wrongPasswordCredential));
    }

    @Test
    void shouldNotLoginWhenEmailIsIncorrect() {
        StudentDTO studentDto = TestUtils.createStudentInfo();
        CredentialDTO credentialDto = TestUtils.createCredential();

        SignUpStudentData signUpStudent = TestUtils.createSignUpStudentData(credentialDto, studentDto);

        studentController.createStudent(signUpStudent);

        CredentialDTO wrongEmailCredential = TestUtils.createCredential();
        wrongEmailCredential.setEmail("wrongemail@gmail.com");

        assertThrows(ResponseStatusException.class, () -> studentController.login(wrongEmailCredential));
    }

    @Test
    void shouldCreateStudent() {
        StudentDTO studentDto = TestUtils.createStudentInfo();
        CredentialDTO credentialDto = TestUtils.createCredential();

        SignUpStudentData signUpStudent = TestUtils.createSignUpStudentData(credentialDto, studentDto);

        StudentDTO createdStudent = studentController.createStudent(signUpStudent).getBody();
        assertNotNull(createdStudent);

        assertEquals(studentDto.getName(), createdStudent.getName());
        assertEquals(studentDto.getGoal(), createdStudent.getGoal());
        assertEquals(studentDto.getStudyArea(), createdStudent.getStudyArea());
    }

    @Test
    void shouldNotCreateStudentWhenEmailIsAlreadyRegistered() {
        StudentDTO studentDto = TestUtils.createStudentInfo();
        CredentialDTO credentialDto = TestUtils.createCredential();

        SignUpStudentData signUpStudent = TestUtils.createSignUpStudentData(credentialDto, studentDto);

        studentController.createStudent(signUpStudent);

        assertThrows(ResponseStatusException.class, () -> studentController.createStudent(signUpStudent));
    }
}
