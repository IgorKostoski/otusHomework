package org.example.solid.atm;

import org.example.solid.atm.currency.Denomination;
import org.example.solid.atm.logic.GreddyWithdrawalStrategy;
import org.example.solid.atm.logic.WithdrawalStrategy;
import org.example.solid.atm.storage.CannotDispenseAmountException;
import org.example.solid.atm.storage.CashStorage;
import org.example.solid.atm.storage.InsufficientFundsException;
import org.example.solid.atm.storage.SimpleCashStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

public class AtmDemo {
    private static final Logger logger = LoggerFactory.getLogger(AtmDemo.class);

    public static void main(String[] args) {
        logger.info("--- Starting ATM Demo ---");

        Map<Denomination, Integer> initialCash = new EnumMap<>(Denomination.class);
        initialCash.put(Denomination.R_1000, 10);
        initialCash.put(Denomination.R_500, 20);
        initialCash.put(Denomination.R_100, 50);
        initialCash.put(Denomination.R_50, 100);
        logger.debug("Setting initial cash: {}", initialCash);

        CashStorage storage = new SimpleCashStorage(initialCash);
        WithdrawalStrategy strategy = new GreddyWithdrawalStrategy();

        Atm atm = new AtmImpl(storage, strategy);

        logger.info("--- Initial State Check ---");
        atm.getBalance();

        logger.info("\n--- Performing Deposit ---");
        Map<Denomination, Integer> depositNotes = new EnumMap<>(Denomination.class);
        depositNotes.put(Denomination.R_100, 10);
        depositNotes.put(Denomination.R_10, 20);
        atm.deposit(depositNotes);

        logger.info("\n--- Performing Withdrawals ---");
        withdrawAndReport(atm, 3880);
        withdrawAndReport(atm, 5000);
        withdrawAndReport(atm, 125);
        withdrawAndReport(atm, 125);
        withdrawAndReport(atm, 200000);
        withdrawAndReport(atm, 50);
        withdrawAndReport(atm, 10);
        withdrawAndReport(atm, 190);
        withdrawAndReport(atm, 10);
        withdrawAndReport(atm, 10);
        withdrawAndReport(atm, 10);

        logger.info("\n--- Final State Check ---");
        atm.getBalance();

        if (storage instanceof SimpleCashStorage simpleStorage) {
            logger.debug("Final Stock details: {}", simpleStorage.getCurrentStock());
        } else {
            logger.debug(
                    "Final Stock details not available (storage type: {})",
                    storage.getClass().getSimpleName());
        }

        logger.info("--- ATM Demo Finished ---");
    }

    private static void withdrawAndReport(Atm atm, int amount) {
        try {
            atm.withdraw(amount);
        } catch (InsufficientFundsException | CannotDispenseAmountException e) {
            logger.error("<<< Withdrawal request failed for amount {}: {}", amount, e.getMessage());
        } finally {
            logger.debug("Checking balance after withdrawal attempt for {}", amount);
            atm.getBalance();
            logger.info("-----------------------------");
        }
    }
}
