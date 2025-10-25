package com.math.examregistration.repository;

import com.math.examregistration.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.currentCount < r.capacity ORDER BY r.id ASC")
    List<Room> findAvailableRooms(Pageable pageable);

    default Optional<Room> findAvailableRoom() {
        List<Room> rooms = findAvailableRooms(Pageable.ofSize(1));
        return rooms.isEmpty() ? Optional.empty() : Optional.of(rooms.get(0));
    }
    @Query("SELECT COALESCE(SUM(r.capacity), 0) FROM Room r")
    int getTotalCapacity();

    @Query("SELECT COALESCE(SUM(r.currentCount), 0) FROM Room r")
    int getTotalCurrentCount();

}
