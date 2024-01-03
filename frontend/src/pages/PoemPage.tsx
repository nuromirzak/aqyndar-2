import {useLoaderData} from "react-router-dom";
import {PoemLoaderResponse} from "../types";
import PoemComponent from "../components/Poem.tsx";
import {UserContext} from "../contexts/UserContext.tsx";
import {useContext} from "react";

export default function PoemPage() {
    const {user} = useContext(UserContext);
    const loaderData = useLoaderData() as PoemLoaderResponse | null;

    if (!loaderData) {
        return <p>Poem or author not found.</p>;
    }

    const {poem, author} = loaderData;

    return (
        <>
            <PoemComponent poem={poem} author={author} isEditable={user !== null}/>
        </>
    )
}

