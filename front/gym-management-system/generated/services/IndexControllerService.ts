/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class IndexControllerService {
    /**
     * index
     * @returns string OK
     * @throws ApiError
     */
    public static indexUsingGet(): CancelablePromise<string> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
