package com.example.model;

public class UploadFileResponse {
    private final String fileName;
    private final String fileDownloadUri;

    public UploadFileResponse(String fileName, String fileDownloadUri) {
        this.fileName= fileName;
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }
}
