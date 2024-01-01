import {Outlet} from "react-router-dom";
import Navbar from "./Navbar.tsx";
import Footer from "./Footer.tsx";

function RootLayout() {
    return (
        <div className="container d-flex flex-column gap-5 min-vh-100">
            <Navbar/>
            <Outlet/>
            <Footer/>
        </div>
    );
}

export default RootLayout;