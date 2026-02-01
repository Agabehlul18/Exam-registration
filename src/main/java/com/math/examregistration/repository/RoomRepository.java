package com.math.examregistration.repository;

import com.math.examregistration.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // A və B otaqlarını prefix ilə gətir (LOCKSIZ)
    @Query("SELECT r FROM Room r WHERE r.roomNo LIKE CONCAT(:prefix, '%') ORDER BY r.id ASC")
    List<Room> findRoomsByPrefixOrderByIdAsc(@Param("prefix") String prefix);

    // 11:30 (B otaqları) üçün LOCK-lu
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.roomNo LIKE CONCAT(:prefix, '%') ORDER BY r.id ASC")
    List<Room> findRoomsByPrefixOrderByIdAscForUpdate(@Param("prefix") String prefix);

    // Ümumi tutum
    @Query("SELECT COALESCE(SUM(r.capacity), 0) FROM Room r")
    int getTotalCapacity();

    // 10:00 A otaqlarının doluluğu (currentCount)
    @Query("SELECT COALESCE(SUM(r.currentCount), 0) FROM Room r")
    int getTotalCurrentCount();

    // 11:30 B otaqlarının doluluğu (bCurrentCount)
    @Query("SELECT COALESCE(SUM(r.bCurrentCount), 0) FROM Room r")
    int getTotalBCurrentCount();

    // Əgər “hər hansı boş otaq” lazım olsa:
    @Query("SELECT r FROM Room r WHERE r.currentCount < r.capacity ORDER BY r.id ASC")
    List<Room> findFirstAvailableRoom();

    default Optional<Room> findAvailableRoom() {
        List<Room> rooms = findFirstAvailableRoom();
        return rooms.isEmpty() ? Optional.empty() : Optional.of(rooms.get(0));
    }
}
