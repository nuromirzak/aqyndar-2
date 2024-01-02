import axiosInstance from '../axiosInstance.ts';
import {CreateAuthorRequest, DeleteResponse, GetAuthorResponse, Page, Pageable, PatchAuthorRequest} from '../../types';

const getAllAuthors = async (pageable?: Pageable): Promise<Page<GetAuthorResponse>> => {
    const response = await axiosInstance
        .get<Page<GetAuthorResponse>>('/author', {params: pageable});
    return response.data;
};

const getAuthorById = async (id: number): Promise<GetAuthorResponse> => {
    const response = await axiosInstance.get<GetAuthorResponse>(`/author/${id}`);
    return response.data;
};

const createAuthor = async (request: CreateAuthorRequest): Promise<GetAuthorResponse> => {
    const response = await axiosInstance.post<GetAuthorResponse>('/author', request);
    return response.data;
};

const updateAuthor = async (id: number, request: PatchAuthorRequest): Promise<GetAuthorResponse> => {
    const response = await axiosInstance.patch<GetAuthorResponse>(`/author/${id}`, request);
    return response.data;
};

const deleteAuthor = async (id: number): Promise<DeleteResponse> => {
    const response = await axiosInstance.delete<DeleteResponse>(`/author/${id}`);
    return response.data;
};

export const authorService = {
    getAllAuthors,
    getAuthorById,
    createAuthor,
    updateAuthor,
    deleteAuthor,
};
