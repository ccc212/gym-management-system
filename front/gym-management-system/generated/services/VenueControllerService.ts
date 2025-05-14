/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Page_VenueEntity_ } from '../models/Page_VenueEntity_';
import type { ReservationEntity } from '../models/ReservationEntity';
import type { ReservationRequest } from '../models/ReservationRequest';
import type { TimeSlot } from '../models/TimeSlot';
import type { VenueEntity } from '../models/VenueEntity';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class VenueControllerService {
    /**
     * getAllVenues
     * @param page page
     * @param size size
     * @param type type
     * @returns Page_VenueEntity_ OK
     * @throws ApiError
     */
    public static getAllVenuesUsingGet(
        page: number = 1,
        size: number = 10,
        type?: string,
    ): CancelablePromise<Page_VenueEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venues',
            query: {
                'page': page,
                'size': size,
                'type': type,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * createVenue
     * @param venue venue
     * @returns VenueEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static createVenueUsingPost(
        venue: VenueEntity,
    ): CancelablePromise<VenueEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/venues',
            body: venue,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getAvailableVenues
     * @returns VenueEntity OK
     * @throws ApiError
     */
    public static getAvailableVenuesUsingGet(): CancelablePromise<Array<VenueEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venues/available',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getVenuesByType
     * @param type type
     * @param page page
     * @param size size
     * @returns Page_VenueEntity_ OK
     * @throws ApiError
     */
    public static getVenuesByTypeUsingGet(
        type: string,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_VenueEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venues/type/{type}',
            path: {
                'type': type,
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
     * getVenueTypes
     * @returns string OK
     * @throws ApiError
     */
    public static getVenueTypesUsingGet(): CancelablePromise<Array<string>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venues/types',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getVenueById
     * @param id id
     * @returns VenueEntity OK
     * @throws ApiError
     */
    public static getVenueByIdUsingGet(
        id: number,
    ): CancelablePromise<VenueEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venues/{id}',
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
     * updateVenue
     * @param id id
     * @param venue venue
     * @returns VenueEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateVenueUsingPut(
        id: number,
        venue: VenueEntity,
    ): CancelablePromise<VenueEntity | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/venues/{id}',
            path: {
                'id': id,
            },
            body: venue,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deleteVenue
     * @param id id
     * @returns any OK
     * @throws ApiError
     */
    public static deleteVenueUsingDelete(
        id: number,
    ): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/venues/{id}',
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
     * updateVenueStatus
     * @param id id
     * @param status status
     * @returns VenueEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateVenueStatusUsingPut(
        id: number,
        status: string,
    ): CancelablePromise<VenueEntity | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/venues/{id}/status',
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
     * createReservation
     * @param request request
     * @param venueId venueId
     * @returns ReservationEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static createReservationUsingPost1(
        request: ReservationRequest,
        venueId: number,
    ): CancelablePromise<ReservationEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/venues/{venueId}/reserve',
            path: {
                'venueId': venueId,
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
     * getTimeSlots
     * @param date date
     * @param venueId venueId
     * @returns TimeSlot OK
     * @throws ApiError
     */
    public static getTimeSlotsUsingGet(
        date: string,
        venueId: number,
    ): CancelablePromise<Array<TimeSlot>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venues/{venueId}/time-slots',
            path: {
                'venueId': venueId,
            },
            query: {
                'date': date,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
