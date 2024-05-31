package br.studyleague.api.controller;

import br.studyleague.api.model.scheduling.studycycle.StudyCycle;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.util.Mapper;
import br.studyleague.api.repository.StudentRepository;
import br.studyleague.api.repository.StudyCycleRepository;
import dtos.student.StudyCycleDTO;
import dtos.student.StudyCycleEntryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.EndpointPrefixes;

import java.util.List;

@RestController
public class StudyCycleController {

    private final StudentRepository studentRepository;
    private final StudyCycleRepository studyCycleRepository;

    public StudyCycleController(StudentRepository studentRepository, StudyCycleRepository studyCycleRepository) {
        this.studentRepository = studentRepository;
        this.studyCycleRepository = studyCycleRepository;
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STUDY_CYCLE)
    public void setStudyCycle(@PathVariable Long studentId, @RequestBody List<StudyCycleEntryDTO> studyCycleEntries) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        StudyCycle newStudyCycle = new StudyCycle();

        if (student.getStudyCycle() != null) {
            newStudyCycle.setWeeklyMinutesToStudy(student.getStudyCycle().getWeeklyMinutesToStudy());

            deleteOldStudyCycle(student);
        }

        newStudyCycle.setEntries(Mapper.studyCycleEntriesFromDTOs(studyCycleEntries));
        student.setStudyCycle(newStudyCycle);

        student.syncGrades();

        studentRepository.save(student);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STUDY_CYCLE)
    public ResponseEntity<StudyCycleDTO> getStudyCycle(@PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        return ResponseEntity.ok(Mapper.studyCycleToDTO(student.getStudyCycle()));
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STUDY_CYCLE_NEXT)
    public void nextSubject(@PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        student.getStudyCycle().nextEntry();
        studentRepository.save(student);
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STUDY_CYCLE + EndpointPrefixes.GOALS)
    public void setGoals(@PathVariable Long studentId, @RequestBody Integer weeklyMinuteGoal) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        student.getStudyCycle().setWeeklyMinutesToStudy(weeklyMinuteGoal);
        student.syncGrades();

        studentRepository.save(student);
    }

    private void deleteOldStudyCycle(Student student) {
        StudyCycle currentStudyCycle = student.getStudyCycle();

        student.setStudyCycle(null);
        studentRepository.save(student);

        studyCycleRepository.delete(currentStudyCycle);
    }
}
