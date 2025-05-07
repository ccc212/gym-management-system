package com.gymsys.service.equipment.impl;

import com.gymsys.service.equipment.BorrowService;
import org.springframework.stereotype.Service;

@Service
public class BorrowServiceImpl implements BorrowService {
    @Override
    public boolean lendEquipment(Integer equipmentId) {
        // 实现借出逻辑
        return true;
    }

    @Override
    public boolean returnEquipment(Integer equipmentId) {
        // 实现归还逻辑
        return true;
    }
}
