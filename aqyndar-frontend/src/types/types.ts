import {GetAuthorResponse, GetPoemResponse, TopEntityResponse} from "./responseTypes.ts";

export interface IAlertInfo {
    error: boolean;
    title: string;
    detail?: string;
}

export interface AccessTokenPayload {
    sub: string;
    exp: number;
    roles: string[];
    firstName: string;
}

export interface PoemLoaderResponse {
    poem: GetPoemResponse;
    author: GetAuthorResponse;
}

export interface LeaderboardLoaderResponse {
    poems: TopEntityResponse[];
    users: TopEntityResponse[];
}
