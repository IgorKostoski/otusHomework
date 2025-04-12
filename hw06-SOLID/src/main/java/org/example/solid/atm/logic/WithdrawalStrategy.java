package org.example.solid.atm.logic;

import java.util.Map;
import org.example.solid.atm.currency.Denomination;
import org.example.solid.atm.storage.CannotDispenseAmountException;
import org.example.solid.atm.storage.InsufficientFundsException;

public interface WithdrawalStrategy {

    Map<Denomination, Integer> calculateWithdrawal(int amount, Map<Denomination, Integer> availableNotes)
            throws CannotDispenseAmountException, InsufficientFundsException;
}
