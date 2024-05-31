package br.studyleague.api.controller;

import br.studyleague.api.controller.util.datetime.DateTimeUtils;
import br.studyleague.api.model.Credential;
import br.studyleague.api.model.aggregabledata.grade.Grade;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.util.Mapper;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import br.studyleague.api.repository.StudentRepository;
import dtos.signin.CredentialDTO;
import dtos.signin.SignUpStudentData;
import dtos.student.StudentDTO;
import dtos.student.StudentStatisticsDTO;
import enums.StudySchedulingMethods;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import util.EndpointPrefixes;

import java.time.LocalDate;
import java.util.Optional;

import static br.studyleague.api.model.util.Mapper.statisticsToReadDto;

@RestController
public class StudentController {
    private final String ENDPOINT_PREFIX = EndpointPrefixes.STUDENT;

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping(EndpointPrefixes.LOGIN)
    public ResponseEntity<StudentDTO> login(@RequestBody CredentialDTO credentialDto) {
        Student student = studentRepository.findByCredential_Email(credentialDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas!"));

        boolean isPasswordValid = new BCryptPasswordEncoder().matches(credentialDto.getPassword(), student.getCredential().getPassword());
        if (!isPasswordValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas!");
        }

        return ResponseEntity.ok(Mapper.studentToDto(student));
    }

    @PostMapping(ENDPOINT_PREFIX)
    public ResponseEntity<StudentDTO> createStudent(@RequestBody SignUpStudentData signUpData) {
        Student student = Mapper.studentFromDto(signUpData.getStudent());
        Credential credential = encryptCredentialPassword(signUpData.getCredential());

        Optional<Student> matchedStudent = studentRepository.findByCredential_Email(credential.getEmail());
        if (matchedStudent.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado!");
        }

        student.setCredential(credential);

        student = studentRepository.save(student);

        return ResponseEntity.ok(Mapper.studentToDto(student));
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID)
    public ResponseEntity<StudentDTO> getById(@PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        return ResponseEntity.ok(Mapper.studentToDto(student));
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STATS)
    public ResponseEntity<StudentStatisticsDTO> getStats(@PathVariable Long studentId, @RequestParam LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        LocalDate offsettedDate = DateTimeUtils.studentTimezoneOffsettedDate(date);
        StudentStatisticsDTO studentStatsDto = calculateStudentStatistics(student, offsettedDate);

        return ResponseEntity.ok(studentStatsDto);
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.CHANGE_SCHEDULE_METHOD)
    public ResponseEntity<StudentDTO> changeScheduleMethod(@PathVariable Long studentId, @RequestParam StudySchedulingMethods newMethod) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        student.setCurrentStudySchedulingMethod(newMethod);
        student.syncGrades();

        studentRepository.save(student);

        return ResponseEntity.ok().build();
    }

    private Credential encryptCredentialPassword(CredentialDTO credentialDto) {
        Credential credential = Mapper.credentialFromDto(credentialDto);

        String encryptedPassword = new BCryptPasswordEncoder().encode(credential.getPassword());

        credential.setPassword(encryptedPassword);

        return credential;
    }

    private StudentStatisticsDTO calculateStudentStatistics(Student student, LocalDate date) {
        float dailyGrade = Grade.parse(student.getDailyGrades()).getDailyDataOrDefault(date).getGrade();

        RawDataParser<Grade> weeklyGradeParser = Grade.parse(student.getWeeklyGrades());
        float weeklyGrade = weeklyGradeParser.getWeeklyData(date).getGrade();
        float monthlyGrade = Grade.calculateMonthlyGrade(date, weeklyGradeParser);

        RawDataParser<Statistic> statisticParser = Statistic.parse(student.getDailyStatistics());
        Statistic dailyStatistic = statisticParser.getDailyDataOrDefault(date);
        Statistic weeklyStatistic = statisticParser.getWeeklyData(date);
        Statistic allTimeStatistic = statisticParser.getAllTimeData();

        StudentStatisticsDTO studentStatsDto = new StudentStatisticsDTO();
        studentStatsDto.setDailyGrade(dailyGrade);
        studentStatsDto.setWeeklyGrade(weeklyGrade);
        studentStatsDto.setMonthlyGrade(monthlyGrade);

        int hoursGoalsCompleted = student.getStudySchedulingMethod().calculateHoursGoalsCompleted(student, date);
        studentStatsDto.setHoursGoalsCompleted(hoursGoalsCompleted);

        studentStatsDto.setDailyStatistic(statisticsToReadDto(dailyStatistic));
        studentStatsDto.setWeeklyStatistic(statisticsToReadDto(weeklyStatistic));
        studentStatsDto.setAllTimeStatistic(statisticsToReadDto(allTimeStatistic));

        return studentStatsDto;
    }
}
