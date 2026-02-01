package com.math.examregistration.repository;

import com.math.examregistration.entity.Exam;
import com.math.examregistration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByNameAndSurnameAndFatherNameAndGradeAndExam(
            String name, String surname, String fatherName, int grade, Exam exam
    );

    boolean existsByStudentCode(String studentCode);

    List<Student> findAllByRoomId(Long roomId);

    long countByGrade(int grade);

    @Query("SELECT SUM(s.paymentAmount) FROM Student s")
    Double sumPaymentAmount();

    List<Student> findAllByOrderByIdAsc();

    @Query("""
        SELECT 
            SUM(CASE WHEN s.paymentAmount = 2 THEN 1 ELSE 0 END) AS bspCount,
            SUM(CASE WHEN s.paymentAmount = 10 THEN 1 ELSE 0 END) AS externalCount,
            SUM(s.paymentAmount) AS totalPayment
        FROM Student s
    """)
    Object getPaymentStatistics();

    @Query("SELECT s.seatNo FROM Student s WHERE s.room.id = :roomId")
    List<Integer> findSeatNosByRoomId(@Param("roomId") Long roomId);

    // ✅ 11:30 üçün seat doluluğunu yoxlamağa lazım olan query
    @Query("""
        SELECT s.seatNo
        FROM Student s
        WHERE s.room.id = :roomId AND s.examTime = :examTime
    """)
    List<Integer> findSeatNosByRoomAndTime(@Param("roomId") Long roomId,
                                           @Param("examTime") String examTime);
}
