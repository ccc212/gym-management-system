/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Result } from '../models/Result';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AdminControllerService {
    /**
     * approveRent
     * @param approved approved
     * @param equipmentId equipmentId
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static approveRentUsingPost(
        approved: boolean,
        equipmentId: number,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/admin/approval/rent',
            query: {
                'approved': approved,
                'equipmentId': equipmentId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * approveRepair
     * @param approved approved
     * @param equipmentId equipmentId
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static approveRepairUsingPost(
        approved: boolean,
        equipmentId: number,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/admin/approval/repair',
            query: {
                'approved': approved,
                'equipmentId': equipmentId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
