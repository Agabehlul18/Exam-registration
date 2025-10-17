package com.math.examregistration.service;

import com.math.examregistration.entity.Exam;
import com.math.examregistration.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;

    // Yeni imtahan əlavə et
    public Exam createExam(Exam exam) {
        exam.setActive(true);
        return examRepository.save(exam);
    }

    // Bütün imtahanları gətir
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    // ID-yə görə tap
    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    // Sinif üzrə imtahanlar
    public List<Exam> getExamsByGrade(int grade) {
        return examRepository.findAllByGrade(grade);
    }

    // Aktiv imtahanlar
    public List<Exam> getActiveExams() {
        return examRepository.findAllByIsActiveTrue();
    }

    // Gələcək imtahanları gətir
    public List<Exam> getUpcomingExams() {
        // 🔹 Düzəliş: examDate → date
        return examRepository.findAllByDateAfter(LocalDateTime.now());
    }


    // Otağa görə imtahanlar
    public List<Exam> getExamsByRoom(String roomName) {
        return examRepository.findAllByRoomName(roomName);
    }

    // İmtahanı deaktiv et
    public void deactivateExam(Long id) {
        examRepository.findById(id).ifPresent(exam -> {
            exam.setActive(false);
            examRepository.save(exam);
        });
    }

    // Silmə
    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }
}

