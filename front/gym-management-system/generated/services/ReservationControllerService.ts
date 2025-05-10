/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ReservationEntity } from '../models/ReservationEntity';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ReservationControllerService {
    /**
     * reserveVenue
     * @param cardNumber cardNumber
     * @param endTime endTime
     * @param startTime startTime
     * @param venueId venueId
     * @returns ReservationEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static reserveVenueUsingPost(
        cardNumber: string,
        endTime: string,
        startTime: string,
        venueId: number,
    ): CancelablePromise<ReservationEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations',
            query: {
                'cardNumber': cardNumber,
                'endTime': endTime,
                'startTime': startTime,
                'venueId': venueId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * reserveVenueForClass
     * @param endTime endTime
     * @param startTime startTime
     * @param venueId venueId
     * @returns ReservationEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static reserveVenueForClassUsingPost(
        endTime: string,
        startTime: string,
        venueId: number,
    ): CancelablePromise<ReservationEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/class',
            query: {
                'endTime': endTime,
                'startTime': startTime,
                'venueId': venueId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * reserveVenueForTeam
     * @param endTime endTime
     * @param startTime startTime
     * @param venueId venueId
     * @returns ReservationEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static reserveVenueForTeamUsingPost(
        endTime: string,
        startTime: string,
        venueId: number,
    ): CancelablePromise<ReservationEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/team',
            query: {
                'endTime': endTime,
                'startTime': startTime,
                'venueId': venueId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getUserReservations
     * @param cardNumber cardNumber
     * @returns ReservationEntity OK
     * @throws ApiError
     */
    public static getUserReservationsUsingGet(
        cardNumber: string,
    ): CancelablePromise<Array<ReservationEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/user/{cardNumber}',
            path: {
                'cardNumber': cardNumber,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getVenueWeeklyReservations
     * @param venueId venueId
     * @returns ReservationEntity OK
     * @throws ApiError
     */
    public static getVenueWeeklyReservationsUsingGet(
        venueId: number,
    ): CancelablePromise<Array<ReservationEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/venue/{venueId}/weekly',
            path: {
                'venueId': venueId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getReservation
     * @param id id
     * @returns ReservationEntity OK
     * @throws ApiError
     */
    public static getReservationUsingGet(
        id: number,
    ): CancelablePromise<ReservationEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * modifyReservation
     * @param cardNumber cardNumber
     * @param endTime endTime
     * @param id id
     * @param startTime startTime
     * @returns ReservationEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static modifyReservationUsingPut(
        cardNumber: string,
        endTime: string,
        id: number,
        startTime: string,
    ): CancelablePromise<ReservationEntity | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/reservations/{id}',
            path: {
                'id': id,
            },
            query: {
                'cardNumber': cardNumber,
                'endTime': endTime,
                'startTime': startTime,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * cancelReservation
     * @param cardNumber cardNumber
     * @param id id
     * @returns any OK
     * @throws ApiError
     */
    public static cancelReservationUsingPost(
        cardNumber: string,
        id: number,
    ): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/{id}/cancel',
            path: {
                'id': id,
            },
            query: {
                'cardNumber': cardNumber,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * handleNoShow
     * @param id id
     * @returns any OK
     * @throws ApiError
     */
    public static handleNoShowUsingPost(
        id: number,
    ): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/{id}/no-show',
            path: {
                'id': id,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
