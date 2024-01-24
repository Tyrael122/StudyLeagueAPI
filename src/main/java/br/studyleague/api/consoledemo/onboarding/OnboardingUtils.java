package br.studyleague.api.consoledemo.onboarding;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Scanner;

public class OnboardingUtils {
    private OnboardingUtils() {}

    private static final String currentDirectory = System.getProperty("user.dir");
    private static final File inputFile = new File(currentDirectory + "\\Input.txt");

    private static final Scanner scanner = initializeScanner();

    public static String readNextLine() {
        String nextLine = scanner.nextLine();

        System.out.println(nextLine);

        return nextLine;
    }

    public static DayOfWeek parseDayOfWeek(String s) {
        return switch (s) {
            case "Segunda" -> DayOfWeek.MONDAY;
            case "Terça" -> DayOfWeek.TUESDAY;
            case "Quarta" -> DayOfWeek.WEDNESDAY;
            case "Quinta" -> DayOfWeek.THURSDAY;
            case "Sexta" -> DayOfWeek.FRIDAY;
            case "Sábado" -> DayOfWeek.SATURDAY;
            case "Domingo" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalStateException("Unexpected value: " + s);
        };
    }

    private static Scanner initializeScanner() {
        try {
            return new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
