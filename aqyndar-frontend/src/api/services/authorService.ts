import axiosInstance from '../axiosInstance.ts';
import {GetAuthorResponse, CreateAuthorRequest, PatchAuthorRequest, DeleteResponse, Pageable} from '../../types';

const getAllAuthors = async (pageable?: Pageable): Promise<GetAuthorResponse[]> => { // Replace 'any' with your pageable type
    const response = await axiosInstance.get<GetAuthorResponse[]>('/author', { params: pageable });
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
