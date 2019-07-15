package pl.sdacademy.servlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import pl.sdacademy.dao.ProductDao;
import pl.sdacademy.model.Client;
import pl.sdacademy.model.Product;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;

@WebServlet("/product")
public class ProductServlet extends HttpServlet {

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
            Collection<Product> all = productDao.findAll();
            String result = objectWriter.writeValueAsString(all);
            resp.getWriter().println(result);
        } else {
            try {
                Product product = productDao.findById(Integer.valueOf(id));
                if (product == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    String clientJSON = objectWriter.writeValueAsString(product);
                    resp.getWriter().println(clientJSON);
                }
            } catch (NumberFormatException ex) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        try {
            Product product = objectMapper.readValue(reader, Product.class);
            productDao.save(product);
            resp.getWriter().println(objectWriter.writeValueAsString(product));
        } catch (JsonParseException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id != null) {
            try {
                Product product = productDao.findById(Integer.valueOf(id));
                if (product == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    productDao.deleteById(product.getId());
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            } catch (NumberFormatException ex) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        BufferedReader reader = req.getReader();
        try {
            Product product = objectMapper.readValue(reader, Product.class);
            Product productFromDB = productDao.findById(product.getId());
            if (productFromDB != null) {
               if(product.getName()!=null) productFromDB.setName(product.getName());
                if(product.getPrice()!=null) productFromDB.setPrice(product.getPrice());
                productDao.update(productFromDB);
            }
            resp.getWriter().println(objectWriter.writeValueAsString(productFromDB));
        } catch (JsonParseException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
