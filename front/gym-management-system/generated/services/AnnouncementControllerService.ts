/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AnnouncementEntity } from '../models/AnnouncementEntity';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AnnouncementControllerService {
    /**
     * getActiveAnnouncements
     * @returns AnnouncementEntity OK
     * @throws ApiError
     */
    public static getActiveAnnouncementsUsingGet(): CancelablePromise<Array<AnnouncementEntity>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/announcements',
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
     * getLatestAnnouncement
     * @returns string OK
     * @throws ApiError
     */
    public static getLatestAnnouncementUsingGet(): CancelablePromise<Record<string, string>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/announcements/latest',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deactivateAnnouncement
     * @param id id
     * @returns any OK
     * @throws ApiError
     */
    public static deactivateAnnouncementUsingPost(
        id: number,
    ): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/announcements/{id}/deactivate',
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
