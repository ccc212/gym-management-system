/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CompetitionEquipmentRelation } from './CompetitionEquipmentRelation';
import type { CompetitionVenueRelation } from './CompetitionVenueRelation';
export type CompetitionDetailVO = {
    category?: number;
    description?: string;
    endTime?: string;
    equipmentRelations?: Array<CompetitionEquipmentRelation>;
    hoster?: string;
    id?: number;
    isTeamCompetition?: number;
    name?: string;
    requirement?: string;
    signUpDeadline?: string;
    startTime?: string;
    status?: number;
    type?: number;
    venueRelations?: Array<CompetitionVenueRelation>;
};

