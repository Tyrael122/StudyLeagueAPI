package br.studyleague.api.model.util;

import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.student.schedule.ScheduleEntry;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.dtos.enums.StatisticType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeCalculator {
    private GradeCalculator() {}

    public static float calculateDailyGrade(LocalDate date, List<ScheduleEntry> scheduleEntries) {
        if (scheduleEntries.isEmpty()) {
            return 0;
        }

        float numberOfHoursGoalsDone = 0;
        float hoursStudiedAverage = 0;

        Map<Subject, Float> subjects = new HashMap<>();
        for (ScheduleEntry entry : scheduleEntries) {
            float previousDuration = subjects.getOrDefault(entry.getSubject(), 0F);
            subjects.put(entry.getSubject(), previousDuration + entry.getDuration());
        }

        for (Subject subject : subjects.keySet()) {
            Statistic subjectStatistic = Statistic.parse(subject.getDailyStatistics()).getDailyDataOrDefault(date);

            float totalHoursToDo = subjects.get(subject);

            float hoursGrade = ceilGrade(subjectStatistic.getValue(StatisticType.HOURS), totalHoursToDo);

            if (subjectStatistic.getValue(StatisticType.HOURS) >= totalHoursToDo) {
                numberOfHoursGoalsDone++;
            }

            hoursStudiedAverage += hoursGrade;
        }

        hoursStudiedAverage /= subjects.size();
        float numberOfHoursGoalsDoneAverage = (numberOfHoursGoalsDone / subjects.size());

        float average = (hoursStudiedAverage + numberOfHoursGoalsDoneAverage) / 2;
        return limitFinalAverage(average * 10);
    }

    public static float calculateWeeklyGrade(DateRange weekRange, List<Subject> subjects) {
        float numberOfHoursGoalsDone = 0;

        float doneQuestionsAverage = 0;
        float hoursStudiedAverage = 0;
        float reviewsDoneAverage = 0;

        for (Subject subject : subjects) {
            Statistic subjectStatistics = Statistic.parse(subject.getDailyStatistics()).getWeeklyData(weekRange);

            float hoursGrade = ceilGrade(subjectStatistics.getValue(StatisticType.HOURS), subject.getGoals().getWeeklyGoal(StatisticType.HOURS));
            float questionsGrade = ceilGrade(subjectStatistics.getValue(StatisticType.QUESTIONS), subject.getGoals().getWeeklyGoal(StatisticType.QUESTIONS));
            float reviewsGrade = ceilGrade(subjectStatistics.getValue(StatisticType.REVIEWS), subject.getGoals().getWeeklyGoal(StatisticType.REVIEWS));

            if (subjectStatistics.getValue(StatisticType.HOURS) >= subject.getGoals().getWeeklyGoal(StatisticType.HOURS)) {
                numberOfHoursGoalsDone++;
            }

            doneQuestionsAverage += questionsGrade;
            hoursStudiedAverage += hoursGrade;
            reviewsDoneAverage += reviewsGrade;
        }

        doneQuestionsAverage /= subjects.size();
        hoursStudiedAverage /= subjects.size();
        reviewsDoneAverage /= subjects.size();

        float numberOfHoursGoalsDoneAverage = (numberOfHoursGoalsDone / subjects.size());

        float weeklyGrade = (doneQuestionsAverage + hoursStudiedAverage + reviewsDoneAverage + numberOfHoursGoalsDoneAverage) / 4;
        return limitFinalAverage(weeklyGrade * 10);
    }

    private static float ceilGrade(float achieved, float target) {
        if (target == 0) {
            return 0;
        }

        if (achieved > target) {
            return 1.1F;
        }

        return achieved / target;
    }

    private static float limitFinalAverage(float average) {
        if (average > 10) {
            return 10;
        }

        return average;
    }
}
