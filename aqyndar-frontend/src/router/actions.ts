import {AccessTokenPayload, CustomErrorResponse, IAlertInfo} from "../types";
import {authService} from "../api/services/authService.ts";
import axios from 'axios';
import {getUserFromLocalStorage} from "../utils/tokenUtils.ts";

interface LoginActionProps {
    setUser: (user: AccessTokenPayload | null) => void;
}

const processError = (e: unknown): IAlertInfo => {
    let errorTitle = "Unknown error";
    let errorDetail = "An error occurred";

    if (axios.isAxiosError<CustomErrorResponse>(e)) {
        errorTitle = e.response?.data.title || e.name;
        errorDetail = e.response?.data.detail || e.message;
    } else if (e instanceof Error) {
        errorTitle = e.name;
        errorDetail = e.message;
    } else {
        console.log("Caught a non-JavaScript error:", e);
    }

    return {
        error: true,
        title: errorTitle,
        detail: errorDetail,
    };
};

const extractFormData = async (request: Request, fields: string[]): Promise<Record<string, string>> => {
    const data = await request.formData();
    return fields.reduce((acc, field) => {
        const value = data.get(field);
        acc[field] = typeof value === 'string' ? value : "";
        return acc;
    }, {} as Record<string, string>);
};

export function loginAction({setUser}: LoginActionProps): ({request}: { request: Request }) => Promise<IAlertInfo> {
    return async ({request}: { request: Request }): Promise<IAlertInfo> => {
        const {email, password} = await extractFormData(request, ["email", "password"]);

        try {
            const response = await authService.signin({email, password});
            localStorage.setItem("accessToken", response.accessToken);
            localStorage.setItem("refreshToken", response.refreshToken);
            setUser(getUserFromLocalStorage());
            return {error: false, title: "You have successfully logged in"};
        } catch (e) {
            return processError(e);
        }
    }
}

export async function registerAction({request}: { request: Request }): Promise<IAlertInfo> {
    const {email, firstName, password} = await extractFormData(request, ["email", "firstName", "password"]);

    try {
        await authService.signup({email, firstName, password});
        return {error: false, title: "You have successfully registered"};
    } catch (e) {
        return processError(e);
    }
}
