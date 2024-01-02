import {poemService} from "../api/services/poemService.ts";
import {
    GetAuthorResponse,
    GetPoemResponse,
    GetWhoResponse,
    LeaderboardLoaderResponse,
    Page,
    PoemLoaderResponse,
    TopEntity
} from "../types";
import {authorService} from "../api/services/authorService.ts";
import {profileService} from "../api/services/profileService.ts";
import {reactionService} from "../api/services/reactionService.ts";

interface LoaderArgs {
    params: CustomLoaderParams;
    request: Request;
}

type CustomLoaderParams = Record<string, string | undefined>;

function getNumericParam(params: CustomLoaderParams, key: string, defaultValue: number): number {
    const value = params[key];
    const numberValue = Number(value);
    return isNaN(numberValue) ? defaultValue : numberValue;
}

function getNumericQueryParam(searchParams: URLSearchParams, key: string, defaultValue: number): number {
    const value = searchParams.get(key);
    const numberValue = Number(value);
    return isNaN(numberValue) ? defaultValue : numberValue;
}

function parsePageAndSize(searchParams: URLSearchParams): { page: number, size: number } {
    let page = getNumericQueryParam(searchParams, 'page', 0);
    page = page > 0 ? page - 1 : 0;
    const size = getNumericQueryParam(searchParams, 'size', 20);
    return {page, size};
}

export async function poemsLoader({request}: LoaderArgs): Promise<Page<GetPoemResponse>> {
    const searchParams = new URL(request.url).searchParams;
    const {page, size} = parsePageAndSize(searchParams);

    try {
        return await poemService.getAllPoems({page, size});
    } catch (e) {
        console.error(e);
        throw e;
    }
}

export async function authorsLoader({request}: LoaderArgs): Promise<Page<GetAuthorResponse>> {
    const searchParams = new URL(request.url).searchParams;
    const {page, size} = parsePageAndSize(searchParams);

    try {
        return await authorService.getAllAuthors({page, size});
    } catch (e) {
        console.error(e);
        throw e;
    }
}

export async function poemLoader({params}: LoaderArgs): Promise<PoemLoaderResponse | null> {
    const id = getNumericParam(params, 'id', -1);
    if (id < 0) {
        return null;
    }

    try {
        const poem = await poemService.getPoemById(id);
        const author = await authorService.getAuthorById(poem.authorId);
        return {poem, author};
    } catch (e) {
        console.error(e);
        return null;
    }
}

export async function authorLoader({params}: LoaderArgs): Promise<GetAuthorResponse | null> {
    const id = getNumericParam(params, 'id', -1);
    if (id < 0) {
        return null;
    }

    try {
        return await authorService.getAuthorById(id);
    } catch (e) {
        console.error(e);
        return null;
    }
}

export async function profileLoader(): Promise<GetWhoResponse | null> {
    try {
        return await profileService.getCurrentUser();
    } catch (e) {
        console.error(e);
        return null;
    }
}

export async function leaderboardLoader(): Promise<LeaderboardLoaderResponse> {
    const poems = await reactionService.getTopEntities(TopEntity.POEM);
    const users = await reactionService.getTopEntities(TopEntity.USER);
    return {poems, users};
}
