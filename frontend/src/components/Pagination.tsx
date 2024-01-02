import {Page} from "../types";
import {Link} from "react-router-dom";

interface PaginationProps {
    page: Page<unknown>
}

export default function Pagination({page}: PaginationProps) {
    const size = page.size;

    return <nav aria-label="Page navigation example">
        <ul className="pagination flex-wrap">
            <li className={`page-item ${page.first ? 'disabled' : ''}`}>
                <Link className="page-link" to={`?page=${page.number}&size=${size}`}>Previous</Link>
            </li>
            {
                Array.from(Array(page.totalPages).keys()).map((pageIndex) => (
                    <li key={pageIndex} className={`page-item ${page.number === pageIndex ? 'active' : ''}`}>
                        <Link className="page-link" to={`?page=${pageIndex + 1}&size=${size}`}>{pageIndex + 1}</Link>
                    </li>
                ))
            }
            <li className={`page-item ${page.last ? 'disabled' : ''}`}>
                <Link className="page-link" to={`?page=${page.number + 2}&size=${size}`}>Next</Link>
            </li>
        </ul>
    </nav>
}
