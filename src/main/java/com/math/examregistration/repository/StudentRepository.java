package com.math.examregistration.repository;

import com.math.examregistration.entity.Exam;
import com.math.examregistration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Eyni şagirdi adı, soyadı, ata adı və sinif üzrə tapmaq
    Optional<Student> findByNameAndSurnameAndFatherNameAndGradeAndExam(String name, String surname, String fatherName, int grade, Exam exam);

    // ✅ Unikal iş nömrəsinin olub-olmadığını yoxlamaq üçün metod
    boolean existsByStudentCode(String studentCode);
    // 🔹 Otaq üzrə tələbələri gətirən metod
    List<Student> findAllByRoomId(Long roomId);
    long countByGrade(int grade);

}
