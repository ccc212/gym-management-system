/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Menu } from '../models/Menu';
import type { Result } from '../models/Result';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class MenuControllerService {
    /**
     * add
     * @param menu menu
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static addUsingPost2(
        menu: Menu,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/menu',
            body: menu,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * update
     * @param menu menu
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateUsingPut2(
        menu: Menu,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/system/menu',
            body: menu,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getMenuList
     * @param id id
     * @returns Result OK
     * @throws ApiError
     */
    public static getMenuListUsingGet(
        id?: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/menu/getMenuList',
            query: {
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
     * getParent
     * @returns Result OK
     * @throws ApiError
     */
    public static getParentUsingGet(): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/menu/getParent',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getList
     * @returns Result OK
     * @throws ApiError
     */
    public static getListUsingGet2(): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/menu/list',
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
    public static deleteUsingDelete2(
        id: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/system/menu/{id}',
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
