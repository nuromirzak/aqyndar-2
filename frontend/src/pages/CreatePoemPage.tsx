import {Form, useActionData, useLoaderData} from "react-router-dom";
import {GetAuthorResponse, IAlertInfo} from "../types";
import StatusAlert from "../components/StatusAlert.tsx";
import {useContext} from "react";
import {UserContext} from "../contexts/UserContext.tsx";
import './styles/CreatePoemPage.scss';

export default function CreatePoemPage() {
    const authors = useLoaderData() as GetAuthorResponse[];
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
                            <label htmlFor="title" className="form-label">Poem Title</label>
                            <input type="text" id="title" name="title" className="form-control"/>
                        </div>
                        <div>
                            <label htmlFor="content" className="form-label">Poem Content</label>
                            <textarea id="content" name="content" className="form-control" rows={4}></textarea>
                        </div>
                        <div>
                            <label htmlFor="authorId" className="form-label">Select Author</label>
                            <select id="authorId" name="authorId" className="form-select">
                                {authors.map(author => (
                                    <option key={author.id} value={author.id}>{author.fullName}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label htmlFor="schoolGrade" className="form-label">School Grade (optional)</label>
                            <input type="number" id="schoolGrade" name="schoolGrade" className="form-control"/>
                        </div>
                        <div>
                            <label htmlFor="complexity" className="form-label">Complexity (optional)</label>
                            <input type="number" id="complexity" name="complexity" className="form-control"/>
                        </div>
                        <div>
                            <label htmlFor="topics" className="form-label">Topics (tags, separated by commas)</label>
                            <input type="text" id="topics" name="topics" className="form-control"/>
                        </div>
                        <button type="submit" className="btn btn-primary w-100">Create</button>
                    </Form>
                    {data && <StatusAlert {...data} className='m-0'/>}
                </div>
            </div>
        </div>
    );
}