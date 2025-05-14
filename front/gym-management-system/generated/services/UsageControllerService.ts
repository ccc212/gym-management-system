/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Page_UsageEntity_ } from '../models/Page_UsageEntity_';
import type { UsageEntity } from '../models/UsageEntity';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class UsageControllerService {
    /**
     * startUsage
     * @param usage usage
     * @returns UsageEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static startUsageUsingPost(
        usage: UsageEntity,
    ): CancelablePromise<UsageEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/usages',
            body: usage,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getActiveUsages
     * @param page page
     * @param size size
     * @returns Page_UsageEntity_ OK
     * @throws ApiError
     */
    public static getActiveUsagesUsingGet(
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_UsageEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/usages/active',
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
     * getUsagesByCardNumber
     * @param cardNumber cardNumber
     * @param page page
     * @param size size
     * @returns Page_UsageEntity_ OK
     * @throws ApiError
     */
    public static getUsagesByCardNumberUsingGet(
        cardNumber: string,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_UsageEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/usages/card/{cardNumber}',
            path: {
                'cardNumber': cardNumber,
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
     * getUsagesByTimeRange
     * @param endTime endTime
     * @param startTime startTime
     * @param page page
     * @param size size
     * @returns Page_UsageEntity_ OK
     * @throws ApiError
     */
    public static getUsagesByTimeRangeUsingGet(
        endTime: string,
        startTime: string,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_UsageEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/usages/time-range',
            query: {
                'endTime': endTime,
                'page': page,
                'size': size,
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
     * getUserUsages
     * @param userId userId
     * @param page page
     * @param size size
     * @returns Page_UsageEntity_ OK
     * @throws ApiError
     */
    public static getUserUsagesUsingGet(
        userId: number,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_UsageEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/usages/user/{userId}',
            path: {
                'userId': userId,
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
     * getVenueUsages
     * @param venueId venueId
     * @param page page
     * @param size size
     * @returns Page_UsageEntity_ OK
     * @throws ApiError
     */
    public static getVenueUsagesUsingGet(
        venueId: number,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_UsageEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/usages/venue/{venueId}',
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
     * endUsage
     * @param id id
     * @returns UsageEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static endUsageUsingPut(
        id: number,
    ): CancelablePromise<UsageEntity | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/usages/{id}/end',
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
     * payUsage
     * @param id id
     * @returns UsageEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static payUsageUsingPut(
        id: number,
    ): CancelablePromise<UsageEntity | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/usages/{id}/pay',
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
