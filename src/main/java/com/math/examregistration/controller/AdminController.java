package com.math.examregistration.controller;

import com.math.examregistration.entity.Student;
import com.math.examregistration.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final StudentService studentService;

    // 1️⃣ Bütün tələbələri gətir
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // 2️⃣ Statistikalar (ümumi say, siniflər üzrə say)
    @GetMapping("/students/statistics")
    public Map<String, Object> getStudentStatistics() {
        long total = studentService.countAllStudents();
        long grade9 = studentService.countByGrade(9);
        long grade10 = studentService.countByGrade(10);
        long grade11 = studentService.countByGrade(11);
        long grade12 = studentService.countByGrade(12);
        Double totalPayment = studentService.getTotalPayment();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("grade9", grade9);
        stats.put("grade10", grade10);
        stats.put("grade11", grade11);
        stats.put("grade12", grade12);
        stats.put("totalPayment", totalPayment != null ? totalPayment : 0.0);

        return stats;
    }

    @GetMapping("/students/payment-statistics")
    public Map<String, Object> getPaymentStats() {
        return studentService.getPaymentStatistics();
    }

}
