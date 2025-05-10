/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddCompetitionSignUpUserDTO } from '../models/AddCompetitionSignUpUserDTO';
import type { Result_CompetitionSignUpUser_ } from '../models/Result_CompetitionSignUpUser_';
import type { Result_IPage_CompetitionSignUpUser_ } from '../models/Result_IPage_CompetitionSignUpUser_';
import type { Result_object_ } from '../models/Result_object_';
import type { UpdateCompetitionSignUpUserDTO } from '../models/UpdateCompetitionSignUpUserDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class CompetitionSignUpUserControllerService {
    /**
     * addCompetitionSignUpUser
     * @param addCompetitionSignUpUserDto addCompetitionSignUpUserDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addCompetitionSignUpUserUsingPost(
        addCompetitionSignUpUserDto: AddCompetitionSignUpUserDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/competition-sign-up-user/add',
            body: addCompetitionSignUpUserDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * approveSignUp
     * @param id id
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static approveSignUpUsingPost(
        id: number,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/competition-sign-up-user/approve/{id}',
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
     * deleteCompetitionSignUpUser
     * @param id id
     * @returns Result_object_ OK
     * @throws ApiError
     */
    public static deleteCompetitionSignUpUserUsingDelete(
        id: number,
    ): CancelablePromise<Result_object_> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/competition-sign-up-user/delete/{id}',
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
     * getCompetitionSignUpUser
     * @param id id
     * @returns Result_CompetitionSignUpUser_ OK
     * @throws ApiError
     */
    public static getCompetitionSignUpUserUsingGet(
        id: number,
    ): CancelablePromise<Result_CompetitionSignUpUser_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-sign-up-user/get/{id}',
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
     * listCompetitionSignUpUser
     * @param competitionId
     * @param competitionItemId
     * @param departId
     * @param name
     * @param page
     * @param pageSize
     * @param status
     * @param userId
     * @returns Result_IPage_CompetitionSignUpUser_ OK
     * @throws ApiError
     */
    public static listCompetitionSignUpUserUsingGet(
        competitionId?: number,
        competitionItemId?: number,
        departId?: number,
        name?: string,
        page?: number,
        pageSize?: number,
        status?: number,
        userId?: number,
    ): CancelablePromise<Result_IPage_CompetitionSignUpUser_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-sign-up-user/list',
            query: {
                'competitionId': competitionId,
                'competitionItemId': competitionItemId,
                'departId': departId,
                'name': name,
                'page': page,
                'pageSize': pageSize,
                'status': status,
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
     * rejectSignUp
     * @param id id
     * @param rejectReason rejectReason
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static rejectSignUpUsingPost(
        id: number,
        rejectReason: string,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/competition-sign-up-user/reject',
            query: {
                'id': id,
                'rejectReason': rejectReason,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * updateCompetitionSignUpUser
     * @param updateCompetitionSignUpUserDto updateCompetitionSignUpUserDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateCompetitionSignUpUserUsingPut(
        updateCompetitionSignUpUserDto: UpdateCompetitionSignUpUserDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/competition-sign-up-user/update',
            body: updateCompetitionSignUpUserDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
