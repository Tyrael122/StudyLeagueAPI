package br.studyleague.api.controller;

import br.studyleague.api.controller.util.EndpointPrefixes;
import br.studyleague.api.model.aggregabledata.grade.Grade;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import br.studyleague.api.repository.StudentRepository;
import br.studyleague.dtos.ReadStatisticDTO;
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
    public ResponseEntity<Student> create(@RequestBody StudentDTO studentDto) {
        Student student = modelMapper.map(studentDto, Student.class);

        studentRepository.save(student);

        return ResponseEntity.ok(student);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID)
    public ResponseEntity<StudentDTO> getById(@PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        StudentDTO studentDto = modelMapper.map(student, StudentDTO.class);

        return ResponseEntity.ok(studentDto);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STATS)
    public ResponseEntity<StudentStatisticsDTO> getStats(@PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        StudentStatisticsDTO studentStatsDto = calculateStudentStatistics(student);

        return ResponseEntity.ok(studentStatsDto);
    }

    private StudentStatisticsDTO calculateStudentStatistics(Student student) {
        LocalDate currentDate = LocalDate.now();

        float dailyGrade = Grade.parse(student.getDailyGrades()).getDailyDataOrDefault(currentDate).getGrade();

        RawDataParser<Grade> weeklyGradeParser = Grade.parse(student.getWeeklyGrades());
        float weeklyGrade = weeklyGradeParser.getWeeklyData(currentDate).getGrade();
        float monthlyGrade = weeklyGradeParser.getMonthlyData(currentDate).getGrade();

        RawDataParser<Statistic> statisticParser = Statistic.parse(student.getDailyStatistics());
        Statistic dailyStatistic = statisticParser.getDailyDataOrDefault(currentDate);
        Statistic weeklyStatistic = statisticParser.getWeeklyData(currentDate);
        Statistic allTimeStatistic = statisticParser.getAllTimeData();

        StudentStatisticsDTO studentStatsDto = new StudentStatisticsDTO();
        studentStatsDto.setDailyGrade(dailyGrade);
        studentStatsDto.setWeeklyGrade(weeklyGrade);
        studentStatsDto.setMonthlyGrade(monthlyGrade);

        studentStatsDto.setDailyStatistic(mapToReadStatisticDTO(dailyStatistic));
        studentStatsDto.setWeeklyStatistic(mapToReadStatisticDTO(weeklyStatistic));
        studentStatsDto.setAllTimeStatistic(mapToReadStatisticDTO(allTimeStatistic));

        return studentStatsDto;
    }

    private ReadStatisticDTO mapToReadStatisticDTO(Statistic statistic) {
        return modelMapper.map(statistic, ReadStatisticDTO.class);
    }
}
