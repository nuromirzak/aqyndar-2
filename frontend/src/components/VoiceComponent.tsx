import {useEffect, useState} from "react";
import {IAlertInfo, TextToSpeechResultResponse, TextToSpeechStatus} from "../types";
import {voiceService} from "../api/services/voiceService.ts";
import {processError} from "../router/actions.ts";
import StatusAlert from "./StatusAlert.tsx";

interface VoiceComponentProps {
    text: string;
}

export default function VoiceComponent({text}: VoiceComponentProps) {
    const [response, setResponse] = useState<TextToSpeechResultResponse | null>(null);
    const [requestId, setRequestId] = useState<string | null>(null);
    const [polling, setPolling] = useState<boolean>(false);
    const [alertInfo, setAlertInfo] = useState<IAlertInfo | null>(null);

    const onVoiceClick = function () {
        voiceService.initiateTextToSpeechConversion(text)
            .then(r => {
                setRequestId(r.uuid);
                setPolling(true);
            })
            .catch(e => {
                const processAlertInfo = processError(e);
                setResponse({
                    status: TextToSpeechStatus.FAILED,
                    error: processAlertInfo.title
                });
                setAlertInfo(processAlertInfo);
            });
    }

    useEffect(() => {
        let intervalId: number | null = null;

        if (polling && requestId) {
            intervalId = setInterval(() => {
                voiceService.getConversionResult(requestId)
                    .then(result => {
                        setResponse(result);
                        if (result.status !== TextToSpeechStatus.PROCESSING) {
                            console.log('result.status !== TextToSpeechStatus.PROCESSING');
                            setPolling(false);
                        }
                    })
                    .catch(e => {
                        const processAlertInfo = processError(e);
                        setResponse({
                            status: TextToSpeechStatus.FAILED,
                            error: processAlertInfo.title
                        });
                        setAlertInfo(processAlertInfo);
                        setPolling(false);
                    });
            }, 2000);
        }

        return () => {
            if (intervalId) {
                clearInterval(intervalId);
            }
        };
    }, [polling, requestId]);

    return (
        <div className="d-flex flex-row gap-3 align-items-start">
            {
                requestId === null &&
                <button className="btn btn-primary" onClick={onVoiceClick}>Play</button>
            }
            {
                ((requestId !== null && response === null) || response?.status === TextToSpeechStatus.PROCESSING) &&
                <div className="spinner-border" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
            }
            {
                response !== null && response.status === TextToSpeechStatus.FAILED &&
                <StatusAlert error={true} title={alertInfo?.title ?? response.error} detail={alertInfo?.detail}/>
            }
            {
                response !== null && response.status === TextToSpeechStatus.COMPLETED &&
                <audio controls>
                  <source src={response.url} type="audio/wav"/>
                </audio>
            }
        </div>
    );
}