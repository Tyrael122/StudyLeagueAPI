package br.studyleague.api.model.student;

import br.studyleague.api.model.ScheduleEntry;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class StudyDay {

    @Id
    @GeneratedValue
    private Long id;

    private DayOfWeek dayOfWeek;

    @OneToMany
    private List<ScheduleEntry> schedule = new ArrayList<>();
}

