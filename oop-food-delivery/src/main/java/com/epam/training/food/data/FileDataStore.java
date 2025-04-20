package com.epam.training.food.data;

import com.epam.training.food.data.DataStore;
import com.epam.training.food.domain.Customer;
import com.epam.training.food.domain.Food;
import com.epam.training.food.domain.Order;

import java.util.List;

public class FileDataStore implements DataStore {
    public FileDataStore(String inputFolderPath) {
    }

    public void init() {
    }

    @Override
    public List<Customer> getCustomers() {
        return null;
    }

    @Override
    public List<Food> getFoods() {
        return null;
    }

    @Override
    public List<Order> getOrders() {
        return null;
    }

    @Override
    public Order createOrder(Order order) {
        return null;
    }

    @Override
    public void writeOrders() {

    }
}
