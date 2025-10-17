package com.math.examregistration.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomNo; // Məs: "Otaq 1"
    private int capacity; // Tutum
    private int currentCount; // Hal-hazırda neçə nəfər var
}
