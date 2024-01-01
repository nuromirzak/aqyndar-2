import {UserContext} from "../contexts/UserContext.tsx";
import {useContext} from "react";
import StatusAlert from "../components/StatusAlert.tsx";
import {Form, useActionData} from "react-router-dom";
import {IAlertInfo} from "../types";

export default function CreateAuthorPage() {
    const {user} = useContext(UserContext);
    const data = useActionData() as IAlertInfo | undefined;

    if (!user) {
        return (
            <div>
                <h1>Not logged in</h1>
            </div>
        );
    }

    return (
        <div className="container-fluid d-flex flex-column gap-3">
            <div className="row justify-content-start">
                <div className="col-md-6 d-flex flex-column gap-3">
                    <h2>Register</h2>
                    <Form action="../create" method="post" className="d-flex flex-column gap-3">
                        <div>
                            <label htmlFor="fullName" className="form-label">Full name</label>
                            <input type="text" id="fullName" name="fullName"
                                   className="form-control"/>
                        </div>
                        <button type="submit" className="btn btn-primary w-100">Create</button>
                    </Form>
                    {data && <StatusAlert {...data} className='m-0'/>}
                </div>
            </div>
        </div>
    );
}
