package com.math.examregistration.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examName; // Məs: "Riyaziyyat Sınaq 2"
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date; // İmtahan tarixi
    private String subject; // Fənn (məsələn, Riyaziyyat, Fizika)
    private int grade; // 9, 10, 11 və s.
    private String roomName; // İmtahan otağı (məs: "Otaq 1")

    @Column(length = 500)
    private String description; // Əlavə qeyd (istəyə görə)

    private boolean isActive; // Aktiv imtahan olub-olmadığını göstərir
}
