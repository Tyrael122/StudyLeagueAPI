package br.studyleague.api.consoledemo;

import br.studyleague.api.model.student.Student;

import java.time.LocalDate;
import java.util.Scanner;

import static br.studyleague.api.consoledemo.UserInputReader.startUserInteraction;
import static br.studyleague.api.consoledemo.onboarding.Onboarding.onboardStudent;

public class Main {

    public static void main(String[] args) {
        Student student = onboardStudent();

        System.out.println("Welcome to Study League, " + student.getName() + "!");

        startUserInteraction(student);
    }
}

