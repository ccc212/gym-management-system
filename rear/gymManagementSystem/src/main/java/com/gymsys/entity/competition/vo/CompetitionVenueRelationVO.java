package com.gymsys.entity.competition.vo;

import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.gymsys.entity.competition.CompetitionVenueRelation;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CompetitionVenueRelationVO extends CompetitionVenueRelation {

    private String venueName;
}
