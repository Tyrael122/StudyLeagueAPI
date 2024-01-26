package br.studyleague.api.controller;

import br.studyleague.api.controller.util.EndpointPrefixes;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.repository.StudentRepository;
import br.studyleague.dtos.StudentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StudentController {
    private final String ENDPOINT_PREFIX = EndpointPrefixes.STUDENT;
    private final ModelMapper modelMapper;

    private final StudentRepository studentRepository;

    public StudentController(ModelMapper modelMapper, StudentRepository studentRepository) {
        this.modelMapper = modelMapper;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/student")
    public ResponseEntity<Student> create(@RequestBody StudentDTO studentDto) {
        Student student = modelMapper.map(studentDto, Student.class);

        studentRepository.save(student);

        return ResponseEntity.ok(student);
    }

    @GetMapping(ENDPOINT_PREFIX + "/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentRepository.findById(id).orElseThrow());
    }
}
