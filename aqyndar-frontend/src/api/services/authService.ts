import axiosInstance from '../axiosInstance.ts';
import {
    AccessTokenOnlyResponse,
    JwtResponse,
    RefreshRequest,
    SigninRequest,
    SignupRequest,
    SignupResponse
} from '../../types';

const signup = async (signupRequest: SignupRequest): Promise<SignupResponse> => {
    const response = await axiosInstance.post<SignupResponse>('/signup', signupRequest);
    return response.data;
};

const signin = async (signinRequest: SigninRequest): Promise<JwtResponse> => {
    const response = await axiosInstance.post<JwtResponse>('/signin', signinRequest);
    return response.data;
};

const refreshAccessToken = async (refreshRequest: RefreshRequest): Promise<AccessTokenOnlyResponse> => {
    const response = await axiosInstance.post<AccessTokenOnlyResponse>('/refresh', refreshRequest);
    return response.data;
};

export const authService = {
    signup,
    signin,
    refreshAccessToken,
};
