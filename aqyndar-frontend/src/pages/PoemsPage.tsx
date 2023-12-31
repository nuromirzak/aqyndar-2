import {Link, useLoaderData} from "react-router-dom";
import {GetPoemResponse} from "../types";

export default function PoemsPage() {
    const poems = useLoaderData() as GetPoemResponse[];

    return (
        <div className="container">
            <h2 className="mb-3">Hello to poems page!</h2>
            <div className="row">
                {poems.map((poem) => (
                    <div key={poem.id} className="col-lg-3 col-md-4 col-sm-6 mb-3">
                        <div className="card h-100">
                            <div className="card-body">
                                <h5 className="card-title">{poem.title}</h5>
                                <h6 className="card-subtitle mb-2 text-muted">By {poem.authorName}</h6>
                                <p className="card-text">
                                    {poem.content.length > 100 ? `${poem.content.substring(0, 100)}...` : poem.content}
                                </p>
                                <Link to={poem.id.toString()} className="card-link">Read More</Link>
                            </div>
                            <div className="card-footer">
                                <small className="text-muted">Complexity: {poem.complexity || 'N/A'}</small>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}