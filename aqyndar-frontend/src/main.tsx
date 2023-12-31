import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.scss'
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import {UserProvider} from "./contexts/UserContext.tsx";
import App from "./App.tsx";


ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <UserProvider>
            <App/>
        </UserProvider>
    </React.StrictMode>,
)
