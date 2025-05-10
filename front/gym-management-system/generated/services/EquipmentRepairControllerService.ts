/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Result } from '../models/Result';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class EquipmentRepairControllerService {
    /**
     * getRepairRecords
     * @returns Result OK
     * @throws ApiError
     */
    public static getRepairRecordsUsingGet(): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/equipment/repair/admin/getRepairRecords',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * reportRepair
     * @param id id
     * @param reason reason
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static reportRepairUsingPost(
        id: number,
        reason: string,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/equipment/repair/reportRepair/{id}',
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
}
