package br.studyleague.api.controller;

import br.studyleague.api.TestUtils;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.Mapper;
import br.studyleague.api.repository.SubjectRepository;
import dtos.SubjectDTO;
import dtos.student.StudentDTO;
import dtos.student.StudyCycleDTO;
import dtos.student.StudyCycleEntryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudyCycleControllerTest {
    private long studentId;

    @Autowired
    private StudyCycleController studyCycleController;

    @Autowired
    private StudentController studentController;

    @Autowired
    private SubjectRepository subjectRepository;

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
    void shouldCreateStudyCycle() {
        List<StudyCycleEntryDTO> studyCycleEntries = createSampleStudyCycle();

        studyCycleController.setStudyCycle(studentId, studyCycleEntries);

        StudyCycleDTO studyCycleDTO = studyCycleController.getStudyCycle(studentId).getBody();

        assertNotNull(studyCycleDTO);
        assertEquals(3, studyCycleDTO.getEntries().size());
        assertEquals("Math", studyCycleDTO.getEntries().getFirst().getSubject().getName());
        assertEquals(60, studyCycleDTO.getEntries().getFirst().getDurationInMinutes());
    }

    @Test
    void shouldUpdateStudyCycle() {
        List<StudyCycleEntryDTO> studyCycleEntries = createSampleStudyCycle();
        studyCycleController.setStudyCycle(studentId, studyCycleEntries);

        StudyCycleDTO studyCycleDTO = studyCycleController.getStudyCycle(studentId).getBody();

        assertNotNull(studyCycleDTO);
        assertEquals(3, studyCycleDTO.getEntries().size());
        assertEquals("Math", studyCycleDTO.getEntries().getFirst().getSubject().getName());
        assertEquals(60, studyCycleDTO.getEntries().getFirst().getDurationInMinutes());


        List<StudyCycleEntryDTO> updatedStudyCycleEntries = createSampleStudyCycle(List.of(
                Map.of("Math", 120),
                Map.of("Physics", 240),
                Map.of("Chemistry", 360)
        ));
        studyCycleController.setStudyCycle(studentId, updatedStudyCycleEntries);

        StudyCycleDTO updatedStudyCycleDTO = studyCycleController.getStudyCycle(studentId).getBody();

        assertNotNull(updatedStudyCycleDTO);
        assertEquals(3, updatedStudyCycleDTO.getEntries().size());
        assertEquals("Math", updatedStudyCycleDTO.getEntries().getFirst().getSubject().getName());
        assertEquals(120, updatedStudyCycleDTO.getEntries().getFirst().getDurationInMinutes());
    }

    @Test
    void shouldNotCreateStudyCycleWhenStudentDoesNotExist() {
        List<StudyCycleEntryDTO> studyCycleEntries = createSampleStudyCycle();

        assertThrows(Exception.class, () -> studyCycleController.setStudyCycle(0L, studyCycleEntries));
    }

    @Test
    void shouldGetNextSubject() {
        List<StudyCycleEntryDTO> studyCycleEntries = createSampleStudyCycle();
        studyCycleController.setStudyCycle(studentId, studyCycleEntries);

        StudyCycleDTO studyCycleDTO = studyCycleController.getStudyCycle(studentId).getBody();

        assertNotNull(studyCycleDTO);
        assertEquals("Math", studyCycleDTO.getCurrentEntry().getSubject().getName());

        studyCycleController.nextSubject(studentId);

        StudyCycleDTO updatedStudyCycleDTO = studyCycleController.getStudyCycle(studentId).getBody();

        assertNotNull(updatedStudyCycleDTO);
        assertEquals("Physics", updatedStudyCycleDTO.getCurrentEntry().getSubject().getName());
    }

    @Test
    void shouldSetGoals() {
        List<StudyCycleEntryDTO> studyCycleEntries = createSampleStudyCycle();
        studyCycleController.setStudyCycle(studentId, studyCycleEntries);

        StudyCycleDTO studyCycleDTO = studyCycleController.getStudyCycle(studentId).getBody();

        assertNotNull(studyCycleDTO);
        assertEquals(0, studyCycleDTO.getWeeklyMinutesToStudy());

        studyCycleController.setGoals(studentId, 120);

        StudyCycleDTO updatedStudyCycleDTO = studyCycleController.getStudyCycle(studentId).getBody();

        assertNotNull(updatedStudyCycleDTO);
        assertEquals(120, updatedStudyCycleDTO.getWeeklyMinutesToStudy());
    }

    private List<StudyCycleEntryDTO> createSampleStudyCycle() {
        return createSampleStudyCycle(List.of(
                Map.of("Math", 60),
                Map.of("Physics", 120),
                Map.of("Chemistry", 180)
        ));
    }

    private List<StudyCycleEntryDTO> createSampleStudyCycle(List<Map<String, Integer>> subjects) {
        List<StudyCycleEntryDTO> studyCycleEntries = new ArrayList<>();

        for (Map<String, Integer> entry : subjects) {
            for (Map.Entry<String, Integer> subject : entry.entrySet()) {
                studyCycleEntries.add(createStudyCycleEntry(subject.getKey(), subject.getValue()));
            }
        }

        return studyCycleEntries;
    }

    private StudyCycleEntryDTO createStudyCycleEntry(String subjectName, int durationInMinutes) {
        StudyCycleEntryDTO studyCycleEntryDTO = new StudyCycleEntryDTO();

        SubjectDTO subjectDTO = createSubject(subjectName);

        studyCycleEntryDTO.setSubject(subjectDTO);
        studyCycleEntryDTO.setDurationInMinutes(durationInMinutes);

        return studyCycleEntryDTO;
    }

    private SubjectDTO createSubject(String subjectName) {
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setName(subjectName);

        Subject savedSubject = subjectRepository.save(Mapper.subjectFromDTO(subjectDTO));
        return Mapper.subjectToDTO(savedSubject);
    }
}