/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Equipment } from '../models/Equipment';
import type { Result } from '../models/Result';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class EquipBasicsControllerService {
    /**
     * add
     * @param equipment equipment
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static addUsingPost1(
        equipment: Equipment,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/equip/equipment',
            body: equipment,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * update
     * @param equipment equipment
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateUsingPut1(
        equipment: Equipment,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/equip/equipment',
            body: equipment,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getList
     * @param currentPage
     * @param equipmentName
     * @param pageSize
     * @param status
     * @returns Result OK
     * @throws ApiError
     */
    public static getListUsingGet1(
        currentPage?: number,
        equipmentName?: string,
        pageSize?: number,
        status?: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/equip/equipment/getList',
            query: {
                'currentPage': currentPage,
                'equipmentName': equipmentName,
                'pageSize': pageSize,
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
     * selectList
     * @returns Result OK
     * @throws ApiError
     */
    public static selectListUsingGet1(): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/equip/equipment/selectList',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * delete
     * @param id id
     * @returns Result OK
     * @throws ApiError
     */
    public static deleteUsingDelete1(
        id: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/equip/equipment/{id}',
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
