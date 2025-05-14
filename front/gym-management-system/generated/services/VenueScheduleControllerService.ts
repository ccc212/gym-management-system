/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Result_List_string_ } from '../models/Result_List_string_';
import type { Result_List_VenueScheduleDTO_ } from '../models/Result_List_VenueScheduleDTO_';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class VenueScheduleControllerService {
    /**
     * getVenueTypes
     * @returns Result_List_string_ OK
     * @throws ApiError
     */
    public static getVenueTypesUsingGet1(): CancelablePromise<Result_List_string_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venue-schedules/types',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getVenuesByType
     * @param type type
     * @returns Result_List_VenueScheduleDTO_ OK
     * @throws ApiError
     */
    public static getVenuesByTypeUsingGet1(
        type?: string,
    ): CancelablePromise<Result_List_VenueScheduleDTO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venue-schedules/venues',
            query: {
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
     * getWeeklySchedule
     * @param endDate endDate
     * @param startDate startDate
     * @param venueId venueId
     * @param venueType venueType
     * @returns Result_List_VenueScheduleDTO_ OK
     * @throws ApiError
     */
    public static getWeeklyScheduleUsingGet(
        endDate: string,
        startDate: string,
        venueId?: number,
        venueType?: string,
    ): CancelablePromise<Result_List_VenueScheduleDTO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/venue-schedules/weekly',
            query: {
                'endDate': endDate,
                'startDate': startDate,
                'venueId': venueId,
                'venueType': venueType,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
