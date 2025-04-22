package org.example.solid.atm;

import java.util.Map;
import org.example.solid.atm.currency.Denomination;
import org.example.solid.atm.storage.CannotDispenseAmountException;
import org.example.solid.atm.storage.InsufficientFundsException;

public interface Atm {

    void deposit(Map<Denomination, Integer> notes);

    Map<Denomination, Integer> withdraw(int amount) throws InsufficientFundsException, CannotDispenseAmountException;

    long getBalance();
}
