package org.example.solid.atm;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.example.solid.atm.currency.Denomination;
import org.example.solid.atm.logic.WithdrawalStrategy;
import org.example.solid.atm.storage.CannotDispenseAmountException;
import org.example.solid.atm.storage.CashStorage;
import org.example.solid.atm.storage.InsufficientFundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmImpl implements Atm {
    private static final Logger logger = LoggerFactory.getLogger(AtmImpl.class);

    private final CashStorage cashStorage;
    private final WithdrawalStrategy withdrawalStrategy;

    public AtmImpl(CashStorage cashStorage, WithdrawalStrategy withdrawalStrategy) {
        this.cashStorage = Objects.requireNonNull(cashStorage, "CashStorage cannot be null");
        this.withdrawalStrategy = Objects.requireNonNull(withdrawalStrategy, "WithdrawStrategy cannot be null");
        logger.info(
                "ATM Service initialized with storage: {} and strategy: {}",
                cashStorage.getClass().getSimpleName(),
                withdrawalStrategy.getClass().getSimpleName());
    }

    @Override
    public void deposit(Map<Denomination, Integer> notes) {
        String formattedNotes = formatNotes(notes);
        logger.info("Attempting deposit: {}", formattedNotes);

        cashStorage.deposit(notes);
        long currentBalance = cashStorage.getTotalBalance();
        logger.info("Deposit successful. Current balance: {}", currentBalance);
    }

    @Override
    public Map<Denomination, Integer> withdraw(int amount)
            throws InsufficientFundsException, CannotDispenseAmountException {
        logger.info("Attempting to withdraw amount: {}", amount);

        Map<Denomination, Integer> currentStock = cashStorage.getCurrentStock();
        if (logger.isDebugEnabled()) {
            logger.debug("Current stock available for withdrawal calculation: {}", formatNotes(currentStock));
        }

        Map<Denomination, Integer> notesToDispense;
        notesToDispense = withdrawalStrategy.calculateWithdrawal(amount, currentStock);
        if (logger.isDebugEnabled()) {
            logger.debug("Calculated  notes to dispense: {}", formatNotes(notesToDispense));
        }
        try {
            cashStorage.withdraw(notesToDispense);
            String formattedDispensed = formatNotes(notesToDispense);
            long finalBalance = cashStorage.getTotalBalance();

            logger.info("Withdrawal successful. Dispensed: {}. Current balance: {}", formattedDispensed, finalBalance);
            return notesToDispense;
        } catch (InsufficientFundsException e) {
            throw new InsufficientFundsException(
                    "Withdrawal failed due to insufficient funds during final removal: " + e.getMessage());
        }
    }

    @Override
    public long getBalance() {
        long balance = cashStorage.getTotalBalance();
        logger.info("Current ATM Balance: {}", balance);
        return balance;
    }

    private String formatNotes(Map<Denomination, Integer> notes) {
        if (notes == null || notes.isEmpty()) return "[]";
        String content = notes.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() > 0)
                .sorted(Map.Entry.comparingByKey((d1, d2) -> Integer.compare(d2.getValue(), d1.getValue())))
                .map(e -> e.getValue() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
        return "[" + content + "]";
    }
}
