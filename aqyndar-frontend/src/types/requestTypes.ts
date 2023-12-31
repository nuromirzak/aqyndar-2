export interface CreateAnnotationRequest {
    content: string;
    startRangeIndex: number;
    endRangeIndex: number;
    poemId: number;
}

export interface CreateAuthorRequest {
    fullName: string;
}

export interface CreatePoemRequest {
    title: string;
    content: string;
    authorId: number;
    schoolGrade?: number;
    complexity?: number;
    topics: string[];
}

export interface PatchAnnotationRequest {
    content: string;
    startRangeIndex: number;
    endRangeIndex: number;
    poemId: number;
}

export interface PatchAuthorRequest {
    fullName?: string;
}

export interface PatchPoemRequest {
    title?: string;
    authorId?: number;
    schoolGrade?: number;
    complexity?: number;
    topics?: string[];
}

export interface RefreshRequest {
    refreshToken: string;
}

export interface SigninRequest {
    email: string;
    password: string;
}

export interface SignupRequest {
    email: string;
    firstName: string;
    password: string;
}

export interface UpdateReactionRequest {
    reactedEntity: string;
    reactedEntityId: number;
    reactionType: number;
}

export interface Pageable {
    page: number;
    size: number;
    sort?: string[];
}
