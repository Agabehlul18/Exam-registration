package com.math.examregistration.repository;

import com.math.examregistration.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findAllByGrade(int grade);

    List<Exam> findAllByIsActiveTrue();

    List<Exam> findAllByDateAfter(LocalDateTime date); // <-- burada düzəliş

    List<Exam> findAllByRoomName(String roomName);
}
