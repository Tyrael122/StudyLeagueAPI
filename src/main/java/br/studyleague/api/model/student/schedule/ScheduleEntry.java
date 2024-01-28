package br.studyleague.api.model.student.schedule;

import br.studyleague.api.model.subject.Subject;
import jakarta.persistence.*;
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

    @ManyToOne // TODO: ManyToOne or OneToOne? Think about that. It should really be one to one, but for that we have to delete the previous schedule when saving.
    private Subject subject;

    public float getDuration() {
        return (end.getHour() - start.getHour()) + (end.getMinute() - start.getMinute()) / 60F;
    }
}
