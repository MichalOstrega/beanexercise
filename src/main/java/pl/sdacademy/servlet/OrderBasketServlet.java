package pl.sdacademy.servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import pl.sdacademy.bean.ClientBeanSession;
import pl.sdacademy.dao.ClientDao;
import pl.sdacademy.dao.OrderDao;
import pl.sdacademy.dao.ProductDao;
import pl.sdacademy.model.Order;
import pl.sdacademy.model.Product;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/orderBasket")
public class OrderBasketServlet extends HttpServlet {
    @Inject
    private ClientBeanSession clientBeanSession;

    @Inject
    private OrderDao orderDao;

    @Inject
    private ClientDao clientDao;

    @Inject
    private ProductDao productDao;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ObjectWriter objectWriter;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        writeClientBeanSessionToResp(resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        else {
            try {
                Product product = productDao.findById(Integer.valueOf(id));
                if (product != null) {
                    clientBeanSession.getBasket().add(product);
                    writeClientBeanSessionToResp(resp);
                }
                else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException ex) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        else {
            try {
                Product product = productDao.findById(Integer.valueOf(id));
                if (product != null) {
                    clientBeanSession.getBasket().remove(product);
                    writeClientBeanSessionToResp(resp);
                }
                else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException ex) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Order order = new Order(clientBeanSession.getClient(), clientBeanSession.getBasket(), LocalDate.now(), false);
        orderDao.save(order);
        clientBeanSession.getBasket().clear();
        writeClientBeanSessionToResp(resp);
    }

    private void writeClientBeanSessionToResp(HttpServletResponse resp) throws IOException {
        System.out.println("");
        resp.getWriter().println(objectWriter.writeValueAsString(clientBeanSession));
    }
}
