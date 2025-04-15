package org.example.solid.atm.storage;

// Exception for withdrawal calculation issues
public class CannotDispenseAmountException extends Exception {
    public CannotDispenseAmountException(String message) {
        super(message);
    }
}
