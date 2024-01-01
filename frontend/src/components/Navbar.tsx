import {NavLink} from 'react-router-dom';
import {useContext, useEffect} from "react";
import {profileService} from "../api/services/profileService.ts";
import {UserContext} from "../contexts/UserContext.tsx";
import {getUserFromLocalStorage, hasToken, isTokenExpired} from "../utils/tokenUtils.ts";

export default function Navbar() {
    const {user, setUser} = useContext(UserContext);

    useEffect(() => {
        if (!user) {
            if (hasToken() && isTokenExpired()) {
                profileService.getCurrentUser()
                    .catch(e => {
                        console.error(e);
                    });
            }
            setUser(getUserFromLocalStorage());
        }
    }, [user, setUser]);

    return (
        <nav className="navbar navbar-expand-lg bg-body-tertiary">
            <div className="container">
                <NavLink className="navbar-brand" to="/">Navbar</NavLink>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                        data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false"
                        aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <ul className="navbar-nav me-auto gap-3">
                        <li className="nav-item">
                            <NavLink className="nav-link" to="/poems">Poems</NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className="nav-link" to="/authors">Authors</NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className="nav-link" to="/leaderboard">Leaderboard</NavLink>
                        </li>
                    </ul>
                    <ul className="navbar-nav gap-3">
                        {user ? (
                            <li className="nav-item">
                                <NavLink to="/profile">Welcome, {user.firstName}</NavLink>
                            </li>
                        ) : (
                            <>
                                <li className="nav-item">
                                    <NavLink to="/login" className="nav-link">Login</NavLink>
                                </li>
                                <li className="nav-item">
                                    <NavLink to="/register" className="btn btn-primary">Register</NavLink>
                                </li>
                            </>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
}
