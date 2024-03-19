package br.studyleague.api.controller;

import br.studyleague.api.controller.util.datetime.DateTimeUtils;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.student.schedule.Schedule;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.repository.ScheduleRepository;
import br.studyleague.api.repository.StudentRepository;
import dtos.student.schedule.ScheduleDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.EndpointPrefixes;

import java.util.List;

@RestController
public class ScheduleController {
    private final String ENDPOINT_PREFIX = EndpointPrefixes.SCHEDULE;
    private final ModelMapper modelMapper;

    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;

    public ScheduleController(ModelMapper modelMapper, StudentRepository studentRepository, ScheduleRepository scheduleRepository) {
        this.modelMapper = modelMapper;

        this.studentRepository = studentRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + ENDPOINT_PREFIX)
    public ResponseEntity<ScheduleDTO> saveStudentSchedule(@PathVariable Long studentId, @RequestBody ScheduleDTO scheduleDto) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        scheduleRepository.delete(student.getSchedule());

        Schedule schedule = createNewStudentSchedule(scheduleDto, student.getSubjects());

        setStudentSchedule(student, schedule);

        studentRepository.save(student);

        return ResponseEntity.ok(mapScheduleToDto(student.getSchedule()));
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + ENDPOINT_PREFIX)
    public ResponseEntity<ScheduleDTO> getStudentSchedule(@PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        return ResponseEntity.ok(mapScheduleToDto(student.getSchedule()));
    }

    private Schedule createNewStudentSchedule(ScheduleDTO scheduleDto, List<Subject> studentSubjects) {
        Schedule schedule = modelMapper.map(scheduleDto, Schedule.class);
        schedule.syncSubjectHourGoalsWithSchedule(studentSubjects);

        return schedule;
    }

    private static void setStudentSchedule(Student student, Schedule schedule) {
        student.setSchedule(schedule);
        student.syncGradesByDate(DateTimeUtils.timezoneOffsettedNowDate());
    }

    private ScheduleDTO mapScheduleToDto(Schedule schedule) {
        return modelMapper.map(schedule, ScheduleDTO.class);
    }
}
