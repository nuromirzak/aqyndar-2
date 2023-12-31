import axiosInstance from '../axiosInstance.ts';
import { UpdateReactionRequest, UpdateReactionResponse, GetReactionResponse, TopEntityResponse, ReactedEntity, TopEntity } from '../../types';

const updateReaction = async (updateReactionRequest: UpdateReactionRequest): Promise<UpdateReactionResponse> => {
    const response = await axiosInstance.post<UpdateReactionResponse>('/reaction', updateReactionRequest);
    return response.data;
};

const getReaction = async (reactedEntity: ReactedEntity, reactedEntityId: number): Promise<GetReactionResponse> => {
    const params = { reactedEntity, reactedEntityId };
    const response = await axiosInstance.get<GetReactionResponse>('/reaction', { params });
    return response.data;
};

const getTopEntities = async (topEntity: TopEntity): Promise<TopEntityResponse[]> => {
    const params = { topEntity };
    const response = await axiosInstance.get<TopEntityResponse[]>('/reaction/top', { params });
    return response.data;
};

export const reactionService = {
    updateReaction,
    getReaction,
    getTopEntities,
};
