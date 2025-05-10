/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { LoginParm } from '../models/LoginParm';
import type { Result } from '../models/Result';
import type { UpdatePasswordParm } from '../models/UpdatePasswordParm';
import type { User } from '../models/User';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class UserControllerService {
    /**
     * add
     * @param user user
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static addUsingPost5(
        user: User,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/user',
            body: user,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * update
     * @param user user
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateUsingPut5(
        user: User,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/system/user',
            body: user,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getAssingTree
     * @param roleId
     * @param userId
     * @returns Result OK
     * @throws ApiError
     */
    public static getAssingTreeUsingGet(
        roleId?: number,
        userId?: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/user/getAssignTree',
            query: {
                'roleId': roleId,
                'userId': userId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getDepartmentId
     * @param id id
     * @returns Result OK
     * @throws ApiError
     */
    public static getDepartmentIdUsingGet(
        id?: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/user/getDepartId',
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
     * getInfo
     * @param id id
     * @returns Result OK
     * @throws ApiError
     */
    public static getInfoUsingGet(
        id?: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/user/getInfo',
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
     * getList
     * @param currentPage
     * @param name
     * @param pageSize
     * @param userNumber
     * @param userType
     * @returns Result OK
     * @throws ApiError
     */
    public static getListUsingGet5(
        currentPage?: number,
        name?: string,
        pageSize?: number,
        userNumber?: string,
        userType?: string,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/user/getList',
            query: {
                'currentPage': currentPage,
                'name': name,
                'pageSize': pageSize,
                'userNumber': userNumber,
                'userType': userType,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getRoleId
     * @param id id
     * @returns Result OK
     * @throws ApiError
     */
    public static getRoleIdUsingGet(
        id?: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/user/getRoleId',
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
     * getSectionId
     * @param id id
     * @returns Result OK
     * @throws ApiError
     */
    public static getSectionIdUsingGet(
        id?: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/user/getSectionId',
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
     * login
     * @param parm Parm
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static loginUsingPost(
        parm: LoginParm,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/user/login',
            body: parm,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * resetPassword
     * @param id id
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static resetPasswordUsingPut(
        id: number,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/system/user/resetPassword/{id}',
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
     * updatePassword
     * @param parm parm
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static updatePasswordUsingPost(
        parm: UpdatePasswordParm,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/user/updatePassword',
            body: parm,
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
    public static deleteUsingDelete5(
        id: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/system/user/{id}',
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
