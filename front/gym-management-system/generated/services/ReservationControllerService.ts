/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Page_ReservationEntity_ } from '../models/Page_ReservationEntity_';
import type { ReservationEntity } from '../models/ReservationEntity';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ReservationControllerService {
    /**
     * getAllReservations
     * @param endDate endDate
     * @param page page
     * @param size size
     * @param startDate startDate
     * @param status status
     * @param venueType venueType
     * @returns Page_ReservationEntity_ OK
     * @throws ApiError
     */
    public static getAllReservationsUsingGet(
        endDate?: string,
        page: number = 1,
        size: number = 10,
        startDate?: string,
        status?: string,
        venueType?: string,
    ): CancelablePromise<Page_ReservationEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations',
            query: {
                'endDate': endDate,
                'page': page,
                'size': size,
                'startDate': startDate,
                'status': status,
                'venueType': venueType,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * createReservation
     * @param reservation reservation
     * @returns ReservationEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static createReservationUsingPost(
        reservation: ReservationEntity,
    ): CancelablePromise<ReservationEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations',
            body: reservation,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * settleReservation
     * @param settlementData settlementData
     * @returns any OK
     * @throws ApiError
     */
    public static settleReservationUsingPost(
        settlementData: any,
    ): CancelablePromise<Record<string, any>> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/settle',
            body: settlementData,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getReservationsByStatus
     * @param status status
     * @param page page
     * @param size size
     * @returns Page_ReservationEntity_ OK
     * @throws ApiError
     */
    public static getReservationsByStatusUsingGet(
        status: string,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_ReservationEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/status/{status}',
            path: {
                'status': status,
            },
            query: {
                'page': page,
                'size': size,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getTodayReservations
     * @returns any OK
     * @throws ApiError
     */
    public static getTodayReservationsUsingGet(): CancelablePromise<Record<string, any>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/today',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getUserReservations
     * @param userId userId
     * @param endDate endDate
     * @param page page
     * @param size size
     * @param startDate startDate
     * @param status status
     * @returns Page_ReservationEntity_ OK
     * @throws ApiError
     */
    public static getUserReservationsUsingGet(
        userId: number,
        endDate?: string,
        page: number = 1,
        size: number = 10,
        startDate?: string,
        status?: string,
    ): CancelablePromise<Page_ReservationEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/user/{userId}',
            path: {
                'userId': userId,
            },
            query: {
                'endDate': endDate,
                'page': page,
                'size': size,
                'startDate': startDate,
                'status': status,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getReservationsByVenue
     * @param venueId venueId
     * @param page page
     * @param size size
     * @returns Page_ReservationEntity_ OK
     * @throws ApiError
     */
    public static getReservationsByVenueUsingGet(
        venueId: number,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_ReservationEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/venue/{venueId}',
            path: {
                'venueId': venueId,
            },
            query: {
                'page': page,
                'size': size,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getReservationById
     * @param id id
     * @returns ReservationEntity OK
     * @throws ApiError
     */
    public static getReservationByIdUsingGet(
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
     * deleteReservation
     * @param id id
     * @returns any OK
     * @throws ApiError
     */
    public static deleteReservationUsingDelete(
        id: number,
    ): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/reservations/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
            },
        });
    }
    /**
     * approveReservation
     * @param id id
     * @param request request
     * @returns any OK
     * @throws ApiError
     */
    public static approveReservationUsingPut(
        id: number,
        request?: Record<string, string>,
    ): CancelablePromise<Record<string, any>> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/reservations/{id}/approve',
            path: {
                'id': id,
            },
            body: request,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * cancelReservation
     * @param id id
     * @param reason reason
     * @returns any OK
     * @throws ApiError
     */
    public static cancelReservationUsingPut(
        id: number,
        reason?: string,
    ): CancelablePromise<Record<string, any>> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/reservations/{id}/cancel',
            path: {
                'id': id,
            },
            query: {
                'reason': reason,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * rejectReservation
     * @param id id
     * @param request request
     * @returns any OK
     * @throws ApiError
     */
    public static rejectReservationUsingPut(
        id: number,
        request?: Record<string, string>,
    ): CancelablePromise<Record<string, any>> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/reservations/{id}/reject',
            path: {
                'id': id,
            },
            body: request,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * updateReservationStatus
     * @param id id
     * @param status status
     * @returns ReservationEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateReservationStatusUsingPut(
        id: number,
        status: string,
    ): CancelablePromise<ReservationEntity | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/reservations/{id}/status',
            path: {
                'id': id,
            },
            query: {
                'status': status,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getUsageDetail
     * @param id id
     * @returns any OK
     * @throws ApiError
     */
    public static getUsageDetailUsingGet(
        id: number,
    ): CancelablePromise<Record<string, any>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/{id}/usage',
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
