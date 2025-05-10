export interface Equipment {
    id: string;
    name: string;
    status: number;
    createdAt: string;
    updatedAt: string;
    approvalStatus: string;
}

export interface RepairRecord {
    equipmentId: string;
    reason: string;
    reportTime: Date;
    repairStatus: number;
}