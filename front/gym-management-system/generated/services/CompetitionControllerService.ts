/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddCompetitionDTO } from '../models/AddCompetitionDTO';
import type { Result_Competition_ } from '../models/Result_Competition_';
import type { Result_CompetitionDetailVO_ } from '../models/Result_CompetitionDetailVO_';
import type { Result_IPage_Competition_ } from '../models/Result_IPage_Competition_';
import type { Result_List_CompetitionItem_ } from '../models/Result_List_CompetitionItem_';
import type { Result_List_CompetitionItemRelation_ } from '../models/Result_List_CompetitionItemRelation_';
import type { Result_object_ } from '../models/Result_object_';
import type { UpdateCompetitionDTO } from '../models/UpdateCompetitionDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class CompetitionControllerService {
    /**
     * addCompetition
     * @param addCompetitionDto addCompetitionDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addCompetitionUsingPost(
        addCompetitionDto: AddCompetitionDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/competition/add',
            body: addCompetitionDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deleteCompetition
     * @param id id
     * @returns Result_object_ OK
     * @throws ApiError
     */
    public static deleteCompetitionUsingDelete(
        id: number,
    ): CancelablePromise<Result_object_> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/competition/delete/{id}',
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
     * getCompetition
     * @param id id
     * @returns Result_Competition_ OK
     * @throws ApiError
     */
    public static getCompetitionUsingGet(
        id: number,
    ): CancelablePromise<Result_Competition_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition/get/{id}',
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
     * getDetail
     * @param id id
     * @returns Result_CompetitionDetailVO_ OK
     * @throws ApiError
     */
    public static getDetailUsingGet(
        id: number,
    ): CancelablePromise<Result_CompetitionDetailVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition/getDetail/{id}',
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
     * listCompetition
     * @param category
     * @param hoster
     * @param isTeamCompetition
     * @param name
     * @param page
     * @param pageSize
     * @param status
     * @param type
     * @returns Result_IPage_Competition_ OK
     * @throws ApiError
     */
    public static listCompetitionUsingGet(
        category?: number,
        hoster?: string,
        isTeamCompetition?: number,
        name?: string,
        page?: number,
        pageSize?: number,
        status?: number,
        type?: number,
    ): CancelablePromise<Result_IPage_Competition_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition/list',
            query: {
                'category': category,
                'hoster': hoster,
                'isTeamCompetition': isTeamCompetition,
                'name': name,
                'page': page,
                'pageSize': pageSize,
                'status': status,
                'type': type,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * listItem
     * @returns Result_List_CompetitionItem_ OK
     * @throws ApiError
     */
    public static listItemUsingGet(): CancelablePromise<Result_List_CompetitionItem_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition/listItem',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * listItemByCompetitionId
     * @param competitionId competitionId
     * @returns Result_List_CompetitionItemRelation_ OK
     * @throws ApiError
     */
    public static listItemByCompetitionIdUsingGet(
        competitionId: number,
    ): CancelablePromise<Result_List_CompetitionItemRelation_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition/listItemByCompetitionId/{competitionId}',
            path: {
                'competitionId': competitionId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * updateCompetition
     * @param updateCompetitionDto updateCompetitionDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateCompetitionUsingPut(
        updateCompetitionDto: UpdateCompetitionDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/competition/update',
            body: updateCompetitionDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
