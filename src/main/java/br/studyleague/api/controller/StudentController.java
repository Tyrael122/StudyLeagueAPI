package br.studyleague.api.controller;

import br.studyleague.api.controller.util.EndpointPrefixes;
import br.studyleague.api.model.aggregabledata.grade.Grade;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import br.studyleague.api.repository.StudentRepository;
import br.studyleague.dtos.StudentDTO;
import br.studyleague.dtos.StudentStatisticsDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class StudentController {
    private final String ENDPOINT_PREFIX = EndpointPrefixes.STUDENT;
    private final ModelMapper modelMapper;

    private final StudentRepository studentRepository;

    public StudentController(ModelMapper modelMapper, StudentRepository studentRepository) {
        this.modelMapper = modelMapper;
        this.studentRepository = studentRepository;
    }

    @PostMapping(ENDPOINT_PREFIX)
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO studentDto) {
        Student student = modelMapper.map(studentDto, Student.class);

        studentRepository.save(student);

        return ResponseEntity.ok(mapStudentToDto(student));
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID)
    public ResponseEntity<StudentDTO> getById(@PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        return ResponseEntity.ok(mapStudentToDto(student));
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STATS)
    public ResponseEntity<StudentStatisticsDTO> getStats(@PathVariable Long studentId, @RequestParam LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        StudentStatisticsDTO studentStatsDto = calculateStudentStatistics(student, date);

        return ResponseEntity.ok(studentStatsDto);
    }

    private StudentStatisticsDTO calculateStudentStatistics(Student student, LocalDate date) {
        float dailyGrade = Grade.parse(student.getDailyGrades()).getDailyDataOrDefault(date).getGrade();

        RawDataParser<Grade> weeklyGradeParser = Grade.parse(student.getWeeklyGrades());
        float weeklyGrade = weeklyGradeParser.getWeeklyData(date).getGrade();
        float monthlyGrade = weeklyGradeParser.getMonthlyData(date).getGrade();

        RawDataParser<Statistic> statisticParser = Statistic.parse(student.getDailyStatistics());
        Statistic dailyStatistic = statisticParser.getDailyDataOrDefault(date);
        Statistic weeklyStatistic = statisticParser.getWeeklyData(date);
        Statistic allTimeStatistic = statisticParser.getAllTimeData();

        StudentStatisticsDTO studentStatsDto = new StudentStatisticsDTO();
        studentStatsDto.setDailyGrade(dailyGrade);
        studentStatsDto.setWeeklyGrade(weeklyGrade);
        studentStatsDto.setMonthlyGrade(monthlyGrade);

        studentStatsDto.setDailyStatistic(Statistic.toReadDto(dailyStatistic));
        studentStatsDto.setWeeklyStatistic(Statistic.toReadDto(weeklyStatistic));
        studentStatsDto.setAllTimeStatistic(Statistic.toReadDto(allTimeStatistic));

        return studentStatsDto;
    }

    private StudentDTO mapStudentToDto(Student student) {
        return modelMapper.map(student, StudentDTO.class);
    }
}
