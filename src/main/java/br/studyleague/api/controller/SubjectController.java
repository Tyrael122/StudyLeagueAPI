package br.studyleague.api.controller;

import br.studyleague.api.controller.util.EndpointPrefixes;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.repository.StudentRepository;
import br.studyleague.api.repository.SubjectRepository;
import br.studyleague.dtos.SubjectDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubjectController {
    private final String ENDPOINT_PREFIX = EndpointPrefixes.SUBJECT;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public SubjectController(SubjectRepository subjectRepository, StudentRepository studentRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping(EndpointPrefixes.STUDENT_ID + ENDPOINT_PREFIX)
    public ResponseEntity<Subject> create(@PathVariable Long id, @RequestBody SubjectDTO subjectDto) {
        Subject subject = modelMapper.map(subjectDto, Subject.class);

        Student student = studentRepository.findById(id).orElseThrow();
        student.getSubjects().add(subject);

        studentRepository.save(student);

        return ResponseEntity.ok(subject);
    }

    @GetMapping(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    public ResponseEntity<List<Subject>> getById(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElseThrow();

        return ResponseEntity.ok(student.getSubjects());
    }
}
