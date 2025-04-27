package com.epam.training.food.data;

import com.epam.training.food.domain.Customer;
import com.epam.training.food.domain.Food;
import com.epam.training.food.domain.Order;

import java.util.ArrayList;
import java.util.List;

public class FileDataStore implements DataStore {

    private final String inputFolderPath;

    private List<Customer> customers;
    private List<Food> foods;
    private List<Order> orders;
    private final OrderWriter orderWriter = new OrderWriter();

    public FileDataStore(String inputFolderPath) {
        this.inputFolderPath = inputFolderPath;
        this.orders = new ArrayList<>();
    }

    public void init() {
        var customerReader = new CustomerReader();
        var foodReader = new FoodReader();

        this.customers = customerReader.read(inputFolderPath + "/customers.csv");
        this.foods = foodReader.read(inputFolderPath + "/foods.csv");
    }

    @Override
    public List<Customer> getCustomers() {
        return customers;
    }

    @Override
    public List<Food> getFoods() {
        return foods;
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public Order createOrder(Order order) {
        orders.add(order);
        return order;
    }

    @Override
    public void writeOrders() {
        orderWriter.writeOrders(orders, inputFolderPath + "/orders.csv");
    }
}
