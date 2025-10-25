package com.math.examregistration.repository;

import com.math.examregistration.entity.Exam;
import com.math.examregistration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    @Query("SELECT SUM(s.paymentAmount) FROM Student s")
    Double sumPaymentAmount();
    List<Student> findAllByOrderByIdAsc();

    @Query("""
        SELECT 
            SUM(CASE WHEN s.paymentAmount = 0 THEN 1 ELSE 0 END) AS sGroupCount,
            SUM(CASE WHEN s.paymentAmount = 5 THEN 1 ELSE 0 END) AS bspCount,
            SUM(CASE WHEN s.paymentAmount = 7 THEN 1 ELSE 0 END) AS externalCount,
            SUM(s.paymentAmount) AS totalPayment
        FROM Student s
    """)
    Object getPaymentStatistics();



}
