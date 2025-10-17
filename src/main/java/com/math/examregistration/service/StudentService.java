package com.math.examregistration.service;

import com.math.examregistration.dto.StudentDTO;
import com.math.examregistration.entity.Exam;
import com.math.examregistration.entity.Room;
import com.math.examregistration.entity.Student;
import com.math.examregistration.repository.ExamRepository;
import com.math.examregistration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoomService roomService;
    private final ExamRepository examRepository;

    // ✅ Yeni qeydiyyat
    public Student registerStudent(StudentDTO dto) {

        // 1️⃣ İmtahan tap
        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new RuntimeException("İmtahan tapılmadı!"));

        // 2️⃣ Eyni şagird eyni imtahana artıq yazılıbsa, xəta at
        studentRepository.findByNameAndSurnameAndFatherNameAndGradeAndExam(
                dto.getName(),
                dto.getSurname(),
                dto.getFatherName(),
                dto.getGrade(),
                exam
        ).ifPresent(s -> {
            throw new RuntimeException("Bu şagird artıq bu imtahan üçün qeydiyyatdan keçib!");
        });

        // 3️⃣ Boş otaq tap və oturacaq nömrəsini təyin et
        Room room = roomService.assignAvailableRoom();
        int seatNo = room.getCurrentCount() + 1;

        // 4️⃣ Unikal 6 rəqəmli kod yarat
        String code = generateUniqueStudentCode();

        // 5️⃣ Yeni şagirdi formalaşdır
        Student student = new Student();
        student.setName(dto.getName());
        student.setSurname(dto.getSurname());
        student.setFatherName(dto.getFatherName());
        student.setGrade(dto.getGrade());
        student.setPhone(dto.getPhone());
        student.setBspStudent(dto.isBspStudent());
        student.setExam(exam);
        student.setRoom(room);
        student.setSeatNo(seatNo);
        student.setStudentCode(code);

        // 6️⃣ Yadda saxla və otağın sayını artır
        Student saved = studentRepository.save(student);
        roomService.incrementRoomCount(room);

        return saved;
    }

    // ✅ Random və unikal 6 rəqəmli iş nömrəsi
    private String generateUniqueStudentCode() {
        Random random = new Random();
        String code;
        do {
            int number = 100000 + random.nextInt(900000);
            code = String.valueOf(number);
        } while (studentRepository.existsByStudentCode(code));
        return code;
    }

    // ✅ Bütün tələbələri gətir
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // ✅ Otaq üzrə tələbələri gətir
    public List<Student> getStudentsByRoom(Long roomId) {
        return studentRepository.findAllByRoomId(roomId);
    }
}
