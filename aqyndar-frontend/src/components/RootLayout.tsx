import {Outlet} from "react-router-dom";
import Navbar from "./Navbar.tsx";

function RootLayout() {
    return (
        <div className="container d-flex flex-column gap-5">
            <Navbar/>
            <Outlet/>
        </div>
    );
}

export default RootLayout;