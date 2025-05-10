/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { UsageEntity } from '../models/UsageEntity';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class UsageControllerService {
    /**
     * getActiveUsages
     * @returns UsageEntity OK
     * @throws ApiError
     */
    public static getActiveUsagesUsingGet(): CancelablePromise<Array<UsageEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/usages/active',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getVenuePrice
     * @param venueId venueId
     * @returns number OK
     * @throws ApiError
     */
    public static getVenuePriceUsingGet(
        venueId: number,
    ): CancelablePromise<Record<string, number>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/usages/price/{venueId}',
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
     * startVenueUsage
     * @param cardNumber cardNumber
     * @param venueId venueId
     * @param reservationId reservationId
     * @returns UsageEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static startVenueUsageUsingPost(
        cardNumber: string,
        venueId: number,
        reservationId?: number,
    ): CancelablePromise<UsageEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/usages/start',
            query: {
                'cardNumber': cardNumber,
                'reservationId': reservationId,
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
     * getUnpaidUsages
     * @param cardNumber cardNumber
     * @returns UsageEntity OK
     * @throws ApiError
     */
    public static getUnpaidUsagesUsingGet(
        cardNumber: string,
    ): CancelablePromise<Array<UsageEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/usages/unpaid/{cardNumber}',
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
     * endVenueUsage
     * @param id id
     * @returns UsageEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static endVenueUsageUsingPost(
        id: number,
    ): CancelablePromise<UsageEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
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
    public static payUsageUsingPost(
        id: number,
    ): CancelablePromise<UsageEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
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
