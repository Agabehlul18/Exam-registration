package com.math.examregistration.controller;

import com.math.examregistration.entity.Room;
import com.math.examregistration.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Otaq uğurla əlavə olundu ✅");
        response.put("room", savedRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 🔹 Otaq məlumatlarını yenilə
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        Room updatedRoom = roomService.updateRoom(id, room);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Otaq məlumatları uğurla yeniləndi ✏️");
        response.put("room", updatedRoom);
        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 Otağı sil
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Otaq uğurla silindi 🗑️");
        response.put("deletedRoomId", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 Qalan yerləri göstər
     */
    @GetMapping("/{id}/remaining")
    public ResponseEntity<Map<String, Object>> getRemainingSeats(@PathVariable Long id) {
        int remaining = roomService.getRemainingSeats(id);
        Map<String, Object> response = new HashMap<>();
        response.put("roomId", id);
        response.put("remainingSeats", remaining);
        response.put("status", remaining > 0 ? "mövcuddur ✅" : "dolu ❌");
        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 Ümumi tutum
     */
    @GetMapping("/total-capacity")
    public ResponseEntity<Map<String, Object>> getTotalCapacity() {
        int total = roomService.getTotalCapacity();
        return ResponseEntity.ok(Map.of("totalCapacity", total));
    }

    /**
     * 🔹 Ümumi doluluq
     */
    @GetMapping("/total-current")
    public ResponseEntity<Map<String, Object>> getTotalCurrentCount() {
        int total = roomService.getTotalCurrentCount();
        return ResponseEntity.ok(Map.of("totalCurrentCount", total));
    }
}
