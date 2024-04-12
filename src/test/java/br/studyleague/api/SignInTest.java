package br.studyleague.api;

import br.studyleague.api.controller.StudentController;
import dtos.signin.CredentialDTO;
import dtos.signin.SignUpStudentData;
import dtos.student.StudentDTO;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class SignInTest {
    private final static String EXAMPLE_EMAIL = "example@gmail.com";

    @Autowired
    private StudentController studentController;

    @Test
    void shouldLoginWhenRegistered() {
        StudentDTO studentDto = createStudentInfo();
        CredentialDTO credentialDto = createCredential();

        SignUpStudentData signUpStudent = createSignUpStudentData(credentialDto, studentDto);

        studentController.create(signUpStudent);

        StudentDTO matchedStudent = studentController.login(credentialDto).getBody();
        assertNotNull(matchedStudent);

        assertEquals(studentDto.getName(), matchedStudent.getName());
        assertEquals(studentDto.getGoal(), matchedStudent.getGoal());
        assertEquals(studentDto.getStudyArea(), matchedStudent.getStudyArea());
    }

    @Test
    void shouldNotLoginWhenNotRegistered() {
        CredentialDTO credentialDto = createCredential();

        assertThrows(ResponseStatusException.class, () -> studentController.login(credentialDto));
    }

    @Test
    void shouldNotLoginWhenPasswordIsIncorrect() {
        StudentDTO studentDto = createStudentInfo();
        CredentialDTO credentialDto = createCredential();

        SignUpStudentData signUpStudent = createSignUpStudentData(credentialDto, studentDto);

        studentController.create(signUpStudent);

        CredentialDTO wrongPasswordCredential = createCredential();
        wrongPasswordCredential.setPassword("wrongPassword");

        assertThrows(ResponseStatusException.class, () -> studentController.login(wrongPasswordCredential));
    }

    @Test
    void shouldNotLoginWhenEmailIsIncorrect() {
        StudentDTO studentDto = createStudentInfo();
        CredentialDTO credentialDto = createCredential();

        SignUpStudentData signUpStudent = createSignUpStudentData(credentialDto, studentDto);

        studentController.create(signUpStudent);

        CredentialDTO wrongEmailCredential = createCredential();
        wrongEmailCredential.setEmail("wrongemail@gmail.com");

        assertThrows(ResponseStatusException.class, () -> studentController.login(wrongEmailCredential));
    }

    @Test
    void shouldCreateStudent() {
        StudentDTO studentDto = createStudentInfo();
        CredentialDTO credentialDto = createCredential();

        SignUpStudentData signUpStudent = createSignUpStudentData(credentialDto, studentDto);

        StudentDTO createdStudent = studentController.create(signUpStudent).getBody();
        assertNotNull(createdStudent);

        assertEquals(studentDto.getName(), createdStudent.getName());
        assertEquals(studentDto.getGoal(), createdStudent.getGoal());
        assertEquals(studentDto.getStudyArea(), createdStudent.getStudyArea());
    }

    @NotNull
    private static SignUpStudentData createSignUpStudentData(CredentialDTO credentialDto, StudentDTO studentDto) {
        SignUpStudentData signUpStudent = new SignUpStudentData();

        signUpStudent.setCredential(credentialDto);
        signUpStudent.setStudent(studentDto);
        return signUpStudent;
    }

    @Test
    void shouldNotCreateStudentWhenEmailIsAlreadyRegistered() {
        StudentDTO studentDto = createStudentInfo();
        CredentialDTO credentialDto = createCredential();

        SignUpStudentData signUpStudent = createSignUpStudentData(credentialDto, studentDto);

        studentController.create(signUpStudent);

        assertThrows(ResponseStatusException.class, () -> studentController.create(signUpStudent));
    }

    private StudentDTO createStudentInfo() {
        StudentDTO studentDto = new StudentDTO();
        studentDto.setName("John");
        studentDto.setGoal("Pass the exam");
        studentDto.setStudyArea("Physics");
        studentDto.setEmail(EXAMPLE_EMAIL);

        return studentDto;
    }

    private CredentialDTO createCredential() {
        CredentialDTO credentialDto = new CredentialDTO();
        credentialDto.setEmail(EXAMPLE_EMAIL);
        credentialDto.setPassword("password");

        return credentialDto;
    }
}
