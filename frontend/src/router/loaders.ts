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

type CustomLoaderParams = Record<string, string | undefined>;

function getNumericParam(params: CustomLoaderParams, key: string): number | undefined {
    const value = params[key];
    const numberValue = Number(value);
    return isNaN(numberValue) ? undefined : numberValue;
}

interface LoaderArgs {
    params: CustomLoaderParams;
}

export async function poemLoader({params}: LoaderArgs): Promise<PoemLoaderResponse | null> {
    const id = getNumericParam(params, 'id');

    if (id === undefined) {
        return null;
    }

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

export async function authorLoader({params}: LoaderArgs): Promise<GetAuthorResponse | null> {
    const id = getNumericParam(params, 'id');

    if (id === undefined) {
        return null;
    }

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
    return await profileService.getCurrentUser();
}

export async function leaderboardLoader(): Promise<LeaderboardLoaderResponse> {
    const poems = await reactionService.getTopEntities(TopEntity.POEM);
    const users = await reactionService.getTopEntities(TopEntity.USER);
    return {poems, users};
}
