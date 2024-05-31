package br.studyleague.api;

import dtos.signin.CredentialDTO;
import dtos.signin.SignUpStudentData;
import dtos.student.StudentDTO;

public class TestUtils {

    public static SignUpStudentData createSampleStudent() {
        return createSignUpStudentData(createCredential(), createStudentInfo());
    }

    public static SignUpStudentData createSignUpStudentData(CredentialDTO credentialDto, StudentDTO studentDto) {
        SignUpStudentData signUpStudent = new SignUpStudentData();

        signUpStudent.setCredential(credentialDto);
        signUpStudent.setStudent(studentDto);

        return signUpStudent;
    }

    public static StudentDTO createStudentInfo() {
        StudentDTO studentDto = new StudentDTO();

        studentDto.setName("John");
        studentDto.setGoal("Pass the exam");
        studentDto.setStudyArea("Physics");
        studentDto.setEmail("example@gmail.com");

        return studentDto;
    }

    public static CredentialDTO createCredential() {
        CredentialDTO credentialDto = new CredentialDTO();

        credentialDto.setEmail("example@gmail.com");
        credentialDto.setPassword("password");

        return credentialDto;
    }
}
