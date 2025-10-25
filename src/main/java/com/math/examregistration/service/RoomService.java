package com.math.examregistration.service;

import com.math.examregistration.entity.Room;
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

    /**
     * 🔹 Boş otağı tap (capacity > currentCount)
     */
    public Room assignAvailableRoom() {
        return roomRepository.findAvailableRoom()
                .orElseThrow(() -> new RuntimeException("Boş otaq yoxdur!"));
    }

    /**
     * 🔹 Otaq doluluğunu 1 artır
     */
    @Transactional
    public void incrementRoomCount(Room room) {
        if (room.getCurrentCount() >= room.getCapacity()) {
            throw new RuntimeException("Otaq artıq doludur!");
        }
        room.setCurrentCount(room.getCurrentCount() + 1);
        roomRepository.save(room);
        log.info("Otaq #{} üçün say artırıldı → cari say: {}", room.getRoomNo(), room.getCurrentCount());
    }

    /**
     * 🔹 Bütün otaqları gətir (doluluq məlumatı ilə)
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        log.info("Sistemdə {} otaq tapıldı", rooms.size());
        return rooms;
    }

    /**
     * 🔹 Yeni otaq əlavə et
     */
    public Room addRoom(Room room) {
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Otağın tutumu 0-dan böyük olmalıdır!");
        }
        room.setCurrentCount(0);
        Room saved = roomRepository.save(room);
        log.info("Yeni otaq əlavə edildi: #{} (Tutum: {})", saved.getRoomNo(), saved.getCapacity());
        return saved;
    }

    /**
     * 🔹 Otaq məlumatını yenilə
     */
    @Transactional
    public Room updateRoom(Long id, Room updatedRoom) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Otaq tapılmadı!"));

        if (updatedRoom.getCapacity() < existingRoom.getCurrentCount()) {
            throw new IllegalArgumentException("Yeni tutum mövcud doluluqdan az ola bilməz!");
        }

        existingRoom.setRoomNo(updatedRoom.getRoomNo());
        existingRoom.setCapacity(updatedRoom.getCapacity());

        Room saved = roomRepository.save(existingRoom);
        log.info("Otaq #{} məlumatları yeniləndi", saved.getRoomNo());
        return saved;
    }

    /**
     * 🔹 Otağı sil
     */
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Silinəcək otaq tapılmadı!");
        }
        roomRepository.deleteById(id);
        log.info("Otaq #{} silindi", id);
    }

    /**
     * 🔹 Qalan yerlərin sayını göstər
     */
    public int getRemainingSeats(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Otaq tapılmadı!"));
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
