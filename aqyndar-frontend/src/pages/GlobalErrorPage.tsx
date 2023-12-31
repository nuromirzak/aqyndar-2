import {Link, useRouteError} from "react-router-dom";

export default function GlobalErrorPage() {
    const error = useRouteError() as Error;

    const isDevelopment = import.meta.env.MODE === 'development';

    return (
        <div className="d-flex align-items-center justify-content-center vh-100">
            <div className="text-center">
                <h1 className="display-1 fw-bold">{error.name}</h1>
                <p className="fs-3">{error.message}</p>
                {isDevelopment && (
                    <div className="alert alert-secondary">
                        <pre>{error.stack}</pre>
                    </div>
                )}
                <Link to="/" className="btn btn-primary">Go Home</Link>
            </div>
        </div>
    )
}