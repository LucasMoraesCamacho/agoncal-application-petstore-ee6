package org.agoncal.application.petstore.service;

import org.agoncal.application.petstore.domain.Customer;
import org.agoncal.application.petstore.exception.ValidationException;
import org.agoncal.application.petstore.util.Loggable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import lombok.NonNull;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */

@Stateless
@Loggable
public class CustomerService implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    @Inject
    private EntityManager em;

    // ======================================
    // =              Public Methods        =
    // ======================================

    public boolean doesLoginAlreadyExist(final String login) {

        if (login == null)
            throw new ValidationException("Login cannot be null");

        // Login has to be unique
        TypedQuery<Customer> typedQuery = em.createNamedQuery(Customer.FIND_BY_LOGIN, Customer.class);
        typedQuery.setParameter("login", login);
        try {
            typedQuery.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public Customer createCustomer(@NonNull final Customer customer) {
        em.persist(customer);

        return customer;
    }

    public Customer findCustomer(@NonNull final String login) {

        TypedQuery<Customer> typedQuery = em.createNamedQuery(Customer.FIND_BY_LOGIN, Customer.class);
        typedQuery.setParameter("login", login);

        try {
            return typedQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Customer findCustomer(final String login, final String password) {

        if (login == null)
            throw new ValidationException("Invalid login");
        if (password == null)
            throw new ValidationException("Invalid password");

        TypedQuery<Customer> typedQuery = em.createNamedQuery(Customer.FIND_BY_LOGIN_PASSWORD, Customer.class);
        typedQuery.setParameter("login", login);
        typedQuery.setParameter("password", password);

        return typedQuery.getSingleResult();
    }

    public List<Customer> findAllCustomers() {
        TypedQuery<Customer> typedQuery = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
        return typedQuery.getResultList();
    }

    public Customer updateCustomer(@NonNull final Customer customer) {

        // Update the object in the database
        em.merge(customer);

        return customer;
    }

    public void removeCustomer(@NonNull final Customer customer) {

        em.remove(em.merge(customer));
    }
}