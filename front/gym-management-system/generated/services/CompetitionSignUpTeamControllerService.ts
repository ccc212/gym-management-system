/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddCompetitionSignUpTeamDTO } from '../models/AddCompetitionSignUpTeamDTO';
import type { Result_IPage_CompetitionSignUpTeam_ } from '../models/Result_IPage_CompetitionSignUpTeam_';
import type { Result_object_ } from '../models/Result_object_';
import type { UpdateCompetitionSignUpTeamDTO } from '../models/UpdateCompetitionSignUpTeamDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class CompetitionSignUpTeamControllerService {
    /**
     * addCompetitionSignUpTeam
     * @param addCompetitionSignUpTeamDto addCompetitionSignUpTeamDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addCompetitionSignUpTeamUsingPost(
        addCompetitionSignUpTeamDto: AddCompetitionSignUpTeamDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/competition-sign-up-team/add',
            body: addCompetitionSignUpTeamDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deleteCompetitionSignUpTeam
     * @param id id
     * @returns Result_object_ OK
     * @throws ApiError
     */
    public static deleteCompetitionSignUpTeamUsingDelete(
        id: number,
    ): CancelablePromise<Result_object_> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/competition-sign-up-team/delete/{id}',
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
     * listCompetition
     * @param competitionId
     * @param page
     * @param pageSize
     * @param status
     * @param teamName
     * @returns Result_IPage_CompetitionSignUpTeam_ OK
     * @throws ApiError
     */
    public static listCompetitionUsingGet1(
        competitionId?: number,
        page?: number,
        pageSize?: number,
        status?: number,
        teamName?: string,
    ): CancelablePromise<Result_IPage_CompetitionSignUpTeam_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-sign-up-team/list',
            query: {
                'competitionId': competitionId,
                'page': page,
                'pageSize': pageSize,
                'status': status,
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
     * updateCompetitionSignUpTeam
     * @param updateCompetitionSignUpTeamDto updateCompetitionSignUpTeamDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateCompetitionSignUpTeamUsingPut(
        updateCompetitionSignUpTeamDto: UpdateCompetitionSignUpTeamDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/competition-sign-up-team/update',
            body: updateCompetitionSignUpTeamDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
