package br.studyleague.api.controller;

import br.studyleague.api.controller.util.EndpointPrefixes;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.student.schedule.Schedule;
import br.studyleague.api.repository.ScheduleRepository;
import br.studyleague.api.repository.StudentRepository;
import br.studyleague.dtos.schedule.ScheduleDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class ScheduleController {
    private final String ENDPOINT_PREFIX = EndpointPrefixes.SCHEDULE;
    private final ModelMapper modelMapper;

    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;

    public ScheduleController(ModelMapper modelMapper, ScheduleRepository scheduleRepository, StudentRepository studentRepository) {
        this.modelMapper = modelMapper;

        this.scheduleRepository = scheduleRepository;
        this.studentRepository = studentRepository;
    }

    @PutMapping(EndpointPrefixes.STUDENT + "/{id}" + ENDPOINT_PREFIX)
    public ResponseEntity<Schedule> saveStudentSchedule(@PathVariable Long id, @RequestBody ScheduleDTO scheduleDto) {
        Student student = studentRepository.findById(id).orElseThrow();

        Schedule schedule = getNewStudentSchedule(scheduleDto, student);

//        Schedule previousSchedule = student.getSchedule();

        setStudentSchedule(student, schedule);

//        scheduleRepository.delete(previousSchedule);
        studentRepository.save(student);

        return ResponseEntity.ok(student.getSchedule());
    }

    @GetMapping(EndpointPrefixes.STUDENT + "/{id}" + ENDPOINT_PREFIX)
    public ResponseEntity<Schedule> getStudentSchedule(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElseThrow();

        return ResponseEntity.ok(student.getSchedule());
    }

    private Schedule getNewStudentSchedule(ScheduleDTO scheduleDto, Student student) {
        Schedule schedule = modelMapper.map(scheduleDto, Schedule.class);

        schedule.syncSubjectHourGoalsWithSchedule(student.getSubjects());
        return schedule;
    }

    private static void setStudentSchedule(Student student, Schedule schedule) {
        student.setSchedule(schedule);
        student.syncGradesByDate(LocalDate.now());
    }
}
