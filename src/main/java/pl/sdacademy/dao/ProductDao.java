package pl.sdacademy.dao;

import pl.sdacademy.model.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;

@ApplicationScoped
public class ProductDao extends EntityDao<Product> {
    public ProductDao() {
        super(Product.class);
    }
}
