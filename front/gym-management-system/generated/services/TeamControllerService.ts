/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddTeamDTO } from '../models/AddTeamDTO';
import type { Result_IPage_Team_ } from '../models/Result_IPage_Team_';
import type { Result_List_Team_ } from '../models/Result_List_Team_';
import type { Result_object_ } from '../models/Result_object_';
import type { Result_Team_ } from '../models/Result_Team_';
import type { Result_TeamDetailVO_ } from '../models/Result_TeamDetailVO_';
import type { UpdateTeamDTO } from '../models/UpdateTeamDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class TeamControllerService {
    /**
     * addTeam
     * @param addTeamDto addTeamDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addTeamUsingPost(
        addTeamDto: AddTeamDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/team/add',
            body: addTeamDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * addTeamMember
     * @param teamId teamId
     * @param userId userId
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addTeamMemberUsingPost(
        teamId: number,
        userId: number,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/team/addMember',
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
     * deleteTeam
     * @param id id
     * @returns Result_object_ OK
     * @throws ApiError
     */
    public static deleteTeamUsingDelete(
        id: number,
    ): CancelablePromise<Result_object_> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/team/delete/{id}',
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
     * getTeamDetail
     * @param id id
     * @returns Result_TeamDetailVO_ OK
     * @throws ApiError
     */
    public static getTeamDetailUsingGet(
        id: number,
    ): CancelablePromise<Result_TeamDetailVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/team/detail/{id}',
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
     * getTeam
     * @param id id
     * @returns Result_Team_ OK
     * @throws ApiError
     */
    public static getTeamUsingGet(
        id: number,
    ): CancelablePromise<Result_Team_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/team/get/{id}',
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
     * @param departId
     * @param leaderName
     * @param page
     * @param pageSize
     * @param teamName
     * @returns Result_IPage_Team_ OK
     * @throws ApiError
     */
    public static listTeamUsingGet(
        departId?: number,
        leaderName?: string,
        page?: number,
        pageSize?: number,
        teamName?: string,
    ): CancelablePromise<Result_IPage_Team_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/team/list',
            query: {
                'departId': departId,
                'leaderName': leaderName,
                'page': page,
                'pageSize': pageSize,
                'teamName': teamName,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * listAllTeams
     * @returns Result_List_Team_ OK
     * @throws ApiError
     */
    public static listAllTeamsUsingGet(): CancelablePromise<Result_List_Team_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/team/listAll',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * removeTeamMember
     * @param teamId teamId
     * @param userId userId
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static removeTeamMemberUsingPost(
        teamId: number,
        userId: number,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/team/removeMember',
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
     * updateTeam
     * @param updateTeamDto updateTeamDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateTeamUsingPut(
        updateTeamDto: UpdateTeamDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/team/update',
            body: updateTeamDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
