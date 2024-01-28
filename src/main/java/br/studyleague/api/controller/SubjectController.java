package br.studyleague.api.controller;

import br.studyleague.api.controller.util.EndpointPrefixes;
import br.studyleague.api.model.goals.Goal;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.repository.StudentRepository;
import br.studyleague.api.repository.SubjectRepository;
import br.studyleague.dtos.GoalDTO;
import br.studyleague.dtos.SubjectDTO;
import br.studyleague.dtos.WriteStatisticDTO;
import br.studyleague.dtos.enums.StatisticType;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SubjectController {
    private final String ENDPOINT_PREFIX = EndpointPrefixes.SUBJECT;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public SubjectController(SubjectRepository subjectRepository, StudentRepository studentRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + ENDPOINT_PREFIX)
    public ResponseEntity<Subject> create(@PathVariable Long studentId, @RequestBody SubjectDTO subjectDto) {
        Subject subject = modelMapper.map(subjectDto, Subject.class);

        Student student = studentRepository.findById(studentId).orElseThrow();
        student.getSubjects().add(subject);

        studentRepository.save(student);

        return ResponseEntity.ok(subject);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    public ResponseEntity<List<Subject>> getById(@PathVariable Long studentId, @RequestParam(required = false) LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        if (date == null) {
            return ResponseEntity.ok(student.getSubjects());
        }

        List<Subject> subjects = student.getSchedule().getSubjects(date.getDayOfWeek());
        return ResponseEntity.ok(subjects);
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.GOALS)
    public ResponseEntity<Subject> setSubjectGoals(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody GoalDTO goalDto) {
        validateGoalRequest(goalDto);

        Student student = studentRepository.findById(studentId).orElseThrow();
        Subject subject = student.findSubjectById(subjectId);

        Goal goal = modelMapper.map(goalDto, Goal.class);

        subject.getGoals().setWeeklyGoal(goal.getStatisticType(), goal.getValue());
        student.syncGradesByDate(LocalDate.now());

        studentRepository.save(student);

        return ResponseEntity.ok(subject);
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.STATS)
    public ResponseEntity<Subject> setSubjectStats(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody WriteStatisticDTO statisticDto) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Subject subject = student.findSubjectById(subjectId);

        validateStatisticRequest(student, subject);

        LocalDate currentDate = LocalDate.now();
        subject.getStatisticManager().setStatisticValue(currentDate, statisticDto.getStatisticType(), statisticDto.getValue());
        student.syncStatisticsWithSubjects(currentDate);

        studentRepository.save(student);

        return ResponseEntity.ok(subject);
    }

    private void validateGoalRequest(GoalDTO goalDto) {
        if (goalDto.getStatisticType() == StatisticType.HOURS) {
            throw new IllegalArgumentException("You can't set the hours goal manually, since it's calculated based on the schedule.");
        }
    }

    private void validateStatisticRequest(Student student, Subject subject) {
        List<Subject> todaySubjects = student.getSchedule().getSubjects(LocalDate.now().getDayOfWeek());
        if (!todaySubjects.contains(subject)) {
            throw new IllegalArgumentException("You can't set the statistics for a subject that isn't in the schedule for today.");
        }
    }
}
