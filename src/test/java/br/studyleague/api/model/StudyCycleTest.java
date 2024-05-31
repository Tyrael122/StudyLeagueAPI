package br.studyleague.api.model;

import br.studyleague.api.model.scheduling.studycycle.StudyCycle;
import br.studyleague.api.model.scheduling.studycycle.StudyCycleEntry;
import br.studyleague.api.model.subject.Subject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudyCycleTest {

    @Test
    void shouldAddSubject() {
        StudyCycle studyCycle = new StudyCycle();
        Subject subject = mockSubject("Math", 1);

        addSubject(studyCycle, subject);

        assertEquals(1, studyCycle.getEntries().size());
        assertEquals(subject, studyCycle.getEntries().getFirst().getSubject());
    }

    @Test
    void shouldRemoveSubject() {
        StudyCycle studyCycle = new StudyCycle();
        Subject subject = mockSubject("Math", 1);

        addSubject(studyCycle, subject);
        removeSubject(studyCycle, subject);

        assertEquals(0, studyCycle.getEntries().size());
    }

    @Test
    void shouldCycleThroughSubjects() {
        StudyCycle studyCycle = new StudyCycle();

        Subject math = mockSubject("Math", 1);
        Subject physics = mockSubject("Physics", 2);
        Subject chemistry = mockSubject("Chemistry", 3);

        addSubject(studyCycle, math);
        addSubject(studyCycle, physics);
        addSubject(studyCycle, chemistry);

        assertEquals(math, studyCycle.getCurrentEntry().getSubject());

        studyCycle.nextEntry();
        assertEquals(physics, studyCycle.getCurrentEntry().getSubject());

        studyCycle.nextEntry();
        assertEquals(chemistry, studyCycle.getCurrentEntry().getSubject());

        studyCycle.nextEntry();
        assertEquals(math, studyCycle.getCurrentEntry().getSubject());
    }

    @Test
    void shouldReturnTrueWhenAtStart() {
        StudyCycle studyCycle = new StudyCycle();

        Subject math = mockSubject("Math", 1);
        Subject physics = mockSubject("Physics", 2);
        Subject chemistry = mockSubject("Chemistry", 3);

        addSubject(studyCycle, math);
        addSubject(studyCycle, physics);
        addSubject(studyCycle, chemistry);

        assertTrue(studyCycle.isAtStart());
    }

    @Test
    void shouldReturnFalseWhenNotAtStart() {
        StudyCycle studyCycle = new StudyCycle();

        Subject math = mockSubject("Math", 1);
        Subject physics = mockSubject("Physics", 2);
        Subject chemistry = mockSubject("Chemistry", 3);

        addSubject(studyCycle, math);
        addSubject(studyCycle, physics);
        addSubject(studyCycle, chemistry);

        studyCycle.nextEntry();

        assertFalse(studyCycle.isAtStart());
    }

    @Test
    void shouldReturnTrueWhenCycledAndIsAtStart() {
        StudyCycle studyCycle = new StudyCycle();

        Subject math = mockSubject("Math", 1);
        Subject physics = mockSubject("Physics", 2);
        Subject chemistry = mockSubject("Chemistry", 3);

        addSubject(studyCycle, math);
        addSubject(studyCycle, physics);
        addSubject(studyCycle, chemistry);

        studyCycle.nextEntry();
        studyCycle.nextEntry();
        studyCycle.nextEntry();

        assertTrue(studyCycle.isAtStart());
    }

    @Test
    void shouldSetSubjects() {
        StudyCycle studyCycle = new StudyCycle();

        Subject math = mockSubject("Math", 1);
        Subject physics = mockSubject("Physics", 2);
        Subject chemistry = mockSubject("Chemistry", 3);

        addSubject(studyCycle, math);
        addSubject(studyCycle, physics);
        addSubject(studyCycle, chemistry);

        Subject biology = mockSubject("Biology", 4);
        Subject history = mockSubject("History", 5);
        Subject geography = mockSubject("Geography", 6);

        clearEntries(studyCycle);
        addListOfSubjects(studyCycle, biology, history, geography);

        assertEquals(3, studyCycle.getEntries().size());
        assertEquals(biology, studyCycle.getEntries().getFirst().getSubject());
        assertTrue(studyCycle.isAtStart());
    }

    @Test
    void shouldResetCurrentSubjectWhenSettingSubjects() {
        StudyCycle studyCycle = new StudyCycle();

        Subject math = mockSubject("Math", 1);
        Subject physics = mockSubject("Physics", 2);
        Subject chemistry = mockSubject("Chemistry", 3);

        addSubject(studyCycle, math);
        addSubject(studyCycle, physics);
        addSubject(studyCycle, chemistry);

        studyCycle.nextEntry();
        studyCycle.nextEntry();
        assertFalse(studyCycle.isAtStart());

        Subject biology = mockSubject("Biology", 4);
        Subject history = mockSubject("History", 5);
        Subject geography = mockSubject("Geography", 6);

        clearEntries(studyCycle);
        addListOfSubjects(studyCycle, biology, history, geography);


        assertEquals(3, studyCycle.getEntries().size());
        assertEquals(biology, studyCycle.getEntries().getFirst().getSubject());
        assertTrue(studyCycle.isAtStart());
    }

    private void addSubject(StudyCycle studyCycle, Subject subject) {
        StudyCycleEntry studyCycleEntry = new StudyCycleEntry();
        studyCycleEntry.setSubject(subject);
        studyCycleEntry.setDurationInMinutes(60);

        addStudyCycleEntry(studyCycle, studyCycleEntry);
    }

    private void removeSubject(StudyCycle studyCycle, Subject subject) {
        StudyCycleEntry studyCycleEntry = studyCycle.getEntries().stream()
                .filter(entry -> entry.getSubject().equals(subject))
                .findFirst()
                .orElse(null);

        removeStudyCycleEntry(studyCycle, studyCycleEntry);
    }

    private void addStudyCycleEntry(StudyCycle studyCycle, StudyCycleEntry studyCycleEntry) {
        List<StudyCycleEntry> currentEntries = studyCycle.getEntries();
        currentEntries.add(studyCycleEntry);

        studyCycle.setEntries(currentEntries);
    }

    private void removeStudyCycleEntry(StudyCycle studyCycle, StudyCycleEntry studyCycleEntry) {
        List<StudyCycleEntry> currentEntries = studyCycle.getEntries();
        currentEntries.remove(studyCycleEntry);

        studyCycle.setEntries(currentEntries);
    }

    private void addListOfSubjects(StudyCycle studyCycle, Subject... subjects) {
        for (Subject subject : subjects) {
            addSubject(studyCycle, subject);
        }
    }

    private static void clearEntries(StudyCycle studyCycle) {
        studyCycle.setEntries(new ArrayList<>());
    }

    private Subject mockSubject(String name, int id) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setId((long) id);

        return subject;
    }
}