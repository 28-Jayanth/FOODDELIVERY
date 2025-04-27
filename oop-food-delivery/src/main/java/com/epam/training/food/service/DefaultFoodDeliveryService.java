package com.epam.training.food.service;

import com.epam.training.food.data.FileDataStore;
import com.epam.training.food.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DefaultFoodDeliveryService implements FoodDeliveryService {

    private final FileDataStore fileDataStore;

    public DefaultFoodDeliveryService(FileDataStore fileDataStore) {
        this.fileDataStore = fileDataStore;
    }

    @Override
    public Customer authenticate(Credentials credentials) throws AuthenticationException {
        Optional<Customer> customerOpt = fileDataStore.getCustomers()
                .stream()
                .filter(c -> c.getUserName().equals(credentials.getUserName())
                        && c.getPassword().equals(credentials.getPassword()))
                .findFirst();

        if (customerOpt.isEmpty()) {
            throw new AuthenticationException("Invalid username or password");
        }

        return customerOpt.get();
    }

    @Override
    public List<Food> listAllFood() {
        return fileDataStore.getFoods();
    }

    @Override
    public void updateCart(Customer customer, Food food, int pieces) throws LowBalanceException {
        if (pieces < 0) {
            throw new IllegalArgumentException("Pieces cannot be negative.");
        }

        Cart cart = customer.getCart();
        List<OrderItem> orderItems = cart.getOrderItems();
        OrderItem existingItem = orderItems.stream()
                .filter(item -> item.getFood().equals(food))
                .findFirst()
                .orElse(null);

        if (existingItem == null) {
            if (pieces == 0) {
                throw new IllegalArgumentException("Cannot add 0 pieces of a food not already in the cart.");
            }
            BigDecimal newPrice = cart.getPrice().add(food.getPrice().multiply(BigDecimal.valueOf(pieces)));
            if (newPrice.compareTo(customer.getBalance()) > 0) {
                throw new LowBalanceException("Balance is too low to add this food.");
            }
            OrderItem newItem = new OrderItem(food, pieces, food.getPrice().multiply(BigDecimal.valueOf(pieces)));
            orderItems.add(newItem);
        } else {
            if (pieces == 0) {
                orderItems.remove(existingItem);
            } else {
                existingItem.setPieces(pieces);
                existingItem.setPrice(food.getPrice().multiply(BigDecimal.valueOf(pieces)));
            }

            BigDecimal totalPrice = orderItems.stream()
                    .map(OrderItem::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalPrice.compareTo(customer.getBalance()) > 0) {
                throw new LowBalanceException("Balance is too low after updating the cart.");
            }
        }

        BigDecimal newCartPrice = orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setPrice(newCartPrice);
    }

    @Override
    public Order createOrder(Customer customer) throws IllegalStateException {
        Cart cart = customer.getCart();
        if (cart.getOrderItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from an empty cart.");
        }

        Order order = new Order(customer);

        // Assign orderId
        Long nextOrderId = fileDataStore.getOrders()
                .stream()
                .map(Order::getOrderId)
                .filter(id -> id != null)
                .max(Long::compare)
                .map(id -> id + 1)
                .orElse(0L);

        order.setOrderId(nextOrderId);

        // Add order to customer and data store
        customer.getOrders().add(order);
        fileDataStore.createOrder(order);

        // Deduct balance
        customer.setBalance(customer.getBalance().subtract(cart.getPrice()));

        // Empty the cart
        cart.getOrderItems().clear();
        cart.setPrice(BigDecimal.ZERO);

        return order;
    }
}
