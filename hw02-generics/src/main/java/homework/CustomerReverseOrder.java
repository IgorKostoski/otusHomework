package homework;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {

    private final Deque<Customer> customerDequer = new ArrayDeque<>();

    public void add(Customer customer) {
        customerDequer.push(customer);
    }

    public Customer take() {

        return customerDequer.pop();
    }
}
