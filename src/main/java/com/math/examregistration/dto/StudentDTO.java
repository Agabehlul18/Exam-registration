package com.math.examregistration.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private String name;
    private String surname;
    private String fatherName;
    private int grade;
    private String phone;
    private boolean bspStudent;
    private double paymentAmount;
    private Long examId;
    private String examTime;

}
