/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { VenueEntity } from './VenueEntity';
export type ReservationEntity = {
    actualCost?: number;
    actualEndTime?: string;
    actualStartTime?: string;
    cancelReason?: string;
    cardNumber?: string;
    cost?: number;
    createdTime?: string;
    date?: string;
    duration?: string;
    endTime?: string;
    id?: number;
    numberOfPeople?: number;
    paymentMethod?: string;
    paymentTime?: string;
    remarks?: string;
    reservationType?: string;
    startTime?: string;
    status?: string;
    updatedTime?: string;
    userId?: number;
    venueId?: number;
    venueInfo?: VenueEntity;
};

