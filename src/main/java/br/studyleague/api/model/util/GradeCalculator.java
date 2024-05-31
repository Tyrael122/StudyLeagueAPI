package br.studyleague.api.model.util;

import br.studyleague.api.controller.util.datetime.DateRange;
import br.studyleague.api.model.aggregabledata.grade.Grade;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.subject.Subject;
import enums.StatisticType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GradeCalculator {
    private GradeCalculator() {
    }

    public static float calculateDailyGrade(float hoursStudied, float hoursToStudy) {
        float hoursStudiedAverage = ceilGrade(hoursStudied, hoursToStudy);
        return limitFinalAverage(hoursStudiedAverage * 10);
    }

    public static float calculateWeeklyGrade(float questionsGrade, float hoursStudiedGrade, float reviewsGrade) {
        float weeklyGrade = (questionsGrade + hoursStudiedGrade + reviewsGrade) / 3;
        return limitFinalAverage(weeklyGrade * 10);
    }

    public static float calculateQuestionsGrade(DateRange weekRange, List<Subject> subjects) {
        return GradeCalculator.calculateStatisticsGrade(weekRange, subjects, StatisticType.QUESTIONS);
    }

    public static float calculateReviewsGrade(DateRange weekRange, List<Subject> subjects) {
        return GradeCalculator.calculateStatisticsGrade(weekRange, subjects, StatisticType.REVIEWS);
    }

    public static float calculateStatisticsGrade(DateRange weekRange, List<Subject> subjects, StatisticType statisticType) {
        float statisticsGradesSum = 0;
        int numberOfNonEmptyStatisticGoals = 0;

        for (Subject subject : subjects) {
            Statistic subjectStatistics = Statistic.parse(subject.getDailyStatistics()).getWeeklyData(weekRange);

            float statisticGrade = GradeCalculator.ceilGrade(subjectStatistics.getValue(statisticType), subject.getGoals().getWeeklyGoal(statisticType));

            statisticsGradesSum += statisticGrade;

            if (subject.getGoals().getWeeklyGoal(statisticType) != 0) {
                numberOfNonEmptyStatisticGoals++;
            }
        }

        return GradeCalculator.ceilGrade(statisticsGradesSum, numberOfNonEmptyStatisticGoals);
    }

    public static float ceilGrade(float achieved, float target) {
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
