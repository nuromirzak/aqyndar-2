import {GetAuthorResponse, GetPoemResponse} from "../types";

interface PoemProps {
    poem: GetPoemResponse | undefined;
    author: GetAuthorResponse | undefined;
}

function Poem({poem, author}: PoemProps) {
    if (!poem || !author) {
        console.log('poem or author is undefined');
        return <p>Poem or author not found.</p>;
    }

    const lines = poem.content.split('\n').map((line, index) => (
        <p key={index}>{line}</p>
    ));

    return (
        <div className="container mt-4">
            <div className="card mb-4">
                <div className="card-body">
                    <h3 className="card-title">{poem.title}</h3>
                    <h6 className="card-subtitle mb-2 text-muted">By {author.fullName}</h6>
                    {
                        (poem.schoolGrade ?? poem.complexity) && (
                            <div className="d-flex gap-3 mb-2">
                                {poem.schoolGrade && (
                                    <div className="badge bg-info text-dark">School Grade: {poem.schoolGrade}</div>
                                )}
                                {poem.complexity && (
                                    <div className="badge bg-warning text-dark">Complexity: {poem.complexity}</div>
                                )}
                            </div>
                        )
                    }
                    {poem.topics.length > 0 && (
                        <div className="mb-2">
                            <strong>Topics:</strong> {poem.topics.join(', ')}
                        </div>
                    )}
                    <div className="card-text">{lines}</div>
                </div>
            </div>

            <div className="card mb-4">
                <div className="card-header">
                    Annotations
                </div>
                <ul className="list-group list-group-flush">
                    {poem.annotations.map(annotation => (
                        <li key={annotation.id} className="list-group-item">
                            {annotation.content}
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}


export default Poem;