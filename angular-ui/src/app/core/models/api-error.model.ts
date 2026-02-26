export interface ApiError {
    code: string;
    message: string;
    timestamp: string;
    fieldErrors?: { [key: string]: string };
}