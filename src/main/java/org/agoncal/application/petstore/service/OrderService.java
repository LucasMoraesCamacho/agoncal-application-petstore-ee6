package org.agoncal.application.petstore.service;

import org.agoncal.application.petstore.domain.*;
import org.agoncal.application.petstore.exception.ValidationException;
import org.agoncal.application.petstore.util.Loggable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */

@Stateless
@Loggable
public class OrderService implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    @Inject
    private EntityManager em;

    // ======================================
    // =              Public Methods        =
    // ======================================

    public Order createOrder(final Customer customer, final CreditCard creditCard, final List<CartItem> cartItems) {

        // OMake sure the object is valid
        if (cartItems == null || cartItems.size() == 0)
            throw new ValidationException("Shopping cart is empty"); // TODO exception bean validation

        // Creating the order
        Order order = new Order(em.merge(customer), creditCard, customer.getHomeAddress());

        // From the shopping cart we create the order lines
        List<OrderLine> orderLines = new ArrayList<OrderLine>();

        for (CartItem cartItem : cartItems) {
            orderLines.add(new OrderLine(cartItem.getQuantity(), em.merge(cartItem.getItem())));
        }
        order.setOrderLines(orderLines);

        // Persists the object to the database
        em.persist(order);

        return order;
    }

    public Order findOrder(@NonNull Long orderId) {
        return em.find(Order.class, orderId);
    }

    public List<Order> findAllOrders() {
        TypedQuery<Order> typedQuery = em.createNamedQuery(Order.FIND_ALL, Order.class);
        return typedQuery.getResultList();
    }

    public void removeOrder(@NonNull Order order) {

        em.remove(em.merge(order));
    }
}