import {jwtDecode} from "jwt-decode";
import {AccessTokenPayload} from "../types";

const isAccessTokenPayload = (object: unknown): object is AccessTokenPayload => {
    if (typeof object === 'object' && object !== null) {
        return (
            'sub' in object &&
            typeof object.sub === 'string' &&
            'exp' in object &&
            typeof object.exp === 'number' &&
            'roles' in object &&
            Array.isArray(object.roles) &&
            'firstName' in object &&
            typeof object.firstName === 'string'
        );
    }
    return false;
};


const getUserFromToken = (token: string): AccessTokenPayload | null => {
    try {
        const decodedToken = jwtDecode<AccessTokenPayload>(token);
        if (isAccessTokenPayload(decodedToken)) {
            return decodedToken;
        }
        return null;
    } catch (error) {
        console.error("Invalid token:", error);
        console.error("Token:", token);
        return null;
    }
};

const isTokenExpired = (token?: string): boolean => {
    token = token ?? localStorage.getItem("accessToken") ?? "";
    try {
        const decoded = jwtDecode(token);
        const currentUnixTimestamp = Math.floor(Date.now() / 1000);
        return Boolean(decoded.exp && decoded.exp < currentUnixTimestamp);
    } catch (error) {
        console.error("Error decoding token:", error);
        console.error("Token:", token);
        return true;
    }
};

const getUserFromLocalStorage = (): AccessTokenPayload | null => {
    const token = localStorage.getItem("accessToken");
    if (token === null) {
        return null;
    }
    return getUserFromToken(token);
}

export {isTokenExpired, getUserFromLocalStorage};
