package br.studyleague.api.consoledemo.onboarding;

import br.studyleague.api.model.goals.Goal;
import br.studyleague.api.model.aggregabledata.statistics.StatisticType;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.goals.SubjectGoals;

import java.util.ArrayList;
import java.util.List;

import static br.studyleague.api.consoledemo.onboarding.OnboardingUtils.readNextLine;

public class Onboarding {


    public static Student onboardStudent() {
        Student student = askForStudentData();

        student.setSubjects(askForSubjects());

        student.setSubjects(ScheduleOnboard.askForSchedule(student));

        askForSubjectGoals(student.getSubjects());

        return student;
    }

    private static void askForSubjectGoals(List<Subject> subjects) {
        System.out.println("Defina suas metas para cada matéria:");

        for (Subject subject : subjects) {
            SubjectGoals goals = subject.getGoals();

            System.out.println("Metas para " + subject.getName() + ":");
            goals.getAllTimeGoals().add(askForTotalQuestions());
            goals.getWeeklyGoals().addAll(askForWeeklyGoals());

            subject.setGoals(goals);
        }
    }

    private static List<Goal> askForWeeklyGoals() {
        List<Goal> goals = new ArrayList<>();

        System.out.println("Quantas questões você quer resolver por semana?");
        Goal goal = new Goal();
        goal.setValue(Integer.parseInt(readNextLine()));
        goal.setStatisticType(StatisticType.QUESTIONS);

        goals.add(goal);

        System.out.println("Quantas revisões você quer fazer por semana?");
        goal = new Goal();
        goal.setValue(Integer.parseInt(readNextLine()));
        goal.setStatisticType(StatisticType.REVIEWS);

        goals.add(goal);

        return goals;
    }

    private static Goal askForTotalQuestions() {
        Goal goal = new Goal();

        System.out.println("Quantas questões você quer resolver no total?");
        goal.setValue(Integer.parseInt(readNextLine()));
        goal.setStatisticType(StatisticType.QUESTIONS);

        return goal;
    }

    private static List<Subject> askForSubjects() {
        System.out.println("Quais são as máterias que você estuda? (digite 'fim' para terminar)");

        List<Subject> subjects = new ArrayList<>();

        String input = readNextLine();
        while (!input.equals("fim")) {
            Subject subject = new Subject();
            subject.setName(input);

            subjects.add(subject);

            input = readNextLine();
        }

        return subjects;
    }

    private static Student askForStudentData() {
        Student student = new Student();

        System.out.println("Qual seu nome?");
        student.setName(readNextLine());

        System.out.println("Qual sua área de estudo?");
        student.setStudyArea(readNextLine());

        return student;
    }
}
