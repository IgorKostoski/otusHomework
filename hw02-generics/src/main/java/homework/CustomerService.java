package homework;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// при выполнении ДЗ эту аннотацию надо удалить
public class CustomerService {

    // todo: 3. надо реализовать методы этого класса
    // важно подобрать подходящую Map-у, посмотрите на редко используемые методы, они тут полезны
    private final TreeMap<Customer, String> customerMap =
            new TreeMap<>((c1, c2) -> Long.compare(c1.getScores(), c2.getScores()));
    private final Map<Long, String> dataMap = new HashMap<>();

    public Map.Entry<Customer, String> getSmallest() {
        // Возможно, чтобы реализовать этот метод, потребуется посмотреть как Map.Entry сделан в jdk
        if (customerMap.isEmpty()) {
            return null;
        }
        Map.Entry<Customer, String> entry = customerMap.firstEntry();
        return new AbstractMap.SimpleEntry<>(entry.getKey().getCopy(), entry.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        // это "заглушка, чтобы скомилировать"
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
