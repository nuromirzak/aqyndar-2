import {ReactedEntity} from "./enums.ts";

export interface Page<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            sorted: boolean;
            empty: boolean;
            unsorted: boolean;
        };
        offset: number;
        paged: boolean;
        unpaged: boolean;
    };
    last: boolean;
    totalPages: number;
    totalElements: number;
    first: boolean;
    size: number;
    number: number;
    sort: {
        sorted: boolean;
        empty: boolean;
        unsorted: boolean;
    };
    numberOfElements: number;
    empty: boolean;
}

export interface CustomErrorResponse {
    title: string;
    detail: string;
}

export interface DeleteResponse {
    readonly status: string;
}

export interface GetAnnotationResponse {
    id: number;
    content: string;
    startRangeIndex: number;
    endRangeIndex: number;
    poemId: number;
    userId: number;
}

export interface GetAuthorResponse {
    id: number;
    fullName: string;
    userId: number;
    poemsCount: number;
}

export interface GetLikesResponse {
    poemLikes: number;
    annotationLikes: number;
    poemDislikes: number;
    annotationDislikes: number;
}

export interface GetPoemResponse {
    id: number;
    title: string;
    content: string;
    authorId: number;
    authorName: string;
    annotations: GetAnnotationResponse[];
    userId: number;
    schoolGrade?: number;
    complexity?: number;
    topics: string[];
}

export interface GetReactionResponse {
    dislikes: number;
    likes: number;
}

export interface GetTopicResponse {
    id: number;
    name: string;
}

export interface GetWhoResponse {
    id: number;
    email: string;
    firstName: string;
    createdAt: Date;
    roles: string[];
}

export interface JwtResponse {
    readonly type: string;
    accessToken: string;
    refreshToken: string;
}

export type AccessTokenOnlyResponse = Omit<JwtResponse, 'refreshToken'>;

export interface SignupResponse {
    readonly status: string;
    email: string;
}

export interface TopEntityResponse {
    id: number;
    reactionSum: number;
    name: string;
}

export interface UpdateReactionResponse {
    readonly status: string;
    id: number;
    reactionSum: number;
    name: string;
    reactedEntity: ReactedEntity;
    reactedEntityId: number;
    reactionType: number;
    userId: number;
}
