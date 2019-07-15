package pl.sdacademy.filter;

import pl.sdacademy.bean.ClientBeanSession;
import pl.sdacademy.dao.ClientDao;
import pl.sdacademy.model.Client;
import pl.sdacademy.model.Product;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@WebFilter("/*")
public class AuthenticationFilter extends HttpFilter {

    @Inject
    private ClientDao clientDao;

    @Inject
    private ClientBeanSession clientBeanSession;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String authorization = req.getHeader("Authorization");
        if (authorization != null) {
            Optional<Client> result = getClient(authorization);
            if (result.isPresent()) {
                clientBeanSession.setClient(result.get());
                clientBeanSession.setBasket(new ArrayList<>());
                chain.doFilter(req, res);
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            Optional<Client> result = getClient(req.getParameter("login") + ":" + req.getParameter("password"));
            if (result.isPresent()) {
                clientBeanSession.setClient(result.get());
                clientBeanSession.setBasket(new ArrayList<>());
                chain.doFilter(req, res);
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    private Optional<Client> getClient(String authorization) {
        String[] split = authorization.split(":");
        String email = split[0];
        String password = split[1];
        Collection<Client> clients = clientDao.findAll();
        return clients.stream().filter(client -> client.getEmail().equals(email) && client.getPassword().equals(password)).findFirst();
    }


}
