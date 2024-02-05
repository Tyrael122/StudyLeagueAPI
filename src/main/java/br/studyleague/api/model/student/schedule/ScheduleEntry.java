package br.studyleague.api.model.student.schedule;

import br.studyleague.api.model.subject.Subject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
public class ScheduleEntry {

    @Id
    @GeneratedValue
    private Long id;

    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne // TODO: ManyToOne or OneToOne? Think about that. It should really be one to one, but for that we have to delete the previous schedule when saving.
    private Subject subject;

    public float getDuration() {
        return (endTime.getHour() - startTime.getHour()) + (endTime.getMinute() - startTime.getMinute()) / 60F;
    }
}
