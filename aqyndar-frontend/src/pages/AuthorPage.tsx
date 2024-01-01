import {useLoaderData} from "react-router-dom";
import {GetAuthorResponse} from "../types";
import "./styles/Author.scss";

export default function AuthorPage() {
    const loaderData = useLoaderData() as GetAuthorResponse | null;

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
                <h2>{author.fullName}</h2>
                <p>Poems Count: {author.poemsCount}</p>
                <p>More information about the author...</p>
            </div>
        </div>
    );
}