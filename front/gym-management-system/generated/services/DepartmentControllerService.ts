/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Department } from '../models/Department';
import type { Result } from '../models/Result';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class DepartmentControllerService {
    /**
     * add
     * @param department department
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static addUsingPost(
        department: Department,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/department',
            body: department,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * update
     * @param department department
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateUsingPut(
        department: Department,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/system/department',
            body: department,
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
     * @param departName
     * @param pageSize
     * @returns Result OK
     * @throws ApiError
     */
    public static getListUsingGet(
        currentPage?: number,
        departName?: string,
        pageSize?: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/department/getList',
            query: {
                'currentPage': currentPage,
                'departName': departName,
                'pageSize': pageSize,
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
    public static selectListUsingGet(): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/department/selectList',
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
    public static deleteUsingDelete(
        id: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/system/department/{id}',
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
