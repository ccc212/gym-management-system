/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ReservationEntity } from './ReservationEntity';
import type { VenueEntity } from './VenueEntity';
export type UsageEntity = {
    cardNumber?: string;
    cost?: number;
    endTime?: string;
    id?: number;
    paid?: boolean;
    remark?: string;
    reservation?: ReservationEntity;
    startTime?: string;
    venue?: VenueEntity;
};

