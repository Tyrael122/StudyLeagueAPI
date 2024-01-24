package br.studyleague.api.consoledemo.screens;

import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.aggregabledata.statistics.StatisticType;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.aggregable.DailyDataParser;

import java.time.LocalDate;

import static br.studyleague.api.consoledemo.UserInputReader.readConsoleNextLine;

public class ScreenUtils {
    public static Student student;
    public static LocalDate currentDate = Screens.currentDate;

    public static Subject askForSubject() {
        System.out.println("Escolha uma matéria: ");
        for (Subject subject : student.getSubjects()) {
            System.out.println(subject.getName());
        }

        String subjectName = readConsoleNextLine();
        return student.getSubjects().stream()
                .filter(s -> s.getName().equals(subjectName))
                .findFirst().orElseThrow();
    }

    public static String getWeeklyHoursTarget(Subject subject) {
        float targetWeeklyHours = subject.getGoals().getWeeklyGoal(StatisticType.HOURS);

        return Float.toString(targetWeeklyHours);
    }

    public static int getStudiedHoursThisWeek(Subject subject) {
        return (int) DailyDataParser.of(subject.getDailyStatistics()).getWeeklyData(currentDate).getValue(StatisticType.HOURS);
    }

    public static void showStats(Statistic statistic) {
        for (StatisticType statisticType : StatisticType.values()) {
            System.out.println(statisticType + ": " + statistic.getValue(statisticType));
        }
    }

    public static StatisticType askForStatisticType() {
        System.out.println("Qual o tipo de estatística você quer adicionar?");
        for (StatisticType statisticType : StatisticType.values()) {
            System.out.println(statisticType);
        }

        return StatisticType.valueOf(readConsoleNextLine());
    }
}
