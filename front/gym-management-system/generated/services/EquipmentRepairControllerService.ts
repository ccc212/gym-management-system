/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { EquipmentRepair } from '../models/EquipmentRepair';
import type { Result } from '../models/Result';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class EquipmentRepairControllerService {
    /**
     * addRepair
     * @param repair repair
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static addRepairUsingPost(
        repair: EquipmentRepair,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/equipment/repair/add',
            body: repair,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
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
     * deleteRepair
     * @param id id
     * @returns Result OK
     * @throws ApiError
     */
    public static deleteRepairUsingDelete(
        id: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/equipment/repair/delete/{id}',
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
     * getRepairById
     * @param id id
     * @returns Result OK
     * @throws ApiError
     */
    public static getRepairByIdUsingGet(
        id: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/equipment/repair/get/{id}',
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
     * listAllRepairs
     * @returns Result OK
     * @throws ApiError
     */
    public static listAllRepairsUsingGet(): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/equipment/repair/listAll',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * listRepairsByEquipment
     * @param equipmentId equipmentId
     * @returns Result OK
     * @throws ApiError
     */
    public static listRepairsByEquipmentUsingGet(
        equipmentId: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/equipment/repair/listByEquipment/{equipmentId}',
            path: {
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
     * updateRepair
     * @param repair repair
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateRepairUsingPut(
        repair: EquipmentRepair,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/equipment/repair/update',
            body: repair,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
