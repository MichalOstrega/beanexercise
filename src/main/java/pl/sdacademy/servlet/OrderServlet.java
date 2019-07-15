package pl.sdacademy.servlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.Incubating;
import pl.sdacademy.dao.ClientDao;
import pl.sdacademy.dao.OrderDao;
import pl.sdacademy.dao.ProductDao;
import pl.sdacademy.model.Client;
import pl.sdacademy.model.Order;
import pl.sdacademy.model.OrderInputModel;
import pl.sdacademy.model.Product;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
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
        String id = req.getParameter("id");
        if (id == null) {
            Collection<Order> orderCollection = orderDao.findAll();
            String orders = objectWriter.writeValueAsString(orderCollection);
            resp.getWriter().println(orders);
        } else {
            try {
                Order order = orderDao.findById(Integer.valueOf(id));
                resp.getWriter().println(objectWriter.writeValueAsString(order));
            } catch (NumberFormatException ex) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        try {
            OrderInputModel orderInputModel = objectMapper.readValue(reader, OrderInputModel.class);
            Client client = clientDao.findById(orderInputModel.getClientId());
            List<Product> collect = orderInputModel.getProductIds().stream().map(productId -> productDao.findById(productId)).collect(Collectors.toList());
            Order newOrder = new Order(client, collect, LocalDate.now(), false);
            orderDao.save(newOrder);
            resp.getWriter().println(objectWriter.writeValueAsString(newOrder));
        } catch (JsonParseException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            JsonNode jsonNode = objectMapper.readTree(req.getReader());
            String id = jsonNode.findValue("id").asText();
            Order orderFromDB = orderDao.findById(Integer.valueOf(id));
            ((ObjectNode) jsonNode).remove("id");
            OrderInputModel orderInputModel = objectMapper.treeToValue(jsonNode, OrderInputModel.class);

            if (orderFromDB != null) {
                if (orderInputModel.getClientId() != null) orderFromDB.setClient(clientDao.findById(orderInputModel.getClientId()));
                orderFromDB.setProducts(orderInputModel.getProductIds().stream().map(productId -> productDao.findById(productId)).collect(Collectors.toList()));
                orderDao.update(orderFromDB);
                resp.getWriter().println(objectWriter.writeValueAsString(orderFromDB));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }  catch (JsonParseException | NumberFormatException exception) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                Order orderFromDB = orderDao.findById(Integer.valueOf(id));
                if (orderFromDB != null) {
                    orderDao.deleteById(orderFromDB.getId());
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException ex) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
