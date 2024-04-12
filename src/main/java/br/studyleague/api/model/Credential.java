package br.studyleague.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Credential {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
}
