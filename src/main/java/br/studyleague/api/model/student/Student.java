package br.studyleague.api.model.student;

import br.studyleague.api.controller.util.datetime.DateTimeUtils;
import br.studyleague.api.model.Credential;
import br.studyleague.api.model.scheduling.StudySchedulingMethod;
import enums.StudySchedulingMethods;
import br.studyleague.api.model.scheduling.studycycle.StudyCycle;
import br.studyleague.api.model.aggregabledata.StudentAggregableData;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.scheduling.schedule.Schedule;
import br.studyleague.api.model.subject.Subject;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Delegate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Credential credential;

    private String name = "";
    private String studyArea = "";
    private String goal = "";

    @OneToMany(cascade = CascadeType.ALL)
    private List<Subject> subjects = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private Schedule schedule = new Schedule();

    @OneToOne(cascade = CascadeType.ALL)
    private StudyCycle studyCycle = new StudyCycle();

    private StudySchedulingMethods currentStudySchedulingMethod;

    @Delegate
    @OneToOne(cascade = CascadeType.ALL)
    private StudentAggregableData aggregableData = new StudentAggregableData();

    public List<Statistic> getDailyStatistics() {
        return aggregableData.getStatisticManager().getRawStatistics();
    }

    public void syncGrades() {
        syncGradesByDate(DateTimeUtils.timezoneOffsettedNowDate());
    }

    public void syncGradesByDate(LocalDate updatedDate) {
        getStudySchedulingMethod().syncGradesByDate(this, updatedDate);
    }

    public StudySchedulingMethod getStudySchedulingMethod() {
        return switch (getCurrentStudySchedulingMethod()) {
            case STUDYCYCLE -> getStudyCycle();
            case SCHEDULE -> getSchedule();
        };
    }

    public void syncStatisticsWithSubjects(LocalDate updatedDate) {
        List<Subject> todaySubjects = getStudySchedulingMethod().getSubjects(updatedDate.getDayOfWeek());

        Statistic newDailyStatistic = Subject.sumSubjectStatistics(updatedDate, todaySubjects);
        newDailyStatistic.setDate(updatedDate);

        aggregableData.getStatisticManager().setStatisticValue(updatedDate, newDailyStatistic);

        syncGradesByDate(updatedDate);
    }

    public Subject findSubjectById(Long subjectId) {
        return subjects.stream().filter(subject -> subject.getId().equals(subjectId)).findFirst().orElseThrow();
    }

    public StudySchedulingMethods getCurrentStudySchedulingMethod() {
        if (currentStudySchedulingMethod == null) {
            currentStudySchedulingMethod = StudySchedulingMethods.Companion.getDefaultValue();
        }

        return currentStudySchedulingMethod;
    }

    public StudyCycle getStudyCycle() {
        if (studyCycle == null) {
            studyCycle = new StudyCycle();
        }

        return studyCycle;
    }

    public Schedule getSchedule() {
        if (schedule == null) {
            schedule = new Schedule();
        }

        return schedule;
    }
}
