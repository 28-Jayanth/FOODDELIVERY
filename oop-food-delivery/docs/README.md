Object-oriented Programming
============

In this exercise, the task is to create the implementation for a food delivery service.

Concepts from SOLID such as Dependency Inversion can be seen when looking at the data Reader classes. 

An abstract Reader defines common methods and implementations, while creating an abstract method that will be
implemented by its child classes.
The FileDataStore is communicating with the DefaultFoodDeliveryService through a common DataStore interface, thus the
initialization and storage mechanics can stay hidden to the Service implementation.

The View and FoodDeliveryService interfaces define clear boundaries between UI & backend implementations.

Description
===========
The food delivery application works in the following way (the happy path):
1. Application data initialization: read food & customer from csv files.
2. Authenticate the customer.
3. List all the foods.
4. The user selects a food and specifies the amount. If a selected food is already in the cart,
then selecting that food now and specifying its amount overwrites the old amount in the cart. 
If the new amount is 0, then the food is removed from the cart.
5. The user is prompted to add a new food:
   - a. If the user continues to shop, go back to #4.
   - b. If not, create the order.
6. Print the order details and thank the user for his/her purchase.
7. Write order details into orders.csv

There are several "unhappy path" cases that have to be taken into account:

* For functionality (1), authentication.
    * If the user enters incorrect credentials, the application writes a message to the console. There is no need to request credentials again, the application quits.
* For functionality (4), updating cart.
    * If the price of the food and pieces along with the already present cart items exceed the customer's balance, don't add the food to the cart
* In all other error cases the application can stop running.

There are two main components that communicate through the App class to implement this functionality.
A UI (View) and backend (Service) that communicate to each other through well-defined interfaces.

The View is responsible for reading user input and showing information to the user. The service will be responsible for handling the business logic.

Data Model
==========

This exercise reads food  and customer details from csv files.

Format of foods.csv
-------------------

| Name   | Calories | Description | Price (EUR)|
|---|---|---|---|
| Fideua | 558 | Noodles gone wild in a seafood fiesta | 15 |

Format of customers.csv
-----------------------

| Username | Password | Customer Id | Customer name | Account balance |
|---|---|---|---|---|
| Smith | SmithSecret | 2 | Josh Smith | 100 |

Expected result
---------------

    Enter customer name:Smith
    Enter customer password:SmithSecret
    Welcome, Josh Smith. Your balance is: 100 EUR.

    Foods offered today:
    - Fideua 15 EUR each
    - Paella 13 EUR each
    - Tortilla 10 EUR each
    - Gazpacho 8 EUR each
    - Quesadilla 13 EUR each

    Please enter the name and amount of food (separated by comma) you would like to buy:Tortilla,1
    Added 1 piece(s) of Tortilla to the cart.

    Please enter the name and amount of food (separated by comma) you would like to buy:
    Order (items: [Tortilla], price: 10 EUR, timestamp: 13-07-2023 12:43:36) has been confirmed.
    Your balance is 90 EUR.
    Thank you for your purchase.

Example for low balance handling:

    Please enter the name and amount of food (separated by comma) you would like to buy:Paella,10
    
    Unable to add current order for Food{name='Paella', calorie=379, description='Rice party with a saffron twist', price=13}, as with current cart content it would exceed available balance!

Application Classes
===================

Domain Model
------------

![](https://raw.githubusercontent.com/epam-java-cre/exercise-specification-images/main/oop-food/domain.png)

 
| Class       | Description                                                                                                                                                                                                                                                        |
|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Credentials | Object used to hold login data and retrieve the corresponding Customer object during authentication.                                                                                                                                                               |
| Customer    | The object representing the user after login. This stores all the information regarding the customer.                                                                                                                                                       |
| Cart        | The current shopping cart of the customer in the application.                                                                                                                                                                                                      |
| Order       | Object holding all the information of the order of a customer. id of the order is **Long** wrapper class, **not long** primitive; reason: the Order id is null until DataStore generates a new id for it.                                                          |
| OrderItem   | A subunit of both Orders and Carts. An OrderItem holds a Food and the number of pieces of food being ordered.                                                                                                                                                      |
| Food        | An object representing the food that can be purchased in the application.                                                                                                                                                                                          |

Value objects
---
| Class | Description |
|---|---|
| FoodSelection | Business logic related object used to hold relevant data together to be passed between the View and Service. |

Service, View and Data
======================

![](https://raw.githubusercontent.com/epam-java-cre/exercise-specification-images/main/oop-food/service.png)

![](https://raw.githubusercontent.com/epam-java-cre/exercise-specification-images/main/oop-food/exceptions.png)

![](https://raw.githubusercontent.com/epam-java-cre/exercise-specification-images/main/oop-food/view.png)

 
| Class                                           | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|-------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| App                                             | Entry point of the application, this class should control the application flow (e.g. do listing food, adding foods to cart, ordering). This class should use Service and View classes to implement functionality.                                                                                                                                                                                                                                                                                                                                                                                      |
|                                                 | **Exception handling:** Handles LowBalanceException, asks for choosing from foods again. Catches AuthenticationException, but makes the program exit.                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| FoodDeliveryService  DefaultFoodDeliveryService | **FoodDeliveryService**: service interface.  **DefaultFoodDeliveryService**: implementation of FoodDeliveryService. Needs a DataStore argument to be passed in its constructor. Contains logic: getting the list of foods, checking customer balance, etc.                                                                                                                                                                                                                                                                                                                                             |
|                                                 | **authenticate():** Should throw AuthenticationException if given incorrect credentials.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                                                 | **updateCart():** See step 4 in the Description for a an overview. **Price calculation:** The price of the new or modified OrderItem and the new price of the Cart should be calculated. Currently, the price of an OrderItem is the price of the food multiplied by the amount of the food ordered. The total price of a Cart is the sum of the prices of its OrderItems. **Throws:** LowBalanceException if the customer's balance will be lower than the cart's new value (cart contents + current order price). IllegalArgumentException if pieces is 0 and the given food is not in the cart yet. |
|                                                 | **createOrder():** An Order is created by converting the contents of the cart into an order. Then the cart is emptied. **Price calculation:** The price of the Order and the new price of the Cart should be calculated. The total price of an Order is the sum of the prices of its OrderItems. **Throws:** IllegalStateException if the cart is empty.                                                                                                                                                                                                                                               |
| LowBalanceException  AuthenticationException    | Basic unchecked exceptions for domain specific issues.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| View  CLIView                                   | **View:** interface for user interaction, both input and output.  **CLIView:** View implementation that deals with the keyboard and console.  The date/time format used by this class is: **"dd/MM/yyyy HH:mm"**                                                                                                                                                                                                                                                                                                                                                                                       |
| DataStore  FileDataStore                        | **DataStore:** interface that provides methods to access data.  **FileDataStore:** Implementation of the DataStore interface that is responsible to load data from the csv files. Constructor needs the folder name in which the files are loaded from. CSV file names are hard-coded: **customers.csv**, **orders.csv** and **foods.csv**.                                                                                                                                                                                                                                                            |
|                                                 | **init():** Loading data from the csv files should be implemented in this method. It should be called only once from the **App** class.                                                                                                                                                                                                                                                                                                                                                                                                                                                                |                                                 
|                                                 | **createOrder():** creates a new order. Order id generation: take the max value of the existing order ids and increment by one. The new order must be added to the owner customer's orders collection, and the order collection in DataStore. Insert it at the end of the collections. Store the new order in memory.                                                                                                                                                                                                                                                                                  |

**Notes**:

*   The classes must have the fields and methods as defined in the class diagrams. The UML diagrams only show what is the specification of the homework. You may implement **additional methods (or even classes!)** as you see fit.

Constraints
===========

Restrictions and other criteria:

*   Using services in View class(es) is not allowed.
*   Using View class(es) in services is not allowed.
*   Domain model classes should not contain complex logic.
    *   Creation: you can add constructors, other methods that helps to create the objects, and set up their relations with each other.
    *   Update data: you can add methods to query or update data. E.g. the Cart can have methods to manage its order items.

Output
======

Format of orders.csv
--------------------

| Order Id | Customer Id | OrderItem name | OrderItem pieces | OrderItem price | Timestamp   (dd/MM/yyyy HH:mm) | Total price of order |
|---|---|---|---|---|---|---|
| 1 | 20 | Tortilla | 2 | 20 | 28/06/2019 23:09 | 36 |
| 1 | 20 | Gazpacho | 2 | 16 | 28/06/2019 23:09 | 36 |

orders.csv is structured in a way that a single order can be composed of multiple rows of (one for each Order Item).

Technology
==========

*   Use Java version 17

Acceptance
==========

*   Functionality is implemented as defined in the relevant chapter.
*   Object-Oriented principles are followed.
