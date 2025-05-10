/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddTeamMemberRelationDTO } from '../models/AddTeamMemberRelationDTO';
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
}
