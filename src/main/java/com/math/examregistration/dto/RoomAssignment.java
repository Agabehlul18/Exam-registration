package com.math.examregistration.dto;

import com.math.examregistration.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomAssignment {
    private Room room;
    private int seatNo;
}
