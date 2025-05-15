/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { LoginRequest } from '../models/LoginRequest';
import type { LoginResponse } from '../models/LoginResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AuthControllerService {
    /**
     * login
     * @param loginRequest loginRequest
     * @returns LoginResponse OK
     * @returns any Created
     * @throws ApiError
     */
    public static loginUsingPost(
        loginRequest: LoginRequest,
    ): CancelablePromise<LoginResponse | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/auth/login',
            body: loginRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
