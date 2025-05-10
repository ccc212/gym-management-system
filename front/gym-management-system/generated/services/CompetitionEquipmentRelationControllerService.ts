/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddCompetitionEquipmentRelationDTO } from '../models/AddCompetitionEquipmentRelationDTO';
import type { Result_CompetitionEquipmentRelation_ } from '../models/Result_CompetitionEquipmentRelation_';
import type { Result_IPage_CompetitionEquipmentRelation_ } from '../models/Result_IPage_CompetitionEquipmentRelation_';
import type { Result_List_CompetitionEquipmentRelation_ } from '../models/Result_List_CompetitionEquipmentRelation_';
import type { Result_object_ } from '../models/Result_object_';
import type { UpdateCompetitionEquipmentRelationDTO } from '../models/UpdateCompetitionEquipmentRelationDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class CompetitionEquipmentRelationControllerService {
    /**
     * addCompetitionEquipmentRelation
     * @param addCompetitionEquipmentRelationDto addCompetitionEquipmentRelationDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addCompetitionEquipmentRelationUsingPost(
        addCompetitionEquipmentRelationDto: AddCompetitionEquipmentRelationDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/competition-equipment-relation/add',
            body: addCompetitionEquipmentRelationDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deleteCompetitionEquipmentRelation
     * @param id id
     * @returns Result_object_ OK
     * @throws ApiError
     */
    public static deleteCompetitionEquipmentRelationUsingDelete(
        id: number,
    ): CancelablePromise<Result_object_> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/competition-equipment-relation/delete/{id}',
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
     * getCompetitionEquipmentRelation
     * @param id id
     * @returns Result_CompetitionEquipmentRelation_ OK
     * @throws ApiError
     */
    public static getCompetitionEquipmentRelationUsingGet(
        id: number,
    ): CancelablePromise<Result_CompetitionEquipmentRelation_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-equipment-relation/get/{id}',
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
     * listCompetitionEquipmentRelation
     * @param competitionId
     * @param equipmentId
     * @param page
     * @param pageSize
     * @param status
     * @returns Result_IPage_CompetitionEquipmentRelation_ OK
     * @throws ApiError
     */
    public static listCompetitionEquipmentRelationUsingGet(
        competitionId?: number,
        equipmentId?: number,
        page?: number,
        pageSize?: number,
        status?: number,
    ): CancelablePromise<Result_IPage_CompetitionEquipmentRelation_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-equipment-relation/list',
            query: {
                'competitionId': competitionId,
                'equipmentId': equipmentId,
                'page': page,
                'pageSize': pageSize,
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
     * listByCompetitionId
     * @param competitionId competitionId
     * @returns Result_List_CompetitionEquipmentRelation_ OK
     * @throws ApiError
     */
    public static listByCompetitionIdUsingGet(
        competitionId: number,
    ): CancelablePromise<Result_List_CompetitionEquipmentRelation_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-equipment-relation/listByCompetitionId/{competitionId}',
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
     * updateCompetitionEquipmentRelation
     * @param updateCompetitionEquipmentRelationDto updateCompetitionEquipmentRelationDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateCompetitionEquipmentRelationUsingPut(
        updateCompetitionEquipmentRelationDto: UpdateCompetitionEquipmentRelationDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/competition-equipment-relation/update',
            body: updateCompetitionEquipmentRelationDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
