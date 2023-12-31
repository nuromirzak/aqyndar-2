import axiosInstance from '../axiosInstance.ts';
import {CreateAnnotationRequest, DeleteResponse, GetAnnotationResponse, PatchAnnotationRequest} from '../../types';

const getAnnotationById = async (id: number): Promise<GetAnnotationResponse> => {
    const response = await axiosInstance.get<GetAnnotationResponse>(`/annotation/${id}`);
    return response.data;
};

const createAnnotation = async (request: CreateAnnotationRequest): Promise<GetAnnotationResponse> => {
    const response = await axiosInstance.post<GetAnnotationResponse>('/annotation', request);
    return response.data;
};

const updateAnnotation = async (id: number, request: PatchAnnotationRequest): Promise<GetAnnotationResponse> => {
    const response = await axiosInstance.patch<GetAnnotationResponse>(`/annotation/${id}`, request);
    return response.data;
};

const deleteAnnotation = async (id: number): Promise<DeleteResponse> => {
    const response = await axiosInstance.delete<DeleteResponse>(`/annotation/${id}`);
    return response.data;
};

export {
    getAnnotationById,
    createAnnotation,
    updateAnnotation,
    deleteAnnotation,
};
