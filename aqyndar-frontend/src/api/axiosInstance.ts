import axios, {AxiosError} from 'axios';
import {authService} from "./services/authService.ts";

const BASE_URL = typeof import.meta.env.VITE_API_BASE_URL === 'string' ? import.meta.env.VITE_API_BASE_URL : '';

const axiosInstance = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
})

axiosInstance.interceptors.request.use(config => {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
        config.headers['Authorization'] = `Bearer ${accessToken}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

const retryMap = new Map<string, boolean>();

axiosInstance.interceptors.response.use(response => response, async (error: AxiosError) => {
    console.error('Error from axios interceptor', error);
    const originalRequest = error.config;
    if (originalRequest === undefined) {
        return Promise.reject(error);
    }
    const FORBIDDEN = 403;
    const url = originalRequest.url || '';
    const method = originalRequest.method || '';
    const requestKey = `${method}:${url}`;
    if (error.response?.status === FORBIDDEN && !retryMap.get(requestKey)) {
        retryMap.set(requestKey, true);
        try {
            const storedRefreshToken = localStorage.getItem('refreshToken');

            if (!storedRefreshToken) {
                return Promise.reject(new Error('No refresh token stored'));
            }

            const newAccessToken = await authService.refreshAccessToken({refreshToken: storedRefreshToken});

            localStorage.setItem('accessToken', newAccessToken.accessToken);
            console.log('Access token refreshed');

            return axiosInstance(originalRequest);
        } catch (refreshError) {
            console.error('Unable to refresh token', refreshError);
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            retryMap.delete(requestKey);
            return Promise.reject(refreshError);
        }
    }

    return Promise.reject(error);
});

export default axiosInstance;
