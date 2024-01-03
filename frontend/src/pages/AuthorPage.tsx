import {Form, useLoaderData} from "react-router-dom";
import {GetAuthorResponse} from "../types";
import "./styles/Author.scss";
import {useContext} from "react";
import {UserContext} from "../contexts/UserContext.tsx";

export default function AuthorPage() {
    const {user} = useContext(UserContext);
    const loaderData = useLoaderData() as GetAuthorResponse | null;
    const isEditable = user !== null;

    if (!loaderData) {
        return <p>Author not found.</p>;
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
                        <div className="d-flex gap-3">
                            <Form action={`edit`} method="get">
                                <button type="submit" className="btn btn-primary">Edit</button>
                            </Form>
                            <Form action={`delete`} method="post">
                                <button type="submit" className="btn btn-danger">Delete</button>
                            </Form>
                        </div>
                    )}
                </div>
                <p>Poems Count: {author.poemsCount}</p>
                <p>More information about the author...</p>
            </div>
        </div>
    );
}