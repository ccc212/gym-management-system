/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CompetitionEquipmentRelationVO } from './CompetitionEquipmentRelationVO';
import type { CompetitionItem } from './CompetitionItem';
import type { CompetitionVenueRelationVO } from './CompetitionVenueRelationVO';
export type CompetitionDetailVO = {
    category?: number;
    description?: string;
    endTime?: string;
    equipmentRelations?: Array<CompetitionEquipmentRelationVO>;
    hoster?: string;
    id?: number;
    isTeamCompetition?: number;
    itemRelations?: Array<CompetitionItem>;
    maxSignUpNum?: number;
    name?: string;
    requirement?: string;
    signUpDeadline?: string;
    signUpNum?: number;
    startTime?: string;
    status?: number;
    teamMaxNum?: number;
    teamMinNum?: number;
    type?: number;
    venueRelations?: Array<CompetitionVenueRelationVO>;
};

