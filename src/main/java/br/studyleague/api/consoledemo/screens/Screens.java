package br.studyleague.api.consoledemo.screens;

import br.studyleague.api.model.ScheduleEntry;
import br.studyleague.api.model.goals.Goal;
import br.studyleague.api.model.statistics.StatisticType;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.student.StudyDay;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.aggregable.DailyDataParser;

import java.time.LocalDate;

import static br.studyleague.api.consoledemo.UserInputReader.readConsoleNextLine;
import static br.studyleague.api.consoledemo.screens.ScreenUtils.*;

public class Screens {
    public static Student student;
    public static LocalDate currentDate = LocalDate.now();

    public static void showGlobalData() {
        System.out.println("Nota semanal: " + RangedDataParser.of(student.getWeeklyGrades()).getWeeklyData(currentDate).getGrade());
        System.out.println("Nota mensal: " + RangedDataParser.of(student.getWeeklyGrades()).getMonthlyData(currentDate).getGrade());

        System.out.println("Estatísticas totais: ");
        showStats(DailyDataParser.of(student.getDailyStatistics()).getAllTimeData());

        System.out.println("Estatísticas semanais: ");
        showStats(DailyDataParser.of(student.getDailyStatistics()).getWeeklyData(currentDate));
    }

    public static void showDailyData() {
        System.out.println("Nota diária: " + DailyDataParser.of(student.getDailyGrades()).getDailyDataOrDefault(currentDate).getGrade());

        System.out.println("Estatísticas diárias: ");
        showStats(DailyDataParser.of(student.getDailyStatistics()).getDailyDataOrDefault(currentDate));

        System.out.println("Matérias: ");
        for (Subject subject : student.getSchedule().getSubjects(currentDate.getDayOfWeek())) {
            System.out.println(subject.getName() + ", horas estudadas: " + getStudiedHoursThisWeek(subject) + ", carga horária: " + getWeeklyHoursTarget(subject));
        }
    }

    public static void addSubjectStats() {
        Subject subject = askForSubject();

        StatisticType statisticType = askForStatisticType();

        System.out.println("Digite o valor da estatística: ");
        float statisticValue = Float.parseFloat(readConsoleNextLine());

        subject.getStatisticManager().setStatisticValue(currentDate, statisticType, statisticValue);
        student.syncStatisticsWithSubjects(currentDate);
    }

    public static void seeSubjects() {
        System.out.println("Disciplinas: ");
        for (Subject subject : student.getSubjects()) {
            System.out.println(subject.getName() + ", horas estudadas: " + getStudiedHoursThisWeek(subject) + ", carga horária: " + getWeeklyHoursTarget(subject));
        }
    }

    public static void seeSubjectGoals() {
        Subject subject = askForSubject();

        System.out.println("Metas totais para " + subject.getName() + ":");
        for (Goal goal : subject.getGoals().getAllTimeGoals()) {
            System.out.println(goal.getStatisticType() + ": " + goal.getValue());
        }

        System.out.println("Metas semanais para " + subject.getName() + ":");
        for (Goal goal : subject.getGoals().getWeeklyGoals()) {
            System.out.println(goal.getStatisticType() + ": " + goal.getValue());
        }
    }

    public static void seeSubjectStats() {
        Subject subject = askForSubject();

        System.out.println("Estatísticas totais para " + subject.getName() + ":");
        showStats(DailyDataParser.of(subject.getDailyStatistics()).getAllTimeData());

        System.out.println("Estatísticas semanais para " + subject.getName() + ":");
        showStats(DailyDataParser.of(subject.getDailyStatistics()).getWeeklyData(currentDate));
    }

    public static void seeStudentSchedule() {
        System.out.println("Horário do aluno: ");
        for (StudyDay studyDay : student.getSchedule().getDays()) {
            System.out.println(studyDay.getDayOfWeek() + ": ");

            for (ScheduleEntry scheduleEntry : studyDay.getSchedule()) {
                System.out.println(scheduleEntry.getStart() + " - " + scheduleEntry.getEnd() + ": " + scheduleEntry.getSubject().getName());
            }
        }
    }

    public static void setNewGoals() {
        Subject subject = askForSubject();

        System.out.println("Digite o tipo de estatística: ");
        StatisticType statisticType = askForStatisticType();

        System.out.println("Digite o valor da meta: ");
        float goalValue = Float.parseFloat(readConsoleNextLine());

        subject.getGoals().setWeeklyGoal(statisticType, goalValue);
        student.syncGradesByDate(currentDate);
    }

    public static void skipDay() {
        currentDate = currentDate.plusDays(1);
        System.out.println("Pulando para o dia " + currentDate + ", " + currentDate.getDayOfWeek());
    }

    public static void setStudent(Student student) {
        Screens.student = student;
        ScreenUtils.student = student;
    }
}
