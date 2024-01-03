import {Form, useActionData} from "react-router-dom";
import {GetAuthorResponse, IAlertInfo} from "../types";
import StatusAlert from "../components/StatusAlert.tsx";
import {useContext, useEffect, useState} from "react";
import {UserContext} from "../contexts/UserContext.tsx";
import './styles/CreatePoemPage.scss';
import {authorService} from "../api/services/authorService.ts";
import {useCombobox} from "downshift";
import {searchService} from "../api/services/searchService.ts";
import Search from "../components/Search.tsx";

export default function CreatePoemPage() {
    const {user} = useContext(UserContext);
    const data = useActionData() as IAlertInfo | undefined;

    const [items, setItems] = useState<GetAuthorResponse[]>([]);

    useEffect(() => {
        authorService.getAllAuthors().then((response) => {
            setItems(response.content.slice(0, 5));
        }).catch((error) => {
            console.log(error);
        });
    }, []);


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
    })

    console.log(import.meta.env.MODE);

    if (!user) {
        return (
            <div>
                <h1>Not logged in</h1>
            </div>
        );
    }

    return (
        <div className="container-fluid d-flex flex-column gap-3">
            <div className="row justify-content-start">
                <div className="col-md-6 d-flex flex-column gap-3">
                    <h2>Register</h2>
                    <Form action="../create" method="post" className="d-flex flex-column gap-3">
                        <div>
                            <label htmlFor="title" className="form-label">Poem Title</label>
                            <input type="text" id="title" name="title" className="form-control"/>
                        </div>
                        <div>
                            <label htmlFor="content" className="form-label">Poem Content</label>
                            <textarea id="content" name="content" className="form-control" rows={4}></textarea>
                        </div>
                        <div className={import.meta.env.MODE === 'development' ? 'd-block' : 'd-none'}>
                            <label htmlFor="authorId" className="form-label">Select Author</label>
                            <input readOnly name="authorId" className="form-control"
                                   value={combobox.selectedItem?.id ?? ''}/>
                        </div>
                        <Search comboboxProps={combobox} authors={items}/>
                        <div>
                            <label htmlFor="schoolGrade" className="form-label">School Grade (optional)</label>
                            <input type="number" id="schoolGrade" name="schoolGrade" className="form-control"/>
                        </div>
                        <div>
                            <label htmlFor="complexity" className="form-label">Complexity (optional)</label>
                            <input type="number" id="complexity" name="complexity" className="form-control"/>
                        </div>
                        <div>
                            <label htmlFor="topics" className="form-label">Topics (tags, separated by commas)</label>
                            <input type="text" id="topics" name="topics" className="form-control"/>
                        </div>
                        <button type="submit" className="btn btn-primary w-100">Create</button>
                    </Form>
                    {data && <StatusAlert {...data} className='m-0'/>}
                </div>
            </div>
        </div>
    );
}