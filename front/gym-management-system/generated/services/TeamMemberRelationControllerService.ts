/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddTeamMemberRelationDTO } from '../models/AddTeamMemberRelationDTO';
import type { Result_boolean_ } from '../models/Result_boolean_';
import type { Result_IPage_TeamMemberRelation_ } from '../models/Result_IPage_TeamMemberRelation_';
import type { Result_List_TeamMemberRelationVO_ } from '../models/Result_List_TeamMemberRelationVO_';
import type { Result_object_ } from '../models/Result_object_';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class TeamMemberRelationControllerService {
    /**
     * addTeamMemberRelation
     * @param addTeamMemberRelationDto addTeamMemberRelationDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addTeamMemberRelationUsingPost(
        addTeamMemberRelationDto: AddTeamMemberRelationDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/teamMemberRelation-member-relation/add',
            body: addTeamMemberRelationDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * approveApplication
     * @param id id
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static approveApplicationUsingPost(
        id: number,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/teamMemberRelation-member-relation/approve/{id}',
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
     * isUserInTeam
     * @param teamId teamId
     * @param userId userId
     * @returns Result_boolean_ OK
     * @throws ApiError
     */
    public static isUserInTeamUsingGet(
        teamId: number,
        userId: number,
    ): CancelablePromise<Result_boolean_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/teamMemberRelation-member-relation/check',
            query: {
                'teamId': teamId,
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
     * deleteTeamMemberRelation
     * @param id id
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static deleteTeamMemberRelationUsingPost(
        id: number,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/teamMemberRelation-member-relation/delete/{id}',
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
     * listTeam
     * @param page
     * @param pageSize
     * @param status
     * @param teamId
     * @param userId
     * @returns Result_IPage_TeamMemberRelation_ OK
     * @throws ApiError
     */
    public static listTeamUsingGet1(
        page?: number,
        pageSize?: number,
        status?: number,
        teamId?: number,
        userId?: number,
    ): CancelablePromise<Result_IPage_TeamMemberRelation_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/teamMemberRelation-member-relation/list',
            query: {
                'page': page,
                'pageSize': pageSize,
                'status': status,
                'teamId': teamId,
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
     * rejectApplication
     * @param id id
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static rejectApplicationUsingPost(
        id: number,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/teamMemberRelation-member-relation/reject/{id}',
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
     * updateApplicationStatus
     * @param id id
     * @param status status
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateApplicationStatusUsingPost(
        id: number,
        status: number,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/teamMemberRelation-member-relation/status/{id}',
            path: {
                'id': id,
            },
            query: {
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
     * getTeamApplications
     * @param teamId teamId
     * @param status status
     * @returns Result_List_TeamMemberRelationVO_ OK
     * @throws ApiError
     */
    public static getTeamApplicationsUsingGet(
        teamId: number,
        status?: number,
    ): CancelablePromise<Result_List_TeamMemberRelationVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/teamMemberRelation-member-relation/team/{teamId}',
            path: {
                'teamId': teamId,
            },
            query: {
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
     * getUserApplications
     * @param userId userId
     * @param status status
     * @returns Result_List_TeamMemberRelationVO_ OK
     * @throws ApiError
     */
    public static getUserApplicationsUsingGet(
        userId: number,
        status?: number,
    ): CancelablePromise<Result_List_TeamMemberRelationVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/teamMemberRelation-member-relation/user/{userId}',
            path: {
                'userId': userId,
            },
            query: {
                'status': status,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
