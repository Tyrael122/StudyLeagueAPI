package br.studyleague.api.consoledemo.onboarding;

import br.studyleague.api.model.ScheduleEntry;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static br.studyleague.api.consoledemo.onboarding.OnboardingUtils.parseDayOfWeek;
import static br.studyleague.api.consoledemo.onboarding.OnboardingUtils.readNextLine;

public class ScheduleOnboard {
    public static List<Subject> askForSchedule(Student student) {
        List<Subject> studentSubjects = student.getSubjects();

        System.out.println("""
                Adicione as matérias no seu cronograma: (digite 'fim' para terminar)
                Exemplo de entrada:
                Matemática
                Segunda
                14:00 - 15:00""".stripIndent());

        String subjectName = readNextLine();
        while (!subjectName.equals("fim")) {
            ScheduleEntry scheduleEntry = new ScheduleEntry();

            String finalSubjectName = subjectName;
            Subject subject = studentSubjects.stream().filter(s -> s.getName().equals(finalSubjectName)).findFirst().orElseThrow();

            scheduleEntry.setSubject(subject);

            DayOfWeek dayOfWeek = parseDayOfWeek(readNextLine());

            LocalTime[] times = Arrays.stream(readNextLine().split(" - ")).map(LocalTime::parse).toArray(LocalTime[]::new);
            scheduleEntry.setStart(times[0]);
            scheduleEntry.setEnd(times[1]);

            student.getSchedule().addScheduleEntry(scheduleEntry, dayOfWeek);

            subjectName = readNextLine();
        }

        return studentSubjects;
    }
}
