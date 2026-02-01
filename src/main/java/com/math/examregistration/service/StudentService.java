package com.math.examregistration.service;

import com.math.examregistration.dto.StudentDTO;
import com.math.examregistration.entity.Exam;
import com.math.examregistration.entity.Room;
import com.math.examregistration.entity.Student;
import com.math.examregistration.exception.BadRequestException;
import com.math.examregistration.exception.StudentAlreadyRegisteredException;
import com.math.examregistration.repository.ExamRepository;
import com.math.examregistration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoomService roomService;
    private final ExamRepository examRepository;

    // ✅ Yeni qeydiyyat
    @Transactional
    public Student registerStudent(StudentDTO dto) {
        log.info("Yeni tələbə qeydiyyatı başladı: {} {} (examId={})",
                dto.getName(), dto.getSurname(), dto.getExamId());

        // 1) İmtahan tap
        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new RuntimeException("İmtahan tapılmadı!"));

        // 2) Təkrar qeydiyyat yoxlanışı
        studentRepository.findByNameAndSurnameAndFatherNameAndGradeAndExam(
                dto.getName(),
                dto.getSurname(),
                dto.getFatherName(),
                dto.getGrade(),
                exam
        ).ifPresent(s -> {
            throw new StudentAlreadyRegisteredException("Bu şagird artıq bu imtahan üçün qeydiyyatdan keçib!");
        });

        // 3) Otaq + seat seçimi (10:00 vs 11:30)
        Room room;
        int seatNo;

        String examTime = dto.getExamTime();

        if ("11:30".equals(examTime)) {
            // 11:30 -> paritet qaydası
            RoomService.SlotGroup group;

            // Fizika = 7
            if (dto.getExamId() == 7) {
                group = RoomService.SlotGroup.PHYSICS;
            }
            // Riyaziyyat 9-lar = examId 6 + grade 9
            else if (dto.getExamId() == 6 && dto.getGrade() == 9) {
                group = RoomService.SlotGroup.MATH9;
            } else {
                throw new BadRequestException("11:30 yalnız Fizika (2) və Riyaziyyat 9-cu sinif (5) üçün nəzərdə tutulub!");
            }

            RoomService.RoomAssignment ra = roomService.assignRoomFor1130Parity(group);
            roomService.incrementRoomCountFor1130(ra.getRoom(), group);

            room = ra.getRoom();
            seatNo = ra.getSeatNo();

        } else {
            // 10:00 (və ya digər) -> köhnə qayda (A otaqları)
            room = roomService.assignAvailableRoomByTime(examTime);
            seatNo = room.getCurrentCount() + 1;
            roomService.incrementRoomCount(room);
        }

        log.info("Tələbə üçün otaq təyin olundu: {} (seatNo={})", room.getRoomNo(), seatNo);

        // 4) Unikal kod yarat
        String code = generateUniqueStudentCode();

        // 5) Student yarat
        Student student = new Student();
        student.setName(dto.getName());
        student.setSurname(dto.getSurname());
        student.setFatherName(dto.getFatherName());
        student.setGrade(dto.getGrade());
        student.setPhone(dto.getPhone());

        student.setBspStudent(dto.isBspStudent());
        student.setPaymentAmount(dto.getPaymentAmount());

        student.setExam(exam);
        student.setRoom(room);
        student.setSeatNo(seatNo);
        student.setStudentCode(code);
        student.setExamTime(examTime);

        // 6) Save
        Student saved = studentRepository.save(student);

        log.info("Tələbə uğurla qeydiyyatdan keçdi: {} {} (examTime={}, room={}, seat={})",
                saved.getName(), saved.getSurname(), saved.getExamTime(),
                saved.getRoom().getRoomNo(), saved.getSeatNo());

        return saved;
    }

    // ✅ Random və unikal 6 rəqəmli kod
    private String generateUniqueStudentCode() {
        Random random = new Random();
        String code;
        do {
            int number = 100000 + random.nextInt(900000);
            code = String.valueOf(number);
        } while (studentRepository.existsByStudentCode(code));
        return code;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAllByOrderByIdAsc();
    }

    public List<Student> getStudentsByRoom(Long roomId) {
        return studentRepository.findAllByRoomId(roomId);
    }

    public long countAllStudents() {
        return studentRepository.count();
    }

    public long countByGrade(int grade) {
        return studentRepository.countByGrade(grade);
    }

    public Double getTotalPayment() {
        return studentRepository.sumPaymentAmount();
    }

    public Map<String, Object> getPaymentStatistics() {
        Object[] stats = (Object[]) studentRepository.getPaymentStatistics();
        Map<String, Object> result = new HashMap<>();
        result.put("bspCount", stats[0]);
        result.put("externalCount", stats[1]);
        result.put("totalPayment", stats[2]);
        return result;
    }
}
