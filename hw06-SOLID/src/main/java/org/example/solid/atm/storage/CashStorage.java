package org.example.solid.atm.storage;

import java.util.Map;
import org.example.solid.atm.currency.Denomination;

public interface CashStorage {

    void deposit(Map<Denomination, Integer> notes);

    void withdraw(Map<Denomination, Integer> notes) throws InsufficientFundsException;

    int getCount(Denomination denomination);

    Map<Denomination, Integer> getCurrentStock();

    long getTotalBalance();
}
