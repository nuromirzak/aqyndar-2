import axiosInstance from "../axiosInstance.ts";
import {GetAuthorResponse} from "../../types";

async function searchAuthors(query: string): Promise<GetAuthorResponse[]> {
    const response = await axiosInstance.get<GetAuthorResponse[]>('/search/author', {
        params: {
            q: query
        }
    });
    return response.data;
}

export const searchService = {
    searchAuthors
}
