package br.studyleague.api.model.student.schedule;

import br.studyleague.api.model.subject.Subject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
public class ScheduleEntry {

    @Id
    @GeneratedValue
    private Long id;

    private LocalTime start;
    private LocalTime end;

    @OneToOne
    private Subject subject;

    public float getDuration() {
        return (end.getHour() - start.getHour()) + (end.getMinute() - start.getMinute()) / 60F;
    }
}
