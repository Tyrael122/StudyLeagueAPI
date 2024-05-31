package br.studyleague.api.model.scheduling;


import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface StudySchedulingMethod {
    List<Subject> getSubjects(DayOfWeek dayOfWeek);

    void syncGradesByDate(Student student, LocalDate date);

    int calculateHoursGoalsCompleted(Student student, LocalDate date);
}
