/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Result } from '../models/Result';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class BorrowControllerService {
    /**
     * lendEquipment
     * @param equipmentId equipmentId
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static lendEquipmentUsingPost(
        equipmentId: number,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/borrow/lend',
            query: {
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
     * returnEquipment
     * @param equipmentId equipmentId
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static returnEquipmentUsingPost(
        equipmentId: number,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/borrow/return',
            query: {
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
