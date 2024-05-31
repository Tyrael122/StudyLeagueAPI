package br.studyleague.api.controller;

import br.studyleague.api.controller.util.datetime.DateTimeUtils;
import br.studyleague.api.model.goals.Goal;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.Mapper;
import br.studyleague.api.repository.StudentRepository;
import dtos.SubjectDTO;
import dtos.student.WriteGoalDTO;
import dtos.student.WriteStatisticDTO;
import enums.DateRangeType;
import enums.StatisticType;
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

    public SubjectController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + ENDPOINT_PREFIX)
    public void create(@PathVariable Long studentId, @RequestBody List<SubjectDTO> subjectDtos) {
        List<Subject> subjects = new ArrayList<>();
        for (SubjectDTO subjectDto : subjectDtos) {
            Subject subject = Mapper.subjectFromDTO(subjectDto);
            subjects.add(subject);
        }

        Student student = studentRepository.findById(studentId).orElseThrow();
        student.getSubjects().addAll(subjects);

        studentRepository.save(student);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(EndpointPrefixes.STUDENT_ID + ENDPOINT_PREFIX)
    public void deleteSubjects(@PathVariable Long studentId, @RequestBody List<SubjectDTO> subjectDtos) {
//        Student student = studentRepository.findById(studentId).orElseThrow();
//
//        for (SubjectDTO subjectDto : subjectDtos) {
//            Subject subject = student.findSubjectById(subjectDto.getId());
//
//            student.getSubjects().remove(subject);
//
//            for (StudyDay studyDay : student.getSchedule().getDays()) {
//                studyDay.getEntries().removeIf(entry -> entry.getSubject().equals(subject));
//            }
//
//            studentRepository.save(student);
//
//            subjectRepository.delete(subject);
//        }
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    public ResponseEntity<List<SubjectDTO>> getAllSubjects(@PathVariable Long studentId, @RequestParam LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        LocalDate offsettedDate = DateTimeUtils.studentTimezoneOffsettedDate(date);

        List<SubjectDTO> subjectDtos = new ArrayList<>();
        for (Subject subject : student.getSubjects()) {
            subjectDtos.add(Mapper.subjectToDTO(subject, offsettedDate));
        }

        return ResponseEntity.ok(subjectDtos);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULED_SUBJECT)
    public ResponseEntity<List<SubjectDTO>> getScheduledSubjects(@PathVariable Long studentId, @RequestParam LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        LocalDate offsettedDate = DateTimeUtils.studentTimezoneOffsettedDate(date);

        Map<Subject, Float> subjectsWithDailyHourTarget = student.getSchedule().getSubjectsWithDailyHourTarget(offsettedDate.getDayOfWeek());

        List<SubjectDTO> subjectDtos = new ArrayList<>();
        for (Map.Entry<Subject, Float> entrySet : subjectsWithDailyHourTarget.entrySet()) {
            SubjectDTO subjectDto = Mapper.subjectToDTO(entrySet.getKey(), offsettedDate);

            subjectDto.setHoursToStudyToday(entrySet.getValue());

            subjectDtos.add(subjectDto);
        }

        return ResponseEntity.ok(subjectDtos);
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.GOALS)
    public void setSubjectGoals(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody List<WriteGoalDTO> writeGoalDtos, @RequestParam DateRangeType dateRangeType) {
        if (writeGoalDtos.isEmpty()) {
            return;
        }

        for (WriteGoalDTO goalDto : writeGoalDtos) {
            validateGoalRequest(goalDto, dateRangeType);
        }

        Student student = studentRepository.findById(studentId).orElseThrow();
        Subject subject = student.findSubjectById(subjectId);

        for (WriteGoalDTO writeGoalDto : writeGoalDtos) {
            Goal goal = Mapper.goalFromDto(writeGoalDto);

            setSubjectGoal(dateRangeType, subject, goal);
        }

        student.syncGrades();

        studentRepository.save(student);
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.STATS)
    public void setSubjectStats(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody List<WriteStatisticDTO> statisticDtos) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Subject subject = student.findSubjectById(subjectId);

        validateStatisticRequest(student, subject);

        LocalDate currentDate = DateTimeUtils.timezoneOffsettedNowDate();

        for (WriteStatisticDTO statisticDto : statisticDtos) {
            subject.getAggregableData().getStatisticManager().setStatisticValue(currentDate, statisticDto.getStatisticType(), statisticDto.getValue());
        }

        student.syncStatisticsWithSubjects(currentDate);

        studentRepository.save(student);
    }

    private void validateGoalRequest(WriteGoalDTO writeGoalDto, DateRangeType dateRangeType) {
        if (writeGoalDto.getStatisticType() == StatisticType.HOURS) {
            throw new IllegalArgumentException("You can't set the hours goal manually, since it's calculated based on the schedule.");
        }

        if (dateRangeType == DateRangeType.ALL_TIME && writeGoalDto.getStatisticType() == StatisticType.REVIEWS) {
            throw new IllegalArgumentException("You can't set an all time goal for reviews.");
        }
    }

    private void validateStatisticRequest(Student student, Subject subject) {
        List<Subject> todaySubjects = student.getStudySchedulingMethod().getSubjects(DateTimeUtils.timezoneOffsettedNowDate().getDayOfWeek());
        if (!todaySubjects.contains(subject)) {
            throw new IllegalArgumentException("You can't set the statistics for a subject that isn't in the schedule for today. " +
                    "Trying to set statistics for subject " + subject.getName() + ".");
        }
    }

    private static void setSubjectGoal(DateRangeType dateRangeType, Subject subject, Goal goal) {
        switch (dateRangeType) {
            case WEEKLY -> subject.getGoals().setWeeklyGoal(goal.getStatisticType(), goal.getTarget());
            case ALL_TIME -> subject.getGoals().setAllTimeGoal(goal.getStatisticType(), goal.getTarget());
            case null, default ->
                    throw new IllegalArgumentException("Date range of type " + dateRangeType + " is not supported.");
        }
    }
}
