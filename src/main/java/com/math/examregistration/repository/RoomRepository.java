package com.math.examregistration.repository;

import com.math.examregistration.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Bütün boş otaqlar, sırf id üzrə (sırf ümumi boş otaqlar üçün)
    @Query("SELECT r FROM Room r WHERE r.currentCount < r.capacity ORDER BY r.id ASC")
    List<Room> findAvailableRooms(Pageable pageable);

    default Optional<Room> findAvailableRoom() {
        List<Room> rooms = findAvailableRooms(Pageable.ofSize(1));
        return rooms.isEmpty() ? Optional.empty() : Optional.of(rooms.get(0));
    }

    // Ümumi tutum
    @Query("SELECT COALESCE(SUM(r.capacity), 0) FROM Room r")
    int getTotalCapacity();

    @Query("SELECT COALESCE(SUM(r.currentCount), 0) FROM Room r")
    int getTotalCurrentCount();

    // 🔹 Prefiksə görə otaqları gətir (ORDER BY currentCount ASC, roomNo ASC)
    @Query("SELECT r FROM Room r WHERE r.roomNo LIKE CONCAT(:prefix, '%') AND r.currentCount < r.capacity ORDER BY r.currentCount ASC, r.roomNo ASC")
    List<Room> findAvailableRoomsByPrefix(@Param("prefix") String prefix, Pageable pageable);

    default Room findAvailableRoomByPrefixSingle(String prefix) {
        List<Room> rooms = findAvailableRoomsByPrefix(prefix, Pageable.ofSize(1));
        if (rooms.isEmpty()) {
            throw new RuntimeException(prefix + " ilə başlayan boş otaq yoxdur!");
        }
        return rooms.get(0);
    }
    @Query("SELECT r FROM Room r WHERE r.roomNo LIKE CONCAT(:prefix, '%') ORDER BY r.id ASC")
    List<Room> findByRoomNoStartingWithOrderByIdAsc(@Param("prefix") String prefix);

}
