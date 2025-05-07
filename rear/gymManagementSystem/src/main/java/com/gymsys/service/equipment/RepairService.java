package com.gymsys.service.equipment;

import com.gymsys.entity.equip.Equipment;

import java.util.Date;


public interface RepairService {
  boolean reportRepair(Equipment equipment, String reason);

  boolean reportRepair(Integer equipmentId, String reason, Date repairTime);
}
