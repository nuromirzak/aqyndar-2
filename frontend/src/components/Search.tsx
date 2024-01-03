import {UseComboboxReturnValue} from "downshift";
import {GetAuthorResponse} from "../types";

interface ComboBoxProps {
    comboboxProps: UseComboboxReturnValue<GetAuthorResponse>;
    authors: GetAuthorResponse[];
}

/*
 * Following errors are thrown in the console:
 * 1. You forgot to call the getInputProps getter function on your component / element.
 * 2. You forgot to call the getMenuProps getter function on your component / element.
 * 3. The ref prop "undefined" from getInputProps was not applied correctly on your element.
 * 4. The ref prop "undefined" from getMenuProps was not applied correctly on your element.
 * But, it seems to be working fine.
 */
function ComboBox({ comboboxProps, authors }: ComboBoxProps) {
    const {
        isOpen,
        getToggleButtonProps,
        getLabelProps,
        getMenuProps,
        getInputProps,
        getItemProps,
    } = comboboxProps;

    return (
        <div>
            <div>
                <label className="form-label" {...getLabelProps()}>
                    Choose an author:
                </label>
                <div className="input-group">
                    <input
                        className="form-control"
                        placeholder="Search..."
                        {...getInputProps()}
                    />
                    <button
                        className="btn btn-outline-secondary"
                        aria-label="toggle menu"
                        type="button"
                        {...getToggleButtonProps()}
                    >
                        {isOpen ? <>&#8593;</> : <>&#8595;</>}
                    </button>
                </div>
            </div>
            <ul
                className={`list-group ${(isOpen && authors.length) ? 'd-block' : 'd-none'}`}
                {...getMenuProps()}
            >
                {isOpen &&
                    authors.map((item, index) => (
                        <li
                            className="list-group-item"
                            key={item.id}
                            {...getItemProps({item, index})}
                        >
                            <span>{item.fullName}</span>
                        </li>
                    ))}
            </ul>
        </div>
    )
}

export default ComboBox;