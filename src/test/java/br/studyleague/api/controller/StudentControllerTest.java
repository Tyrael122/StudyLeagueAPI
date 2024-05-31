package br.studyleague.api.controller;

import br.studyleague.api.TestUtils;
import br.studyleague.api.controller.util.datetime.DateTimeUtils;
import dtos.student.StudentDTO;
import dtos.student.StudentStatisticsDTO;
import enums.StudySchedulingMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StudentControllerTest {
    private static long studentId;

    @Autowired
    private StudentController studentController;

    @BeforeEach
    void createSampleStudentWithId() {
        StudentDTO studentDTO = studentController.createStudent(TestUtils.createSampleStudent()).getBody();
        if (studentDTO != null) {
            studentId = studentDTO.getId();
        } else {
            fail("Failed to create student");
        }
    }

    @Test
    public void shouldReturnNonNullStudent() {
        StudentDTO student = studentController.getById(studentId).getBody();

        assertNotNull(student);
    }

    @Test
    public void shouldGetStatsFromJustCreatedStudentWhenStudyCycleIsNull() {
        StudentStatisticsDTO stats = studentController.getStats(studentId, DateTimeUtils.timezoneOffsettedNowDate()).getBody();

        assertNotNull(stats);
    }

    @Test
    public void shouldChangeStudySchedulingMethod() {
        studentController.changeScheduleMethod(studentId, StudySchedulingMethods.SCHEDULE);

        StudentDTO student = studentController.getById(studentId).getBody();
        assertNotNull(student);
        assertEquals(StudySchedulingMethods.SCHEDULE, student.getCurrentStudySchedulingMethod());

        studentController.changeScheduleMethod(studentId, StudySchedulingMethods.STUDYCYCLE);

        student = studentController.getById(studentId).getBody();
        assertNotNull(student);
        assertEquals(StudySchedulingMethods.STUDYCYCLE, student.getCurrentStudySchedulingMethod());
    }
}
