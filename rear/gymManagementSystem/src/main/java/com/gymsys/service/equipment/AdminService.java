package com.gymsys.service.equipment;

import com.gymsys.entity.Result;

public interface AdminService { /**
 * 审核器材报修
 * @param equipmentId 器材ID
 * @param approved 是否审核通过
 * @return 操作结果
 */
Result approveRepair(Integer equipmentId, boolean approved);

    /**
     * 审核器材借出
     * @param equipmentId 器材ID
     * @param approved 是否审核通过
     * @return 操作结果
     */
    Result approveRent(Integer equipmentId, boolean approved);
}
