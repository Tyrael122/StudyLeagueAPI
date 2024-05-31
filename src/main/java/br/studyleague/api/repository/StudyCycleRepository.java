package br.studyleague.api.repository;

import br.studyleague.api.model.scheduling.studycycle.StudyCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCycleRepository extends JpaRepository<StudyCycle, Long> {
}
