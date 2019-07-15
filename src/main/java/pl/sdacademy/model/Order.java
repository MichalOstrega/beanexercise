package pl.sdacademy.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Client client;
    @ManyToMany
    private Collection<Product> products;
    private LocalDate date;
    private boolean paymentComplete;

    public Order(Client client, Collection<Product> products, LocalDate date, Boolean paymentComplete) {
        this.client = client;
        this.products = products;
        this.date = date;
        this.paymentComplete = paymentComplete;
    }

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Collection<Product> getProducts() {
        return products;
    }

    public LocalDate getDate() {
        return date;
    }

    public Boolean getPaymentComplete() {
        return paymentComplete;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setProducts(Collection<Product> products) {
        this.products = products;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPaymentComplete(boolean paymentComplete) {
        this.paymentComplete = paymentComplete;
    }
}
