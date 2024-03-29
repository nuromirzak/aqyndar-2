import {Form, useActionData, useNavigate} from "react-router-dom";
import {IAlertInfo} from "../types";
import StatusAlert from "../components/StatusAlert.tsx";
import {useEffect, useState} from "react";

export default function RegisterPage() {
    const navigate = useNavigate();
    const data = useActionData() as IAlertInfo | undefined;
    const [registerSuccessful, setRegisterSuccessful] = useState(
        localStorage.getItem("accessToken") !== null
    );

    useEffect(() => {
        if ((data && !data.error) ?? registerSuccessful) {
            setRegisterSuccessful(true);
            const timer = setTimeout(() => {
                navigate('/login');
            }, 2500);

            return () => {
                clearTimeout(timer);
            };
        }
    }, [data, registerSuccessful, navigate]);

    return (
        <div className="container-fluid d-flex flex-column gap-3">
            <div className="row justify-content-center">
                <div className="col-md-6 d-flex flex-column gap-3">
                    <h2 className="text-center">Register</h2>
                    {registerSuccessful ? (
                            <StatusAlert error={false} title="Register successful. Redirecting..."/>
                        ) :
                        <Form action="/register" method="post" className="card card-body gap-3">
                            <div>
                                <label htmlFor="email" className="form-label">Email</label>
                                <input autoComplete="email" type="text" id="email" name="email"
                                       className="form-control"/>
                            </div>
                            <div>
                                <label htmlFor="firstName" className="form-label">First name</label>
                                <input type="text" id="firstName" name="firstName"
                                       className="form-control"/>
                            </div>
                            <div>
                                <label htmlFor="password" className="form-label">Password</label>
                                <input type="password" id="password" name="password" className="form-control"
                                       autoComplete="current-password"/>
                            </div>
                            <button type="submit" className="btn btn-primary w-100">Login</button>
                        </Form>
                    }
                    {data && data.error && <StatusAlert {...data} className='m-0'/>}
                </div>
            </div>
        </div>
    );
}
