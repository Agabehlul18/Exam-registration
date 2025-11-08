package com.math.examregistration.service;

import com.math.examregistration.entity.Room;
import com.math.examregistration.exception.BadRequestException;
import com.math.examregistration.exception.ConflictException;
import com.math.examregistration.exception.NotFoundException;
import com.math.examregistration.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    public Room assignAvailableRoomByTime(String examTime) {
        String prefix = examTime.startsWith("10") ? "A" : "B";
        log.info("Vaxta görə otaq axtarılır: {} -> prefix: {}", examTime, prefix);

        List<Room> rooms = roomRepository.findByRoomNoStartingWithOrderByIdAsc(prefix);

        for (Room room : rooms) {
            if (room.getCurrentCount() < room.getCapacity()) {
                return room;
            }
        }

        throw new NotFoundException(examTime + " saatına boş otaq yoxdur!");
    }

    public Room assignAvailableRoom() {
        return roomRepository.findAvailableRoom()
                .orElseThrow(() -> new NotFoundException("Boş otaq yoxdur!"));
    }

    @Transactional
    public void incrementRoomCount(Room room) {
        if (room.getCurrentCount() >= room.getCapacity()) {
            throw new ConflictException("Otaq artıq doludur!");
        }
        room.setCurrentCount(room.getCurrentCount() + 1);
        roomRepository.save(room);
        log.info("Otaq #{} üçün say artırıldı → cari say: {}", room.getRoomNo(), room.getCurrentCount());
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        log.info("Sistemdə {} otaq tapıldı", rooms.size());
        return rooms;
    }

    public Room addRoom(Room room) {
        if (room.getCapacity() <= 0) {
            throw new BadRequestException("Otağın tutumu 0-dan böyük olmalıdır!");
        }
        room.setCurrentCount(0);
        Room saved = roomRepository.save(room);
        log.info("Yeni otaq əlavə edildi: #{} (Tutum: {})", saved.getRoomNo(), saved.getCapacity());
        return saved;
    }

    @Transactional
    public Room updateRoom(Long id, Room updatedRoom) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Otaq tapılmadı!"));

        if (updatedRoom.getCapacity() < existingRoom.getCurrentCount()) {
            throw new BadRequestException("Yeni tutum mövcud doluluqdan az ola bilməz!");
        }

        existingRoom.setRoomNo(updatedRoom.getRoomNo());
        existingRoom.setCapacity(updatedRoom.getCapacity());

        Room saved = roomRepository.save(existingRoom);
        log.info("Otaq #{} məlumatları yeniləndi", saved.getRoomNo());
        return saved;
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new NotFoundException("Silinəcək otaq tapılmadı!");
        }
        roomRepository.deleteById(id);
        log.info("Otaq #{} silindi", id);
    }

    public int getRemainingSeats(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Otaq tapılmadı!"));
        int remaining = room.getCapacity() - room.getCurrentCount();
        log.info("Otaq #{} üçün qalan yerlər: {}", room.getRoomNo(), remaining);
        return remaining;
    }

    public int getTotalCapacity() {
        int total = roomRepository.getTotalCapacity();
        log.info("Bütün otaqların ümumi tutumu: {}", total);
        return total;
    }

    public int getTotalCurrentCount() {
        int total = roomRepository.getTotalCurrentCount();
        log.info("Bütün otaqlarda hal-hazırda olan tələbələrin ümumi sayı: {}", total);
        return total;
    }
}
