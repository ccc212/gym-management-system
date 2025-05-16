package com.gymsys.service.equipment;

import com.gymsys.entity.equip.EquipmentRepair;

import java.util.List;

/**
 * 报修记录服务接口
 */
public interface EquipmentRepairService {

  /**
   * 新增报修记录
   * @param repair 报修实体
   * @return true成功，false失败
   */
  boolean addRepairRecord(EquipmentRepair repair);

  /**
   * 修改报修记录
   * @param repair 报修实体
   * @return true成功，false失败
   */
  boolean updateRepairRecord(EquipmentRepair repair);

  /**
   * 删除报修记录
   * @param id 报修记录ID
   * @return true成功，false失败
   */
  boolean deleteRepairRecord(Integer id);

  /**
   * 根据ID查询报修记录
   * @param id 报修记录ID
   * @return 报修记录实体
   */
  EquipmentRepair getById(Integer id);

  /**
   * 根据器材ID查询所有报修记录
   * @param equipmentId 器材ID
   * @return 报修记录列表
   */
  List<EquipmentRepair> listByEquipmentId(Integer equipmentId);

  /**
   * 查询所有报修记录
   * @return 报修记录列表
   */
  List<EquipmentRepair> listAll();
}
