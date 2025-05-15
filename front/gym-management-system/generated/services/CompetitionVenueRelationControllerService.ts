/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddCompetitionVenueRelationDTO } from '../models/AddCompetitionVenueRelationDTO';
import type { Result_IPage_CompetitionVenueRelation_ } from '../models/Result_IPage_CompetitionVenueRelation_';
import type { Result_List_CompetitionVenueRelation_ } from '../models/Result_List_CompetitionVenueRelation_';
import type { Result_List_CompetitionVenueRelationVO_ } from '../models/Result_List_CompetitionVenueRelationVO_';
import type { Result_object_ } from '../models/Result_object_';
import type { UpdateCompetitionVenueRelationDTO } from '../models/UpdateCompetitionVenueRelationDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class CompetitionVenueRelationControllerService {
    /**
     * addCompetitionVenueRelation
     * @param addCompetitionVenueRelationDto addCompetitionVenueRelationDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addCompetitionVenueRelationUsingPost(
        addCompetitionVenueRelationDto: AddCompetitionVenueRelationDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/competition-venue-relation/add',
            body: addCompetitionVenueRelationDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deleteCompetitionVenueRelation
     * @param id id
     * @returns Result_object_ OK
     * @throws ApiError
     */
    public static deleteCompetitionVenueRelationUsingDelete(
        id: number,
    ): CancelablePromise<Result_object_> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/competition-venue-relation/delete/{id}',
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
     * getCompetitionVenueRelation
     * @param competitionId competitionId
     * @returns Result_List_CompetitionVenueRelationVO_ OK
     * @throws ApiError
     */
    public static getCompetitionVenueRelationUsingGet(
        competitionId: number,
    ): CancelablePromise<Result_List_CompetitionVenueRelationVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-venue-relation/getByCompetitionId/{competitionId}',
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
     * listCompetitionVenueRelation
     * @param competitionId
     * @param page
     * @param pageSize
     * @param status
     * @param venueId
     * @returns Result_IPage_CompetitionVenueRelation_ OK
     * @throws ApiError
     */
    public static listCompetitionVenueRelationUsingGet(
        competitionId?: number,
        page?: number,
        pageSize?: number,
        status?: number,
        venueId?: number,
    ): CancelablePromise<Result_IPage_CompetitionVenueRelation_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-venue-relation/list',
            query: {
                'competitionId': competitionId,
                'page': page,
                'pageSize': pageSize,
                'status': status,
                'venueId': venueId,
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
     * @returns Result_List_CompetitionVenueRelation_ OK
     * @throws ApiError
     */
    public static listByCompetitionIdUsingGet1(
        competitionId: number,
    ): CancelablePromise<Result_List_CompetitionVenueRelation_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/competition-venue-relation/listByCompetitionId/{competitionId}',
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
     * updateCompetitionVenueRelation
     * @param updateCompetitionVenueRelationDto updateCompetitionVenueRelationDTO
     * @returns Result_object_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateCompetitionVenueRelationUsingPut(
        updateCompetitionVenueRelationDto: UpdateCompetitionVenueRelationDTO,
    ): CancelablePromise<Result_object_ | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/competition-venue-relation/update',
            body: updateCompetitionVenueRelationDto,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
