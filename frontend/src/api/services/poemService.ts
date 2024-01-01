import axiosInstance from '../axiosInstance.ts';
import { Pageable, CreatePoemRequest, PatchPoemRequest, GetPoemResponse, DeleteResponse, GetTopicResponse } from '../../types';

const getAllPoems = async (pageable?: Pageable): Promise<GetPoemResponse[]> => {
    const response = await axiosInstance.get<GetPoemResponse[]>('/poem', { params: pageable });
    return response.data;
};

const getPoemById = async (id: number): Promise<GetPoemResponse> => {
    const response = await axiosInstance.get<GetPoemResponse>(`/poem/${id}`);
    return response.data;
};

const getAllTopics = async (): Promise<GetTopicResponse[]> => {
    const response = await axiosInstance.get<GetTopicResponse[]>('/poem/topics');
    return response.data;
};

const createPoem = async (request: CreatePoemRequest): Promise<GetPoemResponse> => {
    const response = await axiosInstance.post<GetPoemResponse>('/poem', request);
    return response.data;
};

const updatePoem = async (id: number, request: PatchPoemRequest): Promise<GetPoemResponse> => {
    const response = await axiosInstance.patch<GetPoemResponse>(`/poem/${id}`, request);
    return response.data;
};

const deletePoem = async (id: number): Promise<DeleteResponse> => {
    const response = await axiosInstance.delete<DeleteResponse>(`/poem/${id}`);
    return response.data;
};

export const poemService = {
    getAllPoems,
    getPoemById,
    getAllTopics,
    createPoem,
    updatePoem,
    deletePoem,
};
