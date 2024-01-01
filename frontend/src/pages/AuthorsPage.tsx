import {Link, useLoaderData} from "react-router-dom";
import {GetAuthorResponse} from "../types";
import {useContext} from "react";
import {UserContext} from "../contexts/UserContext.tsx";

export default function AuthorsPage() {
    const {user} = useContext(UserContext);
    const authors = useLoaderData() as GetAuthorResponse[];
    const sortedAuthors = authors.sort((a, b) => b.poemsCount - a.poemsCount);

    return (
        <div className="container d-flex flex-column gap-3">
            <div className="d-flex flex-row gap-2 align-items-center">
                <span className="h2">Authors</span>
                {user && <Link to="/authors/create" className="btn btn-primary">Create</Link>}
            </div>
            <ul className="list-group row-cols-1 row-cols-md-2 row-cols-lg-3">
                {sortedAuthors.map((author) => (
                    <li key={author.id} className="list-group-item d-flex justify-content-between align-items-center">
                        <Link to={`/authors/${author.id}`}>{author.fullName}</Link>
                        <span className="badge bg-primary rounded-pill">{author.poemsCount} Poems</span>
                    </li>
                ))}
            </ul>
        </div>
    );
}
