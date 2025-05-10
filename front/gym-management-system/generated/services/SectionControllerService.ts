/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Result } from '../models/Result';
import type { Section } from '../models/Section';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class SectionControllerService {
    /**
     * add
     * @param section section
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static addUsingPost4(
        section: Section,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/section',
            body: section,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * update
     * @param section section
     * @returns Result OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateUsingPut4(
        section: Section,
    ): CancelablePromise<Result | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/system/section',
            body: section,
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
     * @param sectionName
     * @returns Result OK
     * @throws ApiError
     */
    public static getListUsingGet4(
        currentPage?: number,
        pageSize?: number,
        sectionName?: string,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/section/getList',
            query: {
                'currentPage': currentPage,
                'pageSize': pageSize,
                'sectionName': sectionName,
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
    public static selectListUsingGet3(): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/section/selectList',
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
    public static deleteUsingDelete4(
        id: number,
    ): CancelablePromise<Result> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/system/section/{id}',
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
