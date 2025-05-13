import http from "../../../http";
import type { Equipment } from "./EquipmentModel.ts";

// 添加器材
export function addApi(parm: Equipment) {
    return http.post("/api/equip/equipment", parm);
}

// 修改器材信息
export function updateApi(parm: Equipment) {
    return http.put("/api/equip/equipment", parm);
}

// 删除器材
export function deleteApi(id: string) {
    return http.delete(`/api/equip/equipment/${id}`);
}

// 获取器材列表（分页+条件查询）
export function getListApi(parm: any) {
    return http.get("/api/equip/equipment/getList", parm);
}

// 获取器材下拉列表（用于前端选择）
export function selectListApi() {
    return http.get("/api/equip/equipment/selectList");
}

// 提交器材报修申请
export function reportRepairApi(id: string, reason: string) {
    return http.post(`/api/equip/equipment/repair/reportRepair/${id}`, { reason });
}

// 管理员获取报修记录
export function approveRepairApi(equipmentId: string, approved: boolean) {
    return http.post("/api/admin/approval/repair", { equipmentId, approved });
}

export function approveRentApi(equipmentId: string, approved: boolean) {
    return http.post("/api/admin/approval/rent", { equipmentId, approved });
}

export function getRepairRecordsApi() {
    return http.get("/api/equip/equipment/repair/admin/getRepairRecords");
}

// 借出器材
export function lendEquipmentApi(equipmentId: number) {
    return http.post("/api/borrow/lend", { equipmentId });
}

// 归还器材
export function returnEquipmentApi(equipmentId: number) {
    return http.post("/api/borrow/return", { equipmentId });
}