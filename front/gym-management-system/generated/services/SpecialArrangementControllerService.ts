/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Result_Page_SpecialArrangement_ } from '../models/Result_Page_SpecialArrangement_';
import type { Result_SpecialArrangement_ } from '../models/Result_SpecialArrangement_';
import type { Result_Void_ } from '../models/Result_Void_';
import type { SpecialArrangementDTO } from '../models/SpecialArrangementDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class SpecialArrangementControllerService {
    /**
     * getSpecialArrangements
     * @param endDate endDate
     * @param page page
     * @param size size
     * @param startDate startDate
     * @param venueId venueId
     * @param venueType venueType
     * @returns Result_Page_SpecialArrangement_ OK
     * @throws ApiError
     */
    public static getSpecialArrangementsUsingGet(
        endDate?: string,
        page: number = 1,
        size: number = 10,
        startDate?: string,
        venueId?: number,
        venueType?: string,
    ): CancelablePromise<Result_Page_SpecialArrangement_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/special-arrangements',
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
     * createSpecialArrangement
     * @param dto dto
     * @returns Result_SpecialArrangement_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static createSpecialArrangementUsingPost(
        dto: SpecialArrangementDTO,
    ): CancelablePromise<Result_SpecialArrangement_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/special-arrangements',
            body: dto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * updateSpecialArrangement
     * @param dto dto
     * @param id id
     * @returns Result_SpecialArrangement_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateSpecialArrangementUsingPut(
        dto: SpecialArrangementDTO,
        id: number,
    ): CancelablePromise<Result_SpecialArrangement_ | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/special-arrangements/{id}',
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
    /**
     * deleteSpecialArrangement
     * @param id id
     * @returns Result_Void_ OK
     * @throws ApiError
     */
    public static deleteSpecialArrangementUsingDelete(
        id: number,
    ): CancelablePromise<Result_Void_> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/special-arrangements/{id}',
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
