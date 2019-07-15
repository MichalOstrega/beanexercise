package pl.sdacademy.dao;

import pl.sdacademy.model.Order;
import pl.sdacademy.model.Product;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderDao extends EntityDao<Order> {
    public OrderDao() {
        super(Order.class);
    }
}
