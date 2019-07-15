package pl.sdacademy.dao;

import pl.sdacademy.model.Client;
import pl.sdacademy.model.Product;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientDao extends EntityDao<Client> {
    public ClientDao() {
        super(Client.class);
    }
}
