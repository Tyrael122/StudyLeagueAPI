package br.studyleague.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import util.EndpointPrefixes;

import java.time.LocalDateTime;

@Data
@Entity
public class RequestLog {
    @Id
    @GeneratedValue
    private Long id;

    private String method;
    private String path;
    private String queryString;

    private long studentId;

    private LocalDateTime timestamp;

    public void setPath(String path) {
        this.path = path;

        if (path.startsWith(EndpointPrefixes.STUDENT)) {
            String[] pathParts = path.split("/");

            if (pathParts.length < 3) {
                return;
            }

            this.studentId = Long.parseLong(pathParts[2]);
        }
    }
}
