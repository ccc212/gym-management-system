package com.gymsys.mapper.equipment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.equip.EquipmentRepair;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EquipmentRepairMapper extends BaseMapper<EquipmentRepair> {
    // 继承了BaseMapper即可获得增删改查等基础功能，若需自定义复杂SQL再补充
}