package br.studyleague.api.model.scheduling.schedule;

import jakarta.persistence.*;
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

    @OneToMany(cascade = CascadeType.ALL)
    private List<ScheduleEntry> entries = new ArrayList<>();
}

