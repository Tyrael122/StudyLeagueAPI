package br.studyleague.api.model.util;

import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.student.schedule.ScheduleEntry;
import br.studyleague.api.model.subject.Subject;
import enums.StatisticType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeCalculator {
    private GradeCalculator() {
    }

    public static float calculateDailyGrade(LocalDate date, List<ScheduleEntry> scheduleEntries) {
        if (scheduleEntries.isEmpty()) {
            return 0;
        }

        float hoursToStudy = 0;
        float hoursStudied = 0;

        Map<Subject, Float> subjects = new HashMap<>();
        for (ScheduleEntry entry : scheduleEntries) {
            float previousDuration = subjects.getOrDefault(entry.getSubject(), 0F);
            subjects.put(entry.getSubject(), previousDuration + entry.getDuration());
        }

        for (Subject subject : subjects.keySet()) {
            Statistic subjectStatistic = Statistic.parse(subject.getDailyStatistics()).getDailyDataOrDefault(date);

            hoursToStudy += subjects.get(subject);

            hoursStudied = subjectStatistic.getValue(StatisticType.HOURS);
        }

        float hoursStudiedAverage = hoursStudied / hoursToStudy;
        return limitFinalAverage(hoursStudiedAverage * 10);
    }

    public static float calculateWeeklyGrade(DateRange weekRange, List<Subject> subjects) {
        float hoursToStudy = 0;
        float hoursStudied = 0;

        float questionsGradesSum = 0;
        float reviewsDoneGradesSum = 0;

        int numberOfNonEmptyQuestionGoals = 0;
        int numberOfNonEmptyReviewGoals = 0;

        for (Subject subject : subjects) {
            Statistic subjectStatistics = Statistic.parse(subject.getDailyStatistics()).getWeeklyData(weekRange);

            hoursToStudy += subject.getGoals().getWeeklyGoal(StatisticType.HOURS);
            hoursStudied += subjectStatistics.getValue(StatisticType.HOURS);

            float questionsGrade = ceilGrade(subjectStatistics.getValue(StatisticType.QUESTIONS), subject.getGoals().getWeeklyGoal(StatisticType.QUESTIONS));
            float reviewsGrade = ceilGrade(subjectStatistics.getValue(StatisticType.REVIEWS), subject.getGoals().getWeeklyGoal(StatisticType.REVIEWS));

            questionsGradesSum += questionsGrade;
            reviewsDoneGradesSum += reviewsGrade;

            if (subject.getGoals().getWeeklyGoal(StatisticType.QUESTIONS) != 0) {
                numberOfNonEmptyQuestionGoals++;
            }

            if (subject.getGoals().getWeeklyGoal(StatisticType.REVIEWS) != 0) {
                numberOfNonEmptyReviewGoals++;
            }
        }

        float hoursStudiedAverage = hoursStudied / hoursToStudy;
        float doneQuestionsAverage = questionsGradesSum / numberOfNonEmptyQuestionGoals;
        float reviewsDoneAverage = reviewsDoneGradesSum / numberOfNonEmptyReviewGoals;

        float weeklyGrade = (doneQuestionsAverage + hoursStudiedAverage + reviewsDoneAverage) / 3;
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
