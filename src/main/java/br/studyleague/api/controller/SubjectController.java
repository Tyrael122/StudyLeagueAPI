package br.studyleague.api.controller;

import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.goals.Goal;
import br.studyleague.api.model.goals.SubjectGoals;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import br.studyleague.api.repository.StudentRepository;
import dtos.SubjectDTO;
import dtos.statistic.WriteStatisticDTO;
import dtos.student.goals.ReadGoalDTO;
import dtos.student.goals.WriteGoalDTO;
import enums.DateRangeType;
import enums.StatisticType;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.EndpointPrefixes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SubjectController {
    private final String ENDPOINT_PREFIX = EndpointPrefixes.SUBJECT;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public SubjectController(StudentRepository studentRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        this.studentRepository = studentRepository;
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + ENDPOINT_PREFIX)
    public void create(@PathVariable Long studentId, @RequestBody List<SubjectDTO> subjectDtos) {
        List<Subject> subjects = new ArrayList<>();
        for (SubjectDTO subjectDto : subjectDtos) {
            Subject subject = modelMapper.map(subjectDto, Subject.class);
            subjects.add(subject);
        }

        Student student = studentRepository.findById(studentId).orElseThrow();
        student.getSubjects().addAll(subjects);

        studentRepository.save(student);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(EndpointPrefixes.STUDENT_ID + ENDPOINT_PREFIX)
    public void deleteSubjects(@PathVariable Long studentId, @RequestBody List<SubjectDTO> subjectDtos) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        for (SubjectDTO subjectDto : subjectDtos) {
            Subject subject = student.findSubjectById(subjectDto.getId());
            student.getSubjects().remove(subject);
        }

        studentRepository.save(student);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    public ResponseEntity<List<SubjectDTO>> getStudentSubjectDtos(@PathVariable Long studentId, @RequestParam LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        List<SubjectDTO> subjectDtos = new ArrayList<>();
        for (Subject subject : student.getSubjects()) {
            subjectDtos.add(mapSubjectToDto(subject, date));
        }

        return ResponseEntity.ok(subjectDtos);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULED_SUBJECT)
    public ResponseEntity<List<SubjectDTO>> getScheduledSubjects(@PathVariable Long studentId, @RequestParam LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        Map<Subject, Float> subjectsWithDailyHourTarget = student.getSchedule().getSubjectsWithDailyHourTarget(date.getDayOfWeek());

        List<SubjectDTO> subjectDtos = new ArrayList<>();
        for (Map.Entry<Subject, Float> entrySet : subjectsWithDailyHourTarget.entrySet()) {
            SubjectDTO subjectDto = mapSubjectToDto(entrySet.getKey(), date);

            subjectDto.setHoursToStudyToday(entrySet.getValue());

            subjectDtos.add(subjectDto);
        }

        return ResponseEntity.ok(subjectDtos);
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.GOALS)
    public void setSubjectGoals(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody List<WriteGoalDTO> writeGoalDtos, @RequestParam DateRangeType dateRangeType) {
        for (WriteGoalDTO goalDto : writeGoalDtos) {
            validateGoalRequest(goalDto);
        }

        Student student = studentRepository.findById(studentId).orElseThrow();
        Subject subject = student.findSubjectById(subjectId);

        for (WriteGoalDTO writeGoalDto : writeGoalDtos) {
            Goal goal = modelMapper.map(writeGoalDto, Goal.class);

            setSubjectGoal(dateRangeType, subject, goal);
        }

        LocalDate currentDate = LocalDate.now();
        student.syncGradesByDate(currentDate);

        studentRepository.save(student);
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.STATS)
    public void setSubjectStats(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody List<WriteStatisticDTO> statisticDtos) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Subject subject = student.findSubjectById(subjectId);

        validateStatisticRequest(student, subject);

        LocalDate currentDate = LocalDate.now();

        for (WriteStatisticDTO statisticDto : statisticDtos) {
            subject.getStatisticManager().setStatisticValue(currentDate, statisticDto.getStatisticType(), statisticDto.getValue());
        }

        student.syncStatisticsWithSubjects(currentDate);

        studentRepository.save(student);
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
        switch (dateRangeType) {
            case WEEKLY -> subject.getGoals().setWeeklyGoal(goal.getStatisticType(), goal.getValue());
            case ALL_TIME -> subject.getGoals().setAllTimeGoal(goal.getStatisticType(), goal.getValue());
            case null, default ->
                    throw new IllegalArgumentException("Date range of type " + dateRangeType + " is not supported.");
        }
    }

    private SubjectDTO mapSubjectToDto(Subject subject, LocalDate date) {
        RawDataParser<Statistic> statisticParser = Statistic.parse(subject.getDailyStatistics());
        Statistic dailyStatistic = statisticParser.getDailyDataOrDefault(date);
        Statistic weeklyStatistic = statisticParser.getWeeklyData(date);
        Statistic allTimeStatistic = statisticParser.getAllTimeData();

        SubjectDTO subjectDto = modelMapper.map(subject, SubjectDTO.class);
        subjectDto.setDailyStatistic(Statistic.toReadDto(dailyStatistic));
        subjectDto.setWeeklyStatistic(Statistic.toReadDto(weeklyStatistic));
        subjectDto.setAllTimeStatistic(Statistic.toReadDto(allTimeStatistic));

        subjectDto.setWeeklyGoals(mapWeeklyGoalsToDto(subject.getGoals()));
        subjectDto.setAllTimeGoals(mapAllTimeGoalsToDto(subject.getGoals()));

        return subjectDto;
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
}
