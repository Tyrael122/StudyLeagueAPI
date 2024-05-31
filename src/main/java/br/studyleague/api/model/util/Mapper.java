package br.studyleague.api.model.util;

import br.studyleague.api.controller.util.datetime.DateTimeUtils;
import br.studyleague.api.model.Credential;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.goals.Goal;
import br.studyleague.api.model.goals.SubjectGoals;
import br.studyleague.api.model.scheduling.schedule.Schedule;
import br.studyleague.api.model.scheduling.studycycle.StudyCycle;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.scheduling.studycycle.StudyCycleEntry;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import dtos.SubjectDTO;
import dtos.signin.CredentialDTO;
import dtos.student.*;
import dtos.student.ReadGoalDTO;
import dtos.student.ReadStatisticDTO;
import dtos.student.ScheduleDTO;
import dtos.student.StudentDTO;
import dtos.student.StudyCycleDTO;
import dtos.student.StudyCycleEntryDTO;
import enums.StatisticType;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;

public class Mapper {
    private static final ModelMapper modelMapper = buildMapper();

    private static ModelMapper buildMapper() {
        ModelMapper newMapper = new ModelMapper();

        newMapper.getConfiguration()
                .setSkipNullEnabled(true);

        return newMapper;
    }

    public static SubjectDTO subjectToDTO(Subject subject) {
        return subjectToDTO(subject, DateTimeUtils.timezoneOffsettedNowDate());
    }

    public static SubjectDTO subjectToDTO(Subject subject, LocalDate date) {
        RawDataParser<Statistic> statisticParser = Statistic.parse(subject.getDailyStatistics());
        Statistic dailyStatistic = statisticParser.getDailyDataOrDefault(date);
        Statistic weeklyStatistic = statisticParser.getWeeklyData(date);
        Statistic allTimeStatistic = statisticParser.getAllTimeData();

        SubjectDTO subjectDto = modelMapper.map(subject, SubjectDTO.class);
        subjectDto.setDailyStatistic(statisticsToReadDto(dailyStatistic));
        subjectDto.setWeeklyStatistic(statisticsToReadDto(weeklyStatistic));
        subjectDto.setAllTimeStatistic(statisticsToReadDto(allTimeStatistic));

        subjectDto.setWeeklyGoals(mapWeeklyGoalsToDto(subject.getGoals()));
        subjectDto.setAllTimeGoals(mapAllTimeGoalsToDto(subject.getGoals()));

        return subjectDto;
    }

    public static Subject subjectFromDTO(SubjectDTO subjectDTO) {
        return modelMapper.map(subjectDTO, Subject.class);
    }

    public static Schedule scheduleFromDTO(ScheduleDTO scheduleDTO) {
        return modelMapper.map(scheduleDTO, Schedule.class);
    }

    public static ScheduleDTO scheduleToDto(Schedule schedule) {
        return modelMapper.map(schedule, ScheduleDTO.class);
    }

    public static Credential credentialFromDto(CredentialDTO credentialDTO) {
        return modelMapper.map(credentialDTO, Credential.class);
    }

    public static Student studentFromDto(StudentDTO studentDTO) {
        return modelMapper.map(studentDTO, Student.class);
    }

    public static StudentDTO studentToDto(Student student) {
        StudentDTO studentDto = modelMapper.map(student, StudentDTO.class);

        String email = student.getCredential().getEmail();
        studentDto.setEmail(email);

        return studentDto;
    }

    public static Goal goalFromDto(WriteGoalDTO writeGoalDTO) {
        return modelMapper.map(writeGoalDTO, Goal.class);
    }

    public static ReadStatisticDTO statisticsToReadDto(Statistic statistic) {
        return modelMapper.map(statistic, ReadStatisticDTO.class);
    }

    public static StudyCycleDTO studyCycleToDTO(StudyCycle studyCycle) {
        StudyCycleDTO studyCycleDTO = modelMapper.map(studyCycle, StudyCycleDTO.class);

        studyCycleDTO.setEntries(convertEntriesToDTOs(studyCycle));

        StudyCycleEntry currentEntry = studyCycle.getCurrentEntry();
        if (currentEntry == null) {
            return studyCycleDTO;
        }

        studyCycleDTO.setCurrentEntry(Mapper.studyCycleEntryToDTO(currentEntry));

        return studyCycleDTO;
    }

    public static StudyCycleEntryDTO studyCycleEntryToDTO(StudyCycleEntry studyCycleEntry) {
        StudyCycleEntryDTO dto = modelMapper.map(studyCycleEntry, StudyCycleEntryDTO.class);

        dto.setSubject(Mapper.subjectToDTO(studyCycleEntry.getSubject()));

        return dto;
    }

    public static StudyCycleEntry studyCycleEntryFromDTO(StudyCycleEntryDTO studyCycleEntryDTO) {
        return modelMapper.map(studyCycleEntryDTO, StudyCycleEntry.class);
    }

    public static List<StudyCycleEntry> studyCycleEntriesFromDTOs(List<StudyCycleEntryDTO> subjects) {
        return subjects.stream().map(Mapper::studyCycleEntryFromDTO).toList();
    }

    private static List<StudyCycleEntryDTO> convertEntriesToDTOs(StudyCycle studyCycle) {
        return studyCycle.getEntries().stream().map(Mapper::studyCycleEntryToDTO).toList();
    }

    private static ReadGoalDTO mapWeeklyGoalsToDto(SubjectGoals subjectGoals) {
        ReadGoalDTO readGoalDto = new ReadGoalDTO();

        for (StatisticType statisticType : StatisticType.getEntries()) {
            float goalValue = subjectGoals.getWeeklyGoal(statisticType);

            setStatisticTypeToGoalDto(readGoalDto, statisticType, goalValue);
        }

        return readGoalDto;
    }

    private static ReadGoalDTO mapAllTimeGoalsToDto(SubjectGoals subjectGoals) {
        ReadGoalDTO readGoalDto = new ReadGoalDTO();

        for (StatisticType statisticType : StatisticType.getEntries()) {
            float goalValue = subjectGoals.getAllTimeGoal(statisticType);

            setStatisticTypeToGoalDto(readGoalDto, statisticType, goalValue);
        }

        return readGoalDto;
    }

    private static void setStatisticTypeToGoalDto(ReadGoalDTO readGoalDto, StatisticType statisticType, float goalValue) {
        switch (statisticType) {
            case HOURS -> readGoalDto.setHours(goalValue);
            case QUESTIONS -> readGoalDto.setQuestions((int) goalValue);
            case REVIEWS -> readGoalDto.setReviews((int) goalValue);
        }
    }
}
