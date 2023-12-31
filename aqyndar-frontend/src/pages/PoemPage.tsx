import {useLoaderData} from "react-router-dom";
import {PoemLoaderResponse} from "../types";
import PoemComponent from "../components/Poem.tsx";

export default function PoemPage() {
    const loaderData = useLoaderData() as PoemLoaderResponse | null;

    if (!loaderData) {
        return <p>Poem or author not found.</p>;
    }

    const {poem, author} = loaderData;

    return (
        <>
            <PoemComponent poem={poem} author={author}/>
        </>
    )
}

