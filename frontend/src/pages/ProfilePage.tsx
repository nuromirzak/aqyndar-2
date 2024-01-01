import {Link, useLoaderData} from "react-router-dom";
import {GetWhoResponse} from "../types";

export default function ProfilePage() {
    const data = useLoaderData() as GetWhoResponse | null;

    if (!data) {
        return (
            <div className="container">
                <div className="card my-5">
                    <div className="card-body">
                        <h5 className="card-title">Profile</h5>
                        <p className="card-text">Please login to view your profile.</p>
                        <Link to={'/login'} className="btn btn-primary">Login</Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div>
            <div className="container">
                <div className="card my-5">
                    <div className="card-body">
                        <h5 className="card-title">Profile</h5>
                        <p className="card-text"><strong>ID:</strong> {data.id}</p>
                        <p className="card-text"><strong>Email:</strong> {data.email}</p>
                        <p className="card-text"><strong>Name:</strong> {data.firstName}</p>
                        <p className="card-text"><strong>Account
                            Created:</strong> {new Date(data.createdAt).toLocaleDateString()}</p>
                        <p className="card-text"><strong>Roles:</strong> {data.roles.join(', ')}</p>
                    </div>
                </div>
            </div>
        </div>
    );
}