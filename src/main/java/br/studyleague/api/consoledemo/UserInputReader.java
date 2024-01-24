package br.studyleague.api.consoledemo;

import br.studyleague.api.consoledemo.screens.Screens;
import br.studyleague.api.model.student.Student;

import java.util.Scanner;

import static br.studyleague.api.consoledemo.screens.Screens.*;

public class UserInputReader {

    public static void startUserInteraction(Student student) {
        Screens.setStudent(student);

        try {
            String input = "menu";
            while (!input.equals("sair")) {
                showScreens(input);

                input = askUserForOption();
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro.");
            e.printStackTrace();
            startUserInteraction(student);
        }
    }

    private static String askUserForOption() {
        System.out.println("O que você quer fazer agora? Digite 'menu' para ver o menu, caso queira.");

        return readConsoleNextLine();
    }

    private static void showScreens(String input) {
        if ("menu".equals(input)) {
            showMenu();
            return;
        }

        switch (Integer.parseInt(input)) {
            case 1 -> showGlobalData();
            case 2 -> showDailyData();
            case 3 -> addSubjectStats();
            case 4 -> seeSubjects();
            case 5 -> seeSubjectGoals();
            case 6 -> seeSubjectStats();
            case 7 -> seeStudentSchedule();
            case 8 -> setNewGoals();
            case 9 -> skipDay();
        }
    }

    private static void showMenu() {
        System.out.println("""
                1 - Ver dados da tela geral.
                2 - Ver dados da tela diária.
                3 - Adicionar estatísticas.
                4 - Ver matérias.
                5 - Ver metas de uma matéria.
                6 - Ver estatísticas de uma matéria.
                7 - Ver cronograma.
                8 - Definir novas metas.
                9 - Pular dia.
                sair - Sair do programa.
                """.stripIndent());
    }

    public static String readConsoleNextLine() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
