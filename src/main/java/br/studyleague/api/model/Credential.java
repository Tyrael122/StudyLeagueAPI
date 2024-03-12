package br.studyleague.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Credential {
    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String password;
}
