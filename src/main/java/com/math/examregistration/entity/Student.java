package com.math.examregistration.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String fatherName;
    private int grade;
    private String phone;

    private String studentCode;
    private int seatNo; // otaqdakı yer nömrəsi
    @Column(name = "exam_time")
    private String examTime; // imtahan saatı


    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "payment_amount", nullable = true)
    private Double paymentAmount;


    // ✅ BSP LearnUp tələbəsisən? (true / false)
    @Column(name = "is_bsp_student")
    private boolean bspStudent;
}
