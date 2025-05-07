package com.gymsys.service.equipment;

public interface BorrowService {
    /**
     * 借出器材
     */
    boolean lendEquipment(Integer equipmentId);
    
    /**
     * 归还器材
     */
    boolean returnEquipment(Integer equipmentId);
}
