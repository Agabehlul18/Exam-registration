package com.math.examregistration.service;

import com.math.examregistration.dto.StudentDTO;
import com.math.examregistration.entity.Exam;
import com.math.examregistration.entity.Room;
import com.math.examregistration.entity.Student;
import com.math.examregistration.exception.StudentAlreadyRegisteredException;
import com.math.examregistration.repository.ExamRepository;
import com.math.examregistration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoomService roomService;
    private final ExamRepository examRepository;

    // ✅ Yeni qeydiyyat
    public Student registerStudent(StudentDTO dto) {
        log.info("Yeni tələbə qeydiyyatı başladı: {} {} ({})", dto.getName(), dto.getSurname(), dto.getExamId());

        // 1️⃣ İmtahan tap
        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> {
                    log.error("İmtahan tapılmadı! ID: {}", dto.getExamId());
                    return new RuntimeException("İmtahan tapılmadı!");
                });

        // 2️⃣ Eyni şagird artıq yazılıbsa
        studentRepository.findByNameAndSurnameAndFatherNameAndGradeAndExam(
                dto.getName(),
                dto.getSurname(),
                dto.getFatherName(),
                dto.getGrade(),
                exam
        ).ifPresent(s -> {
            log.warn("Təkrar qeydiyyat cəhdi: {} {} (imtahan ID: {})", dto.getName(), dto.getSurname(), exam.getId());
            throw new StudentAlreadyRegisteredException("Bu şagird artıq bu imtahan üçün qeydiyyatdan keçib!");
        });

        // 3️⃣ Boş otaq tap və oturacaq nömrəsini təyin et
        Room room = roomService.assignAvailableRoom();
        int seatNo = room.getCurrentCount() + 1;
        log.info("Tələbə üçün otaq təyin olundu: {} (oturacaq №{})", room.getRoomNo(), seatNo);

        // 4️⃣ Unikal kod yarat
        String code = generateUniqueStudentCode();
        log.debug("Tələbə üçün unikal kod yaradıldı: {}", code);

        // 5️⃣ Yeni tələbəni yarat
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

        // 6️⃣ Yadda saxla
        Student saved = studentRepository.save(student);
        roomService.incrementRoomCount(room);

        log.info("Tələbə uğurla qeydiyyatdan keçdi: {} {} (kod: {})", saved.getName(), saved.getSurname(), saved.getStudentCode());
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
        log.info("Bütün tələbələr gətirilir...");
        return studentRepository.findAll();
    }

    // ✅ Otaq üzrə tələbələri gətir
    public List<Student> getStudentsByRoom(Long roomId) {
        log.info("Otaq üzrə tələbələr gətirilir: otaq ID {}", roomId);
        return studentRepository.findAllByRoomId(roomId);
    }
}
