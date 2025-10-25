package com.math.examregistration.controller;

import com.math.examregistration.entity.Room;
import com.math.examregistration.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    /**
     * 🔹 Bütün otaqları gətir
     */
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * 🔹 Yeni otaq əlavə et
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addRoom(@RequestBody Room room) {
        Room savedRoom = roomService.addRoom(room);
        return ResponseEntity.ok(Map.of(
                "message", "Otaq uğurla əlavə olundu ✅",
                "room", savedRoom
        ));
    }

    /**
     * 🔹 Otaq məlumatlarını yenilə
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        Room updatedRoom = roomService.updateRoom(id, room);
        return ResponseEntity.ok(Map.of(
                "message", "Otaq məlumatları uğurla yeniləndi ✏️",
                "room", updatedRoom
        ));
    }

    /**
     * 🔹 Otağı sil
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(Map.of(
                "message", "Otaq uğurla silindi 🗑️",
                "deletedRoomId", id
        ));
    }

    /**
     * 🔹 Qalan yerləri göstər
     */
    @GetMapping("/{id}/remaining")
    public ResponseEntity<Map<String, Object>> getRemainingSeats(@PathVariable Long id) {
        int remaining = roomService.getRemainingSeats(id);
        return ResponseEntity.ok(Map.of(
                "roomId", id,
                "remainingSeats", remaining,
                "status", remaining > 0 ? "mövcuddur ✅" : "dolu ❌"
        ));
    }

    @GetMapping("/total-capacity")
    public int getTotalCapacity() {
        return roomService.getTotalCapacity();
    }

    @GetMapping("/total-current")
    public int getTotalCurrentCount() {
        return roomService.getTotalCurrentCount();
    }

}
