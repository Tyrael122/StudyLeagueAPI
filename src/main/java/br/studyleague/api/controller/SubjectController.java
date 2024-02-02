package br.studyleague.api.controller;

import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.goals.Goal;
import br.studyleague.api.model.goals.SubjectGoals;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import br.studyleague.api.repository.StudentRepository;
import br.studyleague.api.repository.SubjectRepository;
import dtos.SubjectDTO;
import dtos.statistic.WriteStatisticDTO;
import dtos.student.goals.ReadGoalDTO;
import dtos.student.goals.WriteGoalDTO;
import enums.DateRangeType;
import enums.StatisticType;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.EndpointPrefixes;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public ResponseEntity<List<SubjectDTO>> create(@PathVariable Long studentId, @RequestBody List<SubjectDTO> subjectDtos) {
        List<Subject> subjects = new ArrayList<>();
        for (SubjectDTO subjectDto : subjectDtos) {
            Subject subject = modelMapper.map(subjectDto, Subject.class);
            subjects.add(subject);
        }

        Student student = studentRepository.findById(studentId).orElseThrow();
        student.getSubjects().addAll(subjects);

        studentRepository.save(student);

        return ResponseEntity.ok(subjectDtos);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    public ResponseEntity<List<SubjectDTO>> getStudentSubjects(@PathVariable Long studentId, @RequestParam LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        return ResponseEntity.ok(mapSubjectsToDto(student.getSubjects(), date));
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULED_SUBJECT)
    public ResponseEntity<List<SubjectDTO>> getScheduledSubjects(@PathVariable Long studentId, @RequestParam LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        List<Subject> subjects = student.getSchedule().getSubjects(date.getDayOfWeek());

        return ResponseEntity.ok(mapSubjectsToDto(subjects, date));
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.GOALS)
    public ResponseEntity<SubjectDTO> setSubjectGoals(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody List<WriteGoalDTO> writeGoalDtos, @RequestParam DateRangeType dateRangeType) {
        for (WriteGoalDTO goalDto : writeGoalDtos) {
            validateGoalRequest(goalDto);
        }

        Student student = studentRepository.findById(studentId).orElseThrow();
        Subject subject = student.findSubjectById(subjectId);

        for (WriteGoalDTO writeGoalDto : writeGoalDtos) {
            Goal goal = modelMapper.map(writeGoalDto, Goal.class);

            setSubjectGoal(dateRangeType, subject, goal);
        }

        student.syncGradesByDate(LocalDate.now());

        studentRepository.save(student);

        return ResponseEntity.ok(mapSubjectToDto(subject));
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.STATS)
    public ResponseEntity<SubjectDTO> setSubjectStats(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody List<WriteStatisticDTO> statisticDtos) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Subject subject = student.findSubjectById(subjectId);

        validateStatisticRequest(student, subject);

        LocalDate currentDate = LocalDate.now();

        for (WriteStatisticDTO statisticDto : statisticDtos) {
            subject.getStatisticManager().setStatisticValue(currentDate, statisticDto.getStatisticType(), statisticDto.getValue());
        }

        student.syncStatisticsWithSubjects(currentDate);

        studentRepository.save(student);

        return ResponseEntity.ok(mapSubjectToDto(subject));
    }

    private void validateGoalRequest(WriteGoalDTO writeGoalDto) {
        if (writeGoalDto.getStatisticType() == StatisticType.HOURS) {
            throw new IllegalArgumentException("You can't set the hours goal manually, since it's calculated based on the schedule.");
        }
    }

    private void validateStatisticRequest(Student student, Subject subject) {
        List<Subject> todaySubjects = student.getSchedule().getSubjects(LocalDate.now().getDayOfWeek());
        if (!todaySubjects.contains(subject)) {
            throw new IllegalArgumentException("You can't set the statistics for a subject that isn't in the schedule for today.");
        }
    }

    private static void setSubjectGoal(DateRangeType dateRangeType, Subject subject, Goal goal) {
        if (dateRangeType == DateRangeType.WEEKLY) {
            subject.getGoals().setWeeklyGoal(goal.getStatisticType(), goal.getValue());
        } else if (dateRangeType == DateRangeType.ALL_TIME) {
            subject.getGoals().setAllTimeGoal(goal.getStatisticType(), goal.getValue());
        } else {
            throw new IllegalArgumentException("Date range of type " + dateRangeType + " is not supported.");
        }
    }

    private List<SubjectDTO> mapSubjectsToDto(List<Subject> subjects, LocalDate date) {
        List<SubjectDTO> subjectDtos = new ArrayList<>();
        for (Subject subject : subjects) {
            RawDataParser<Statistic> statisticParser = Statistic.parse(subject.getDailyStatistics());
            Statistic dailyStatistic = statisticParser.getDailyDataOrDefault(date);
            Statistic weeklyStatistic = statisticParser.getWeeklyData(date);
            Statistic allTimeStatistic = statisticParser.getAllTimeData();

            SubjectDTO subjectDto = mapSubjectToDto(subject);
            subjectDto.setDailyStatistic(Statistic.toReadDto(dailyStatistic));
            subjectDto.setWeeklyStatistic(Statistic.toReadDto(weeklyStatistic));
            subjectDto.setAllTimeStatistic(Statistic.toReadDto(allTimeStatistic));

            subjectDto.setWeeklyGoals(mapWeeklyGoalsToDto(subject.getGoals()));
            subjectDto.setAllTimeGoals(mapAllTimeGoalsToDto(subject.getGoals()));

            subjectDtos.add(subjectDto);
        }

        return subjectDtos;
    }

    private ReadGoalDTO mapWeeklyGoalsToDto(SubjectGoals subjectGoals) {
        ReadGoalDTO readGoalDto = new ReadGoalDTO();

        for (StatisticType statisticType : StatisticType.values()) {
            float goalValue = subjectGoals.getWeeklyGoal(statisticType);

            setStatisticTypeToGoalDto(readGoalDto, statisticType, goalValue);
        }

        return readGoalDto;
    }

    private ReadGoalDTO mapAllTimeGoalsToDto(SubjectGoals subjectGoals) {
        ReadGoalDTO readGoalDto = new ReadGoalDTO();

        for (StatisticType statisticType : StatisticType.values()) {
            float goalValue = subjectGoals.getAllTimeGoal(statisticType);

            setStatisticTypeToGoalDto(readGoalDto, statisticType, goalValue);
        }

        return readGoalDto;
    }

    private void setStatisticTypeToGoalDto(ReadGoalDTO readGoalDto, StatisticType statisticType, float goalValue) {
        switch (statisticType) {
            case HOURS -> readGoalDto.setHours(goalValue);
            case QUESTIONS -> readGoalDto.setQuestions((int) goalValue);
            case REVIEWS -> readGoalDto.setReviews((int) goalValue);
        }
    }

    private SubjectDTO mapSubjectToDto(Subject subject) {
        return modelMapper.map(subject, SubjectDTO.class);
    }
}
