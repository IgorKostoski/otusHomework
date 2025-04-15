package org.example.solid.atm.logic;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.example.solid.atm.currency.Denomination;
import org.example.solid.atm.storage.CannotDispenseAmountException;
import org.example.solid.atm.storage.InsufficientFundsException;

public class GreddyWithdrawalStrategy implements WithdrawalStrategy {

    @Override
    public Map<Denomination, Integer> calculateWithdrawal(int amount, Map<Denomination, Integer> availableNotes)
            throws CannotDispenseAmountException, InsufficientFundsException {

        if (amount <= 0) {
            throw new CannotDispenseAmountException("Withdrawal amount must be positive.");
        }

        // Optional: Quick check if total available is enough
        long totalAvailable = availableNotes.entrySet().stream()
                .mapToLong(entry -> (long) entry.getKey().getValue() * entry.getValue())
                .sum();
        if (totalAvailable < amount) {
            throw new InsufficientFundsException(
                    "Total available funds (" + totalAvailable + ") are less than requested amount (" + amount + ").");
        }

        Map<Denomination, Integer> withdrawalNotes = new EnumMap<>(Denomination.class);
        int remainingAmount = amount;
        List<Denomination> sortedDenominations = Denomination.getSortedDescending();

        for (Denomination denomination : sortedDenominations) {
            int value = denomination.getValue();
            int availableCount = availableNotes.getOrDefault(denomination, 0);

            if (remainingAmount >= value && availableCount > 0) {
                int notesToUse = Math.min(remainingAmount / value, availableCount);
                if (notesToUse > 0) {
                    withdrawalNotes.put(denomination, notesToUse);
                    remainingAmount -= notesToUse * value;
                }
            }

            if (remainingAmount == 0) {
                break; // Amount successfully prepared
            }
        }

        if (remainingAmount > 0) {
            // We couldn't make the exact amount with the available denominations using this strategy
            throw new CannotDispenseAmountException("Cannot dispense the exact amount " + amount
                    + " with available banknotes using the current strategy." + " Remaining unfulfilled: "
                    + remainingAmount);
        }

        return withdrawalNotes;
    }
}
