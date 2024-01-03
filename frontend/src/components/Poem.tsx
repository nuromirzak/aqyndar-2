import {GetAnnotationResponse, GetAuthorResponse, GetPoemResponse} from "../types";
import {useCallback, useEffect, useRef} from "react";
import {Form} from "react-router-dom";

interface PoemProps {
    poem: GetPoemResponse | undefined;
    author: GetAuthorResponse | undefined;
    isEditable: boolean;
}

function Poem({poem, author, isEditable}: PoemProps) {
    const poemTextNode = useRef<HTMLPreElement>(null);

    const createRanges = useCallback(function createRanges(annotations: GetAnnotationResponse[]) {
        if (!poemTextNode.current?.firstChild) {
            console.log(poem);
            console.log('poemTextNode.current or poemTextNode.current.firstChild is null');
            return;
        }

        const ranges: Range[] = [], n: number = annotations.length;

        for (let i = 0; i < n; i++) {
            const annotation = annotations[i];
            const range = document.createRange();
            console.log(poemTextNode.current.firstChild.textContent);
            console.log(annotation);
            range.setStart(poemTextNode.current.firstChild, annotation.startRangeIndex);
            range.setEnd(poemTextNode.current.firstChild, annotation.endRangeIndex);
            ranges.push(range);
        }

        console.log(ranges);

        function createHighlightSpan(annotation: GetAnnotationResponse) {
            const span = document.createElement('span');
            span.style.backgroundColor = 'gray';
            span.style.cursor = 'pointer';
            span.title = annotation.content;

            span.addEventListener('mouseover', () => {
                span.style.backgroundColor = 'yellow';
            });

            span.addEventListener('mouseout', () => {
                span.style.backgroundColor = 'gray';
            });

            return span;
        }

        for (let i = 0; i < n; i++) {
            const range = ranges[i];
            const span = createHighlightSpan(annotations[i]);
            range.surroundContents(span);
        }

    }, [poem]);

    useEffect(() => {
        if (poem === undefined) {
            return;
        }
        const id = setTimeout(() => {
            createRanges(poem.annotations);
        }, 1000);

        return () => {
            clearTimeout(id);
        }
    }, [createRanges, poem]);

    if (!poem || !author) {
        console.log('poem or author is undefined');
        return <p>Poem or author not found.</p>;
    }

    return (
        <div className="container mt-4">
            <div className="card mb-4">
                <div className="card-body">
                    <div className="d-flex gap-3">
                        <h3 className="card-title">{poem.title}</h3>
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
                    <div className="card-text">
                        <pre ref={poemTextNode}>{poem.content}</pre>
                    </div>
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
                            <strong>[{annotation.startRangeIndex}:{annotation.endRangeIndex}]</strong>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}


export default Poem;