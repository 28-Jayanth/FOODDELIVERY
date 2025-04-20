package com.epam.training.food.service;

import com.epam.training.food.data.FileDataStore;
import com.epam.training.food.domain.Credentials;
import com.epam.training.food.domain.Customer;
import com.epam.training.food.domain.Food;
import com.epam.training.food.domain.Order;

import java.util.List;

public class DefaultFoodDeliveryService implements FoodDeliveryService {
    public DefaultFoodDeliveryService(FileDataStore fileDataStore) {
    }

    @Override
    public Customer authenticate(Credentials credentials) throws AuthenticationException {
        return null;
    }

    @Override
    public List<Food> listAllFood() {
        return null;
    }

    @Override
    public void updateCart(Customer customer, Food food, int pieces) throws LowBalanceException {

    }

    @Override
    public Order createOrder(Customer customer) throws IllegalStateException {
        return null;
    }
}
