import {Form, useLoaderData} from "react-router-dom";
import {GetAuthorResponse, IAlertInfo} from "../types";
import "./styles/Author.scss";
import {useContext, useState} from "react";
import {UserContext} from "../contexts/UserContext.tsx";
import StatusAlert from "../components/StatusAlert.tsx";
import {processError} from "../router/actions.ts";
import {authorService} from "../api/services/authorService.ts";

export default function AuthorPage() {
    const {user} = useContext(UserContext);
    const loaderData = useLoaderData() as GetAuthorResponse | null;
    const isEditable = user !== null;
    const [deleteStatus, setDeleteStatus] = useState<IAlertInfo | null>(null);

    if (!loaderData) {
        return <p>Author not found.</p>;
    }

    const onDeleteClick = function () {
        const id = loaderData.id;

        authorService.deleteAuthor(id)
            .then(() => {
                setDeleteStatus({
                    error: false,
                    title: 'Poem deleted successfully.'
                });
            })
            .catch(e => {
                setDeleteStatus(processError(e));
            });
    }

    const author = loaderData;
    const imageUrl = `https://via.placeholder.com/500?text=${encodeURIComponent(author.fullName)}`;

    return (
        <div className="container">
            <div className="text-center">
                <img src={imageUrl} alt={author.fullName} className="author-image rounded mb-3"
                     width={250} height={250}/>
                <div className="d-flex justify-content-center gap-3">
                    <h3>{loaderData.fullName}</h3>
                    {isEditable && (
                        <div className="d-flex align-items-start gap-3">
                            <Form action={`edit`} method="get">
                                <button type="submit" className="btn btn-primary">Edit</button>
                            </Form>
                            <button type="button" className="btn btn-danger" onClick={onDeleteClick}>Delete</button>
                        </div>
                    )}
                    {deleteStatus && (
                        <StatusAlert {...deleteStatus}/>
                    )}
                </div>
                <p>Poems Count: {author.poemsCount}</p>
                <p>More information about the author...</p>
            </div>
        </div>
    );
}