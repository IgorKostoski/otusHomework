package homework;

import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    private final TreeMap<Customer, String> customerMap =
            new TreeMap<>((c1, c2) -> Long.compare(c1.getScores(), c2.getScores()));

    public Map.Entry<Customer, String> getSmallest() {

        if (customerMap.isEmpty()) {
            return null;
        }
        Map.Entry<Customer, String> entry = customerMap.firstEntry();
        return new AbstractMap.SimpleEntry<>(entry.getKey().getCopy(), entry.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {

        if (customerMap.isEmpty()) {
            return null;
        }
        Map.Entry<Customer, String> entry = customerMap.higherEntry(customer);
        if (entry == null) {
            return null;
        }

        return new AbstractMap.SimpleEntry<>(entry.getKey().getCopy(), entry.getValue());
    }

    public void add(Customer customer, String data) {
        customerMap.put(customer, data);
    }
}
