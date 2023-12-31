import {poemService} from "../api/services/poemService.ts";
import {GetAuthorResponse, GetWhoResponse, LeaderboardLoaderResponse, PoemLoaderResponse, TopEntity} from "../types";
import {authorService} from "../api/services/authorService.ts";
import {profileService} from "../api/services/profileService.ts";
import {reactionService} from "../api/services/reactionService.ts";

export async function poemsLoader() {
    let response;
    try {
        response = await poemService.getAllPoems();
        return response;
    } catch (e) {
        console.log(e);
        throw e;
    }
}

export async function authorsLoader() {
    let response;
    try {
        response = await authorService.getAllAuthors();
        return response;
    } catch (e) {
        console.log(e);
        throw e;
    }
}

// @ts-expect-error(TODO)
    export async function poemLoader({params}): Promise<PoemLoaderResponse | null> {
    const {id} = params;

    let poem, author;

    try {
        poem = await poemService.getPoemById(id);
        const authorId = poem.authorId;
        author = await authorService.getAuthorById(authorId);
        return {poem, author};
    } catch (e) {
        console.log(e);
        return null;
    }
}

// @ts-expect-error(TODO)
export async function authorLoader({params}): Promise<GetAuthorResponse | null> {
    const {id} = params;

    let author;

    try {
        author = await authorService.getAuthorById(id);
        return author;
    } catch (e) {
        console.log(e);
        return null;
    }
}

export async function profileLoader(): Promise<GetWhoResponse | null> {
    console.log(`profileLoader`);
    let response;
    try {
        response = await profileService.getCurrentUser();
        console.log(`profileLoader`, response);
        return response;
    } catch (e) {
        console.log('profileLoader', e);
        return null;
    }
}

export async function leaderboardLoader(): Promise<LeaderboardLoaderResponse> {
    let poems, users;
    try {
        poems = await reactionService.getTopEntities(TopEntity.POEM);
        users = await reactionService.getTopEntities(TopEntity.USER);
        return {poems, users};
    } catch (e) {
        console.log(e);
        throw e;
    }
}
