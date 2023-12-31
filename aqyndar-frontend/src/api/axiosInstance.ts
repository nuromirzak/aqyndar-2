import axios from 'axios';
import {authService} from "./services/authService.ts";

const axiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
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


axiosInstance.interceptors.response.use(response => response, async (error) => {
    console.error('Error from axios interceptor', error);
    const originalRequest = error.config;
    const FORBIDDEN = 403;

    if (error.response?.status === FORBIDDEN && !originalRequest._retry) {
        console.log('Forbidden, trying to refresh token');
        originalRequest._retry = true;
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
            return Promise.reject(refreshError);
        }
    }

    return Promise.reject(error);
});

export default axiosInstance;
