package pl.sdacademy.model;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;

@ApplicationScoped
public class OrderInputModel {


    private Integer clientId;
    private Collection<Integer> productIds;

    public OrderInputModel(Integer clientId, Collection<Integer> productIds) {
        this.clientId = clientId;
        this.productIds = productIds;
    }

    public OrderInputModel() {
    }

    public Integer getClientId() {
        return clientId;
    }

    public Collection<Integer> getProductIds() {
        return productIds;
    }
}
