/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ReservationRequest } from '../models/ReservationRequest';
import type { Result_List_string_ } from '../models/Result_List_string_';
import type { Result_List_TimeSlot_ } from '../models/Result_List_TimeSlot_';
import type { Result_List_VenueEntity_ } from '../models/Result_List_VenueEntity_';
import type { Result_Page_VenueEntity_ } from '../models/Result_Page_VenueEntity_';
import type { Result_ReservationEntity_ } from '../models/Result_ReservationEntity_';
import type { Result_VenueEntity_ } from '../models/Result_VenueEntity_';
import type { Result_Void_ } from '../models/Result_Void_';
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
     * @returns Result_Page_VenueEntity_ OK
     * @throws ApiError
     */
    public static getAllVenuesUsingGet(
        page: number = 1,
        size: number = 10,
        type?: string,
    ): CancelablePromise<Result_Page_VenueEntity_> {
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
     * @returns Result_VenueEntity_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static createVenueUsingPost(
        venue: VenueEntity,
    ): CancelablePromise<Result_VenueEntity_ | any> {
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
     * @returns Result_List_VenueEntity_ OK
     * @throws ApiError
     */
    public static getAvailableVenuesUsingGet(): CancelablePromise<Result_List_VenueEntity_> {
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
     * @returns Result_List_VenueEntity_ OK
     * @throws ApiError
     */
    public static getVenuesByTypeUsingGet(
        type: string,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Result_List_VenueEntity_> {
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
     * @returns Result_List_string_ OK
     * @throws ApiError
     */
    public static getVenueTypesUsingGet(): CancelablePromise<Result_List_string_> {
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
     * @returns Result_VenueEntity_ OK
     * @throws ApiError
     */
    public static getVenueByIdUsingGet(
        id: number,
    ): CancelablePromise<Result_VenueEntity_> {
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
     * @returns Result_VenueEntity_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateVenueUsingPut(
        id: number,
        venue: VenueEntity,
    ): CancelablePromise<Result_VenueEntity_ | any> {
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
     * @returns Result_Void_ OK
     * @throws ApiError
     */
    public static deleteVenueUsingDelete(
        id: number,
    ): CancelablePromise<Result_Void_> {
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
     * @returns Result_VenueEntity_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateVenueStatusUsingPut(
        id: number,
        status: string,
    ): CancelablePromise<Result_VenueEntity_ | any> {
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
     * @returns Result_ReservationEntity_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static createReservationUsingPost1(
        request: ReservationRequest,
        venueId: number,
    ): CancelablePromise<Result_ReservationEntity_ | any> {
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
     * @returns Result_List_TimeSlot_ OK
     * @throws ApiError
     */
    public static getTimeSlotsUsingGet(
        date: string,
        venueId: number,
    ): CancelablePromise<Result_List_TimeSlot_> {
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
