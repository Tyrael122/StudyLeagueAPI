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

    public static float calculateDailyGrade(LocalDate date, Map<Subject, Float> subjectWithHoursToStudyToday) {
        float hoursToStudy = 0;
        float hoursStudied = 0;

        for (Subject subject : subjectWithHoursToStudyToday.keySet()) {
            Statistic subjectStatistic = Statistic.parse(subject.getDailyStatistics()).getDailyDataOrDefault(date);

            hoursToStudy += subjectWithHoursToStudyToday.get(subject);

            hoursStudied += subjectStatistic.getValue(StatisticType.HOURS);
        }

        float hoursStudiedAverage = ceilGrade(hoursStudied, hoursToStudy);
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

        float hoursStudiedAverage = ceilGrade(hoursStudied, hoursToStudy);
        float doneQuestionsAverage = ceilGrade(questionsGradesSum, numberOfNonEmptyQuestionGoals);
        float reviewsDoneAverage = ceilGrade(reviewsDoneGradesSum, numberOfNonEmptyReviewGoals);

        float weeklyGrade = (doneQuestionsAverage + hoursStudiedAverage + reviewsDoneAverage) / 3;
        return limitFinalAverage(weeklyGrade * 10);
    }

    private static float ceilGrade(float achieved, float target) {
        if (target == 0) {
            return 0;
        }

        float grade = achieved / target;
        if (grade > 1.1) {
            return 1.1F;
        }

        return grade;
    }

    private static float limitFinalAverage(float average) {
        if (average > 10) {
            return 10;
        }

        return average;
    }
}
