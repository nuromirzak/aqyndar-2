import axiosInstance from "../axiosInstance.ts";
import {InitiateTextToSpeechConversionResponse, TextToSpeechResultResponse, TextToSpeechStatus} from "../../types";

async function initiateTextToSpeechConversion(text: string): Promise<InitiateTextToSpeechConversionResponse> {
    const params = { text };
    const response = await axiosInstance.get<InitiateTextToSpeechConversionResponse>("/voice/convert", { params });
    return response.data;
}

async function getConversionResult(uuid: string): Promise<TextToSpeechResultResponse> {
    const response = await axiosInstance.get<TextToSpeechResultResponse>(`/voice/result/${uuid}`);

    if (response.data.status === TextToSpeechStatus.COMPLETED) {
        response.data.url = `${axiosInstance.defaults.baseURL}/files/${response.data.url}`;
    }

    return response.data;
}

export const voiceService = {
    initiateTextToSpeechConversion,
    getConversionResult,
};
