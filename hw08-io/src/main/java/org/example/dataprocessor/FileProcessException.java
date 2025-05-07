package org.example.dataprocessor;

public class FileProcessException extends RuntimeException {
    public FileProcessException(Exception ex) {
        super(ex);
    }

    public FileProcessException(String msg) {
        super(msg);
    }

    public FileProcessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
