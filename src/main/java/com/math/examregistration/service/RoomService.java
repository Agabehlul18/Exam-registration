package com.math.examregistration.service;

import com.math.examregistration.entity.Room;
import com.math.examregistration.exception.BadRequestException;
import com.math.examregistration.exception.ConflictException;
import com.math.examregistration.exception.NotFoundException;
import com.math.examregistration.repository.RoomRepository;
import com.math.examregistration.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    // 11:30 qrupları
    public enum SlotGroup { MATH9, PHYSICS }

    @Data
    @AllArgsConstructor
    public static class RoomAssignment {
        private Room room;
        private int seatNo;
    }

    // ---------------------------------------------------------
    // ✅ Controller üçün lazımdır (səndə "cannot find getAllRooms" xətası)
    // ---------------------------------------------------------
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // ---------------------------------------------------------
    // ✅ 10:00 (A otaqları) — əvvəlki qayda:
    // hər otağın 1-ci yeri, sonra hər otağın 2-ci yeri...
    // ---------------------------------------------------------
    public Room assignAvailableRoomByTime(String examTime) {
        String prefix = examTime.startsWith("10") ? "A" : "B";
        List<Room> rooms = roomRepository.findRoomsByPrefixOrderByIdAsc(prefix);

        if (rooms.isEmpty()) {
            throw new NotFoundException("Bu vaxta uyğun otaq tapılmadı!");
        }

        int maxCapacity = rooms.stream().mapToInt(Room::getCapacity).max().orElse(0);

        for (int seatNo = 1; seatNo <= maxCapacity; seatNo++) {
            for (Room room : rooms) {
                if (seatNo <= room.getCapacity() && room.getCurrentCount() < room.getCapacity()) {
                    if (room.getCurrentCount() + 1 == seatNo) {
                        return room;
                    }
                }
            }
        }

        for (Room room : rooms) {
            if (room.getCurrentCount() < room.getCapacity()) {
                return room;
            }
        }

        throw new NotFoundException("Heç bir otaqda boş yer yoxdur!");
    }

    @Transactional
    public void incrementRoomCount(Room room) {
        if (room.getCurrentCount() >= room.getCapacity()) {
            throw new ConflictException("Otaq artıq doludur!");
        }
        room.setCurrentCount(room.getCurrentCount() + 1);
        roomRepository.save(room);
    }

    // ---------------------------------------------------------
    // ✅✅✅ 11:30 (B otaqları) — SƏNİN QAYDA:
    // Fizika -> tək yerlər (1,3,5..)
    // Riyaziyyat 9 -> cüt yerlər (2,4,6..)
    // Əgər öz parity-si bitdisə -> qarşı parity-yə düşür (yanaşı)
    //
    // ÜSTÜNLÜK: A kimi “seatNo üzrə scan” edir:
    // əvvəl bütün otaqlarda seat 1 (fizika üçün), sonra seat 3..., sonra fallback
    // ---------------------------------------------------------
    @Transactional
    public RoomAssignment assignRoomFor1130Parity(SlotGroup group) {
        final String examTime = "11:30";

        // lock: eyni anda qeydiyyatlarda qarışıqlıq olmasın
        List<Room> rooms = roomRepository.findRoomsByPrefixOrderByIdAscForUpdate("B");

        if (rooms.isEmpty()) {
            throw new NotFoundException("11:30 üçün (B*) otaq tapılmadı!");
        }

        int maxCapacity = rooms.stream().mapToInt(Room::getCapacity).max().orElse(0);

        // 1) əvvəl preferred parity yerləri
        int startPreferred = (group == SlotGroup.PHYSICS) ? 1 : 2;
        RoomAssignment preferred = findBySeatScan(rooms, maxCapacity, startPreferred, 2, examTime);
        if (preferred != null) return preferred;

        // 2) preferred bitdisə -> opposite parity (yanaşı ola bilər)
        int startOpposite = (group == SlotGroup.PHYSICS) ? 2 : 1;
        RoomAssignment opposite = findBySeatScan(rooms, maxCapacity, startOpposite, 2, examTime);
        if (opposite != null) return opposite;

        // 3) ümumiyyətlə boş yer axtar (hər ehtimala)
        RoomAssignment any = findBySeatScan(rooms, maxCapacity, 1, 1, examTime);
        if (any != null) return any;

        throw new NotFoundException("11:30 üçün boş yer yoxdur!");
    }

    /**
     * Seat scan helper:
     * seatNo = start; seatNo += step
     * hər seatNo üçün otaqları id sırasıyla gəzib boş olanı tapır.
     */
    private RoomAssignment findBySeatScan(List<Room> rooms, int maxCapacity, int start, int step, String examTime) {
        for (int seatNo = start; seatNo <= maxCapacity; seatNo += step) {
            for (Room room : rooms) {
                if (seatNo > room.getCapacity()) continue;
                if (room.getBCurrentCount() >= room.getCapacity()) continue;

                // həmin otaqda həmin examTime üçün dolu yerlər
                Set<Integer> occupied = getOccupiedSeats(room.getId(), examTime);

                if (!occupied.contains(seatNo)) {
                    log.info("11:30 seat seçildi -> group={}, room={}, seat={}",
                            examTime, room.getRoomNo(), seatNo);
                    return new RoomAssignment(room, seatNo);
                }
            }
        }
        return null;
    }

    private Set<Integer> getOccupiedSeats(Long roomId, String examTime) {
        List<Integer> seats = studentRepository.findSeatNosByRoomAndTime(roomId, examTime);
        return new HashSet<>(seats);
    }

    // 11:30 üçün count artırma
    @Transactional
    public void incrementRoomCountFor1130(Room room, SlotGroup group) {
        if (room.getBCurrentCount() >= room.getCapacity()) {
            throw new ConflictException("Otaq artıq doludur!");
        }
        room.setBCurrentCount(room.getBCurrentCount() + 1);
        room.setBLastGroup(group.name());
        roomRepository.save(room);
    }

    // ---------------------------------------------------------
    // ✅ CRUD / Statistik
    // ---------------------------------------------------------
    public Room addRoom(Room room) {
        if (room.getCapacity() <= 0) {
            throw new BadRequestException("Otağın tutumu 0-dan böyük olmalıdır!");
        }

        // A
        room.setCurrentCount(0);
        // B
        room.setBCurrentCount(0);
        room.setBLastGroup(null);

        return roomRepository.save(room);
    }

    public int getRemainingSeats(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Otaq tapılmadı!"));

        // hansı prefixdirsə ona görə qalıq:
        boolean isA = room.getRoomNo() != null && room.getRoomNo().startsWith("A");
        int used = isA ? room.getCurrentCount() : room.getBCurrentCount();

        return room.getCapacity() - used;
    }

    public int getTotalCapacity() {
        return roomRepository.getTotalCapacity();
    }

    public int getTotalCurrentCount() {
        return roomRepository.getTotalCurrentCount();
    }

    public int getTotalBCurrentCount() {
        return roomRepository.getTotalBCurrentCount();
    }
}
