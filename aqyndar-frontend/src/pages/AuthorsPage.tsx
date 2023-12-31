import {Link, useLoaderData} from "react-router-dom";
import {GetAuthorResponse} from "../types";

export default function AuthorsPage() {
    const authors = useLoaderData() as GetAuthorResponse[];
    const sortedAuthors = authors.sort((a, b) => b.poemsCount - a.poemsCount);

    return (
        <div className="container">
            <h2>Authors</h2>
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
