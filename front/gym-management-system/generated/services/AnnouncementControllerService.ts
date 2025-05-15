/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AnnouncementEntity } from '../models/AnnouncementEntity';
import type { Page_AnnouncementEntity_ } from '../models/Page_AnnouncementEntity_';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AnnouncementControllerService {
    /**
     * getAllAnnouncements
     * @param page page
     * @param size size
     * @returns Page_AnnouncementEntity_ OK
     * @throws ApiError
     */
    public static getAllAnnouncementsUsingGet(
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<Page_AnnouncementEntity_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/announcements',
            query: {
                'page': page,
                'size': size,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * createAnnouncement
     * @param announcement announcement
     * @returns AnnouncementEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static createAnnouncementUsingPost(
        announcement: AnnouncementEntity,
    ): CancelablePromise<AnnouncementEntity | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/announcements',
            body: announcement,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getActiveAnnouncements
     * @returns AnnouncementEntity OK
     * @throws ApiError
     */
    public static getActiveAnnouncementsUsingGet(): CancelablePromise<Array<AnnouncementEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/announcements/active',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getAnnouncementsByTimeRange
     * @param endTime endTime
     * @param startTime startTime
     * @returns AnnouncementEntity OK
     * @throws ApiError
     */
    public static getAnnouncementsByTimeRangeUsingGet(
        endTime: string,
        startTime: string,
    ): CancelablePromise<Array<AnnouncementEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/announcements/time-range',
            query: {
                'endTime': endTime,
                'startTime': startTime,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * updateAnnouncement
     * @param announcement announcement
     * @param id id
     * @returns AnnouncementEntity OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateAnnouncementUsingPut(
        announcement: AnnouncementEntity,
        id: number,
    ): CancelablePromise<AnnouncementEntity | any> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/announcements/{id}',
            path: {
                'id': id,
            },
            body: announcement,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deleteAnnouncement
     * @param id id
     * @returns any OK
     * @throws ApiError
     */
    public static deleteAnnouncementUsingDelete(
        id: number,
    ): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/announcements/{id}',
            path: {
                'id': id,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
            },
        });
    }
}
