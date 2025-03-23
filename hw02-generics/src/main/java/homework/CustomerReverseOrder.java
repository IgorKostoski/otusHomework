package homework;

import java.util.Stack;

public class CustomerReverseOrder {

    private final Stack<Customer> customerStack = new Stack<>();

    public void add(Customer customer) {
        customerStack.push(customer);
    }

    public Customer take() {
        if (customerStack.isEmpty()) {
            return null;
        }
        return customerStack.pop();
    }
}
