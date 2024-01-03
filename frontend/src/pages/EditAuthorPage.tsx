import {UserContext} from "../contexts/UserContext.tsx";
import {useContext} from "react";
import StatusAlert from "../components/StatusAlert.tsx";
import {Form, useActionData, useLoaderData} from "react-router-dom";
import {GetAuthorResponse, IAlertInfo} from "../types";

export default function EditAuthorPage() {
    const loaderData = useLoaderData() as GetAuthorResponse | null;
    const {user} = useContext(UserContext);
    const data = useActionData() as IAlertInfo | undefined;

    if (!user) {
        return (
            <div>
                <h1>Not logged in</h1>
            </div>
        );
    }

    if (!loaderData) {
        return <p>Author not found.</p>;
    }

    return (
        <div className="container-fluid d-flex flex-column gap-3">
            <div className="row justify-content-start">
                <div className="col-md-6 d-flex flex-column gap-3">
                    <h2>Edit an author</h2>
                    <Form action="../edit" method="post" className="d-flex flex-column gap-3">
                        <div className={import.meta.env.MODE === 'development' ? 'd-block' : 'd-none'}>
                            <label htmlFor="id" className="form-label">Poem ID</label>
                            <input readOnly name="id" className="form-control" value={loaderData.id}/>
                        </div>
                        <div>
                            <label htmlFor="fullName" className="form-label">Full name</label>
                            <input type="text" id="fullName" name="fullName"
                                   className="form-control" defaultValue={loaderData.fullName}/>
                        </div>
                        <button type="submit" className="btn btn-primary w-100">Create</button>
                    </Form>
                    {data && <StatusAlert {...data} className='m-0'/>}
                </div>
            </div>
        </div>
    );
}
