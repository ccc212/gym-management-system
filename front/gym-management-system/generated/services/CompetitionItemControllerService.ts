/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddCompetitionItemDTO } from '../models/AddCompetitionItemDTO';
import type { Result_CompetitionItem_ } from '../models/Result_CompetitionItem_';
import type { Result_IPage_CompetitionItem_ } from '../models/Result_IPage_CompetitionItem_';
import type { Result_List_CompetitionItem_ } from '../models/Result_List_CompetitionItem_';
import type { Result_object_ } from '../models/Result_object_';
import type { UpdateCompetitionItemDTO } from '../models/UpdateCompetitionItemDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class CompetitionItemControllerService {
    /**
     * addCompetitionItem
     * @param addCompetitionItemDto addCompetitionItemDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addCompetitionItemUsingPost(
        addCompetitionItemDto: AddCompetitionItemDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/competition-item/add',
            body: addCompetitionItemDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deleteCompetitionItem
     * @param id id
     * @returns Result_object_ OK
     * @throws ApiError
     */
    public static deleteCompetitionItemUsingDelete(
        id: number,
    ): CancelablePromise<Result_object_> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/competition-item/delete/{id}',
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
     * getCompetitionItem
     * @param id id
     * @returns Result_CompetitionItem_ OK
     * @throws ApiError
     */
    public static getCompetitionItemUsingGet(
        id: number,
    ): CancelablePromise<Result_CompetitionItem_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-item/get/{id}',
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
     * listCompetitionItem
     * @param category
     * @param isTeamCompetition
     * @param name
     * @param page
     * @param pageSize
     * @param type
     * @returns Result_IPage_CompetitionItem_ OK
     * @throws ApiError
     */
    public static listCompetitionItemUsingGet(
        category?: number,
        isTeamCompetition?: number,
        name?: string,
        page?: number,
        pageSize?: number,
        type?: number,
    ): CancelablePromise<Result_IPage_CompetitionItem_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-item/list',
            query: {
                'category': category,
                'isTeamCompetition': isTeamCompetition,
                'name': name,
                'page': page,
                'pageSize': pageSize,
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
     * listAllCompetitionItems
     * @returns Result_List_CompetitionItem_ OK
     * @throws ApiError
     */
    public static listAllCompetitionItemsUsingGet(): CancelablePromise<Result_List_CompetitionItem_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-item/listAll',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * updateCompetitionItem
     * @param updateCompetitionItemDto updateCompetitionItemDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateCompetitionItemUsingPut(
        updateCompetitionItemDto: UpdateCompetitionItemDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/competition-item/update',
            body: updateCompetitionItemDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
