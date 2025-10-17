package com.math.examregistration.controller;

import com.math.examregistration.entity.Exam;
import com.math.examregistration.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExamController {

    private final ExamService examService;

    // 🔹 Yeni imtahan əlavə et
    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        Exam savedExam = examService.createExam(exam);
        return ResponseEntity.ok(savedExam);
    }

    // 🔹 Bütün imtahanları gətir
    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    // 🔹 ID-yə görə gətir
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        return examService.getExamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Aktiv imtahanlar
    @GetMapping("/active")
    public ResponseEntity<List<Exam>> getActiveExams() {
        return ResponseEntity.ok(examService.getActiveExams());
    }

    // 🔹 Sinif üzrə imtahanlar
    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<Exam>> getExamsByGrade(@PathVariable int grade) {
        return ResponseEntity.ok(examService.getExamsByGrade(grade));
    }

    // 🔹 Gələcək imtahanlar
    @GetMapping("/upcoming")
    public ResponseEntity<List<Exam>> getUpcomingExams() {
        return ResponseEntity.ok(examService.getUpcomingExams());
    }

    // 🔹 Otağa görə imtahanlar
    @GetMapping("/room/{roomName}")
    public ResponseEntity<List<Exam>> getExamsByRoom(@PathVariable String roomName) {
        return ResponseEntity.ok(examService.getExamsByRoom(roomName));
    }

    // 🔹 İmtahanı deaktiv et
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateExam(@PathVariable Long id) {
        examService.deactivateExam(id);
        return ResponseEntity.ok().build();
    }

    // 🔹 İmtahanı sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}
