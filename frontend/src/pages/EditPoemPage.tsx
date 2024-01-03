import {useContext, useState} from "react";
import {UserContext} from "../contexts/UserContext.tsx";
import {Form, useActionData, useLoaderData} from "react-router-dom";
import {GetAuthorResponse, IAlertInfo, PoemLoaderResponse} from "../types";
import {useCombobox} from "downshift";
import {searchService} from "../api/services/searchService.ts";
import Search from "../components/Search.tsx";
import StatusAlert from "../components/StatusAlert.tsx";

export default function EditPoemPage() {
    const loaderData = useLoaderData() as PoemLoaderResponse | null;
    const {user} = useContext(UserContext);
    const data = useActionData() as IAlertInfo | undefined;

    const [items, setItems] = useState<GetAuthorResponse[]>([]);


    const combobox = useCombobox<GetAuthorResponse>({
        onInputValueChange({inputValue}) {
            const inputValueString = inputValue ?? '';

            searchService.searchAuthors(inputValueString).then((response) => {
                setItems(response);
            }).catch((error) => {
                console.log(error);
            });
        },
        items,
        itemToString(item) {
            return item ? item.fullName : '';
        },
        initialSelectedItem: loaderData?.author,
    })

    if (!user) {
        return (
            <div>
                <h2>You need to be logged in to edit a poem.</h2>
            </div>
        );
    }

    if (!loaderData) {
        return <p>Poem or author not found.</p>;
    }

    const {poem} = loaderData;

    return (
        <div className="container-fluid d-flex flex-column gap-3">
            <div className="row justify-content-start">
                <div className="col-md-6 d-flex flex-column gap-3">
                    <h2>Edit a poem</h2>
                    <Form action="../edit" method="post" className="d-flex flex-column gap-3">
                        <div className={import.meta.env.MODE === 'development' ? 'd-block' : 'd-none'}>
                            <label htmlFor="id" className="form-label">Poem ID</label>
                            <input readOnly name="id" className="form-control" value={poem.id}/>
                        </div>
                        <div>
                            <label htmlFor="title" className="form-label">Poem Title</label>
                            <input type="text" id="title" name="title" className="form-control"
                                   defaultValue={poem.title}/>
                        </div>
                        <div>
                            <label htmlFor="content" className="form-label">Poem Content</label>
                            <textarea id="content" name="content" className="form-control" rows={4}
                                      defaultValue={poem.content}></textarea>
                        </div>
                        <div className={import.meta.env.MODE === 'development' ? 'd-block' : 'd-none'}>
                            <label htmlFor="authorId" className="form-label">Select Author</label>
                            <input readOnly name="authorId" className="form-control"
                                   value={combobox.selectedItem?.id ?? ''}
                            />
                        </div>
                        <Search comboboxProps={combobox} authors={items}/>
                        <div>
                            <label htmlFor="schoolGrade" className="form-label">School Grade (optional)</label>
                            <input type="number" id="schoolGrade" name="schoolGrade" className="form-control"
                                   defaultValue={poem.schoolGrade}/>
                        </div>
                        <div>
                            <label htmlFor="complexity" className="form-label">Complexity (optional)</label>
                            <input type="number" id="complexity" name="complexity" className="form-control"
                                   defaultValue={poem.complexity}/>
                        </div>
                        <div>
                            <label htmlFor="topics" className="form-label">Topics (tags, separated by commas)</label>
                            <input type="text" id="topics" name="topics" className="form-control"
                                   defaultValue={poem.topics.join(', ')}/>
                        </div>
                        <button type="submit" className="btn btn-primary w-100">Create</button>
                    </Form>
                    {data && <StatusAlert {...data} className='m-0'/>}
                </div>
            </div>
        </div>
    );
}
