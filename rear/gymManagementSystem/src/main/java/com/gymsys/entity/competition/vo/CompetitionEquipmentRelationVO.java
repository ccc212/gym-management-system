package com.gymsys.entity.competition.vo;

import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CompetitionEquipmentRelationVO extends CompetitionEquipmentRelation {

    private String equipmentName;
}
