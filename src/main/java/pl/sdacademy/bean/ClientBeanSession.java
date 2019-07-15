package pl.sdacademy.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pl.sdacademy.model.Client;
import pl.sdacademy.model.Product;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Collection;

@SessionScoped
public class ClientBeanSession implements Serializable {

    private Client client;

    private Collection<Product> basket;

    public ClientBeanSession(Client client, Collection<Product> basket) {
        this.client = client;
        this.basket = basket;
    }




    public ClientBeanSession() {
    }

    public Collection<Product> getBasket() {
        return basket;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setBasket(Collection<Product> basket) {
        this.basket = basket;
    }
}
