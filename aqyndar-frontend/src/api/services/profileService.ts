import axiosInstance from '../axiosInstance.ts';
import { GetWhoResponse, GetLikesResponse } from '../../types';

const getCurrentUser = async (): Promise<GetWhoResponse> => {
    const response = await axiosInstance.get<GetWhoResponse>('/profile');
    return response.data;
};

const getUserById = async (id: number): Promise<GetWhoResponse> => {
    const response = await axiosInstance.get<GetWhoResponse>(`/profile/${id}`);
    return response.data;
};

const getLikes = async (id: number): Promise<GetLikesResponse> => {
    const response = await axiosInstance.get<GetLikesResponse>(`/profile/${id}/likes`);
    return response.data;
};

export const profileService = {
    getCurrentUser,
    getUserById,
    getLikes,
};
