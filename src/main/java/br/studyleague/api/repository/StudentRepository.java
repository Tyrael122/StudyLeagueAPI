package br.studyleague.api.repository;

import br.studyleague.api.model.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByCredential_Email(String email);
}
