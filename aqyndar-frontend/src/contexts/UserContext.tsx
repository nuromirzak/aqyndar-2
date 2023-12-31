import {createContext, ReactNode, useState} from "react";
import {AccessTokenPayload} from "../types";

export interface UserContextProps {
    user: AccessTokenPayload | null,
    setUser: (user: AccessTokenPayload | null) => void
}

const UserContext = createContext<UserContextProps>({
    user: null,
    setUser: () => {
    },
});

interface UserProviderProps {
    children: ReactNode;
}

const UserProvider = ({children}: UserProviderProps) => {
    const [user, setUser] = useState<AccessTokenPayload | null>(null);

    return (
        <UserContext.Provider value={{user, setUser}}>
            {children}
        </UserContext.Provider>
    );
};

export {UserContext, UserProvider};
