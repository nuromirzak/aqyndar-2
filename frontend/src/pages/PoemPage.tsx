import {useLoaderData} from "react-router-dom";
import {IAlertInfo, PoemLoaderResponse} from "../types";
import PoemComponent from "../components/Poem.tsx";
import {UserContext} from "../contexts/UserContext.tsx";
import {useContext, useState} from "react";
import {poemService} from "../api/services/poemService.ts";
import {processError} from "../router/actions.ts";

export default function PoemPage() {
    const {user} = useContext(UserContext);
    const loaderData = useLoaderData() as PoemLoaderResponse | null;
    const [status, setStatus] = useState<IAlertInfo | null>(null);

    if (!loaderData) {
        return <p>Poem or author not found.</p>;
    }

    const onDeleteClick = function () {
        const id = loaderData.poem.id;

        poemService.deletePoem(id)
            .then(() => {
                setStatus({
                    error: false,
                    title: 'Poem deleted successfully.'
                });
            })
            .catch(e => {
                setStatus(processError(e));
            });
    }

    const {poem, author} = loaderData;

    return (
        <>
            <PoemComponent poem={poem} author={author} isEditable={user !== null} deleteStatus={status}
                           onDeleteClick={onDeleteClick}/>
        </>
    )
}

