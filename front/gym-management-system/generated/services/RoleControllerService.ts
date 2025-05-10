/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Result } from '../models/Result';
import type { Role } from '../models/Role';
import type { SaveMenuParm } from '../models/SaveMenuParm';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class RoleControllerService {
    /**
     * add
     * @param role role
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static addUsingPost3(
        role: Role,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/role',
            body: role,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * update
     * @param role role
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateUsingPut3(
        role: Role,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/system/role',
            body: role,
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
     * @param pageSize
     * @param roleName
     * @returns Result OK
     * @throws ApiError
     */
    public static getListUsingGet3(
        currentPage?: number,
        pageSize?: number,
        roleName?: string,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/role/getList',
            query: {
                'currentPage': currentPage,
                'pageSize': pageSize,
                'roleName': roleName,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * saveRoleMenu
     * @param parm parm
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static saveRoleMenuUsingPost(
        parm: SaveMenuParm,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/role/saveRoleMenu',
            body: parm,
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
    public static selectListUsingGet2(): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/role/selectList',
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
    public static deleteUsingDelete3(
        id: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/system/role/{id}',
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
