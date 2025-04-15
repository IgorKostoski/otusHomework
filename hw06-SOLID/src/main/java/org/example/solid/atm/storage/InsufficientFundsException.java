package org.example.solid.atm.storage;

// Base exception for storage issues
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
