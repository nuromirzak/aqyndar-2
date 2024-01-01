import {IAlertInfo} from "../types";

interface StatusAlertProps extends IAlertInfo {
    className?: string;
}

export default function StatusAlert({error, title, detail, className}: StatusAlertProps) {
    const alertClass = error ? 'alert-danger' : 'alert-success';
    const combinedClassNames = `alert ${alertClass} ${className ?? ''}`.trim();

    return (
        <div className={combinedClassNames} role="alert">
            <strong>{title}</strong>
            {detail && <div>{detail}</div>}
        </div>
    );
}