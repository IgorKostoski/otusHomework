package org.example.solid.atm.storage;

import org.example.solid.atm.currency.Denomination;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class SimpleCashStorage implements CashStorage {
    private final Map<Denomination, Integer> cells = new EnumMap<>(Denomination.class);
    private final Lock lock = new ReentrantLock();

    public SimpleCashStorage() {
        for (Denomination d : Denomination.values()) {
            cells.put(d, 0);
        }
    }

    public SimpleCashStorage(Map<Denomination, Integer> initialCash) {
        this();
        deposit(initialCash);
    }

    @Override
    public void deposit(Map<Denomination, Integer> notes) {
        lock.lock();
        try {
            if (notes == null) return;
            notes.forEach((denomination, count) -> {
                if (count != null && count > 0) {
                    cells.merge(denomination, count, Integer::sum); // Add to existing count
                }
            });
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void withdraw(Map<Denomination, Integer> notes) throws InsufficientFundsException {
        lock.lock();
        try {
            if (notes == null || notes.isEmpty()) {
                return;
            }
            for (Map.Entry<Denomination, Integer> entry : notes.entrySet()) {
                Denomination denomination = entry.getKey();
                int requestedCount = entry.getValue();
                if (requestedCount <= 0) continue;

                if (cells.getOrDefault(denomination, 0) < requestedCount) {
                    throw new InsufficientFundsException(
                            "Not enough notes of denomination " + denomination + ". Available: "
                                    + cells.getOrDefault(denomination, 0) + ", Requested: "
                                    + requestedCount);
                }
            }
            for (Map.Entry<Denomination, Integer> entry : notes.entrySet()) {
                Denomination denomination = entry.getKey();
                int countToRemove = entry.getValue();
                if (countToRemove > 0) {
                    cells.computeIfPresent(denomination, (d, currentCount) -> currentCount - countToRemove);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getCount(Denomination denomination) {
        lock.lock();
        try {
            return cells.getOrDefault(denomination, 0);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<Denomination, Integer> getCurrentStock() {
        lock.lock();
        try {
            return Collections.unmodifiableMap(new EnumMap<>(cells));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getTotalBalance() {
        lock.lock();
        try {
            long total = 0L;
            for (Map.Entry<Denomination, Integer> entry : cells.entrySet()) {
                total += (long) entry.getKey().getValue() * entry.getValue();
            }
            return total;
        } finally {
            lock.unlock();
        }
    }
}
