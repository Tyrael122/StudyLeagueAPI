package br.studyleague.api.controller;

import br.studyleague.api.controller.util.datetime.DateTimeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import util.EndpointPrefixes;

import java.time.LocalDateTime;

@RestController
public class UtilController {

    @GetMapping(EndpointPrefixes.CURRENT_TIME)
    public ResponseEntity<LocalDateTime> getCurrentServerTime() {
        return ResponseEntity.ok(DateTimeUtils.timezoneOffsettedNow());
    }
}
