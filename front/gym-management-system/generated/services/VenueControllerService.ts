/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { VenueEntity } from '../models/VenueEntity';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class VenueControllerService {
    /**
     * getAllVenues
     * @returns VenueEntity OK
     * @throws ApiError
     */
    public static getAllVenuesUsingGet(): CancelablePromise<Array<VenueEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venues',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * addVenue
     * @param venue venue
     * @returns VenueEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static addVenueUsingPost(
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
     * @returns VenueEntity OK
     * @throws ApiError
     */
    public static getVenuesByTypeUsingGet(
        type: string,
    ): CancelablePromise<Array<VenueEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venues/type/{type}',
            path: {
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
     * getVenue
     * @param id id
     * @returns VenueEntity OK
     * @throws ApiError
     */
    public static getVenueUsingGet(
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
     * removeVenue
     * @param id id
     * @returns any OK
     * @throws ApiError
     */
    public static removeVenueUsingDelete(
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
}
