package com.math.examregistration.controller;

import com.math.examregistration.dto.StudentDTO;
import com.math.examregistration.entity.Student;
import com.math.examregistration.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    // ✅ Yeni qeydiyyat (imtahanla birlikdə)
    @PostMapping("/add")
    public ResponseEntity<Student> addStudent(@RequestBody StudentDTO dto) {
        Student savedStudent = studentService.registerStudent(dto);
        return ResponseEntity.ok(savedStudent);
    }

    // ✅ Bütün şagirdləri gətir
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // ✅ Otaq üzrə tələbələri gətir
    @GetMapping("/byRoom/{roomId}")
    public ResponseEntity<List<Student>> getStudentsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(studentService.getStudentsByRoom(roomId));
    }
}
