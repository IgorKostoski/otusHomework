package homework;

import java.util.Stack;

@SuppressWarnings({"java:S1186", "java:S1135", "java:S1172"}) // при выполнении ДЗ эту аннотацию надо удалить
public class CustomerReverseOrder {

    // todo: 2. надо реализовать методы этого класса
    // надо подобрать подходящую структуру данных, тогда решение будет в "две строчки"
    private final Stack<Customer> customerStack = new Stack<>();

    public void add(Customer customer) {
        customerStack.push(customer);
    }

    public Customer take() {
        if (customerStack.isEmpty()) {
            return null; // это "заглушка, чтобы скомилировать"
        }
        return customerStack.pop();
    }
}
