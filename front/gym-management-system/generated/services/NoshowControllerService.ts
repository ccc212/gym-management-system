/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { NoshowDTO } from '../models/NoshowDTO';
import type { NoshowEntity } from '../models/NoshowEntity';
import type { Page_NoshowEntity_ } from '../models/Page_NoshowEntity_';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class NoshowControllerService {
    /**
     * getNoshows
     * @param endDate endDate
     * @param page page
     * @param size size
     * @param startDate startDate
     * @param venueId venueId
     * @param venueType venueType
     * @returns Page_NoshowEntity_ OK
     * @throws ApiError
     */
    public static getNoshowsUsingGet(
        endDate?: string,
        page: number = 1,
        size: number = 10,
        startDate?: string,
        venueId?: number,
        venueType?: string,
    ): CancelablePromise<Page_NoshowEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/noshows',
            query: {
                'endDate': endDate,
                'page': page,
                'size': size,
                'startDate': startDate,
                'venueId': venueId,
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
     * createTestNoshows
     * @returns NoshowEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static createTestNoshowsUsingPost(): CancelablePromise<Array<NoshowEntity> | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/noshows/test/create',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getNoshowDetail
     * @param id id
     * @returns NoshowEntity OK
     * @throws ApiError
     */
    public static getNoshowDetailUsingGet(
        id: number,
    ): CancelablePromise<NoshowEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/noshows/{id}',
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
     * handleNoshow
     * @param dto dto
     * @param id id
     * @returns NoshowEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static handleNoshowUsingPost(
        dto: NoshowDTO,
        id: number,
    ): CancelablePromise<NoshowEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/noshows/{id}/handle',
            path: {
                'id': id,
            },
            body: dto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
