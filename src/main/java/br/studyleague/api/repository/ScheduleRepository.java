package br.studyleague.api.repository;

import br.studyleague.api.model.scheduling.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
