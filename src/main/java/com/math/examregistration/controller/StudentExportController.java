package com.math.examregistration.controller;

import com.math.examregistration.entity.Student;
import com.math.examregistration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentExportController {

    private final StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentExportController.class);

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportStudentsToExcel() throws IOException {
        logger.info("Excel export endpoint çağırıldı");
        List<Student> students = studentRepository.findAllByOrderByIdAsc();
        logger.info("Tələbə sayı: {}", students.size());


        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);

            String[] headers = {
                    "ID", "İş nömrəsi","Soyad", "Ad",  "Ata adı", "Sinif", "Telefon",
                     "Otaq", "Yer nömrəsi", "İmtahan adı",
                    "Ödəniş (AZN)", "BSP Tələbəsidir?"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Student s : students) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getStudentCode());
                row.createCell(2).setCellValue(s.getSurname());
                row.createCell(3).setCellValue(s.getName());
                row.createCell(4).setCellValue(s.getFatherName());
                row.createCell(5).setCellValue(s.getGrade());
                row.createCell(6).setCellValue(s.getPhone());
                row.createCell(7).setCellValue(s.getRoom() != null ? s.getRoom().getRoomNo() : "-");
                row.createCell(8).setCellValue(s.getSeatNo());
                row.createCell(9).setCellValue(s.getExam() != null ? s.getExam().getExamName() : "-");
                row.createCell(10).setCellValue(s.getPaymentAmount() != null ? s.getPaymentAmount() : 0.0);
                row.createCell(11).setCellValue(s.isBspStudent() ? "Bəli" : "Xeyr");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            workbook.close();

            logger.info("Excel faylı uğurla yaradıldı");

            HttpHeaders headersRes = new HttpHeaders();
            headersRes.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headersRes.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx");

            return new ResponseEntity<>(bytes, headersRes, HttpStatus.OK);

        } catch (IOException e) {
            logger.error("Excel yaratmaq zamanı xəta baş verdi", e);
            throw e;  // lazım olsa ResponseEntity ilə error qaytara bilərsən
        }
    }
}

