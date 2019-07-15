package pl.sdacademy.servlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hibernate.Incubating;
import pl.sdacademy.dao.ClientDao;
import pl.sdacademy.model.Client;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

@WebServlet("/client")
public class ClientServlet extends HttpServlet {

    @Inject
    private ClientDao clientDao;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ObjectWriter objectWriter;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null) {
            Collection<Client> all = clientDao.findAll();
            String result = objectWriter.writeValueAsString(all);
            resp.getWriter().println(result);
        } else {
            try {
                Client client = clientDao.findById(Integer.valueOf(id));
                if (client == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    String clientJSON = objectWriter.writeValueAsString(client);
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
            Client client = objectMapper.readValue(reader, Client.class);
            clientDao.save(client);
            resp.getWriter().println(objectWriter.writeValueAsString(client));
        } catch (JsonParseException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id != null) {
            try {
                Client clientFromReq = clientDao.findById(Integer.valueOf(id));
                if (clientFromReq == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    clientDao.deleteById(clientFromReq.getId());
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
            Client clientFromReq = objectMapper.readValue(reader, Client.class);
            Client clientFromDB = clientDao.findById(clientFromReq.getId());
            if (clientFromDB != null) {
                if(clientFromReq.getLastName()!= null) clientFromDB.setLastName(clientFromReq.getLastName());
                if(clientFromReq.getFirstName()!= null) clientFromDB.setFirstName(clientFromReq.getFirstName());
                if(clientFromReq.getEmail()!= null) clientFromDB.setEmail(clientFromReq.getEmail());
                if(clientFromReq.getPassword()!= null) clientFromDB.setPassword(clientFromReq.getPassword());
                clientDao.update(clientFromDB);
            }
            resp.getWriter().println(objectWriter.writeValueAsString(clientFromDB));
        } catch (JsonParseException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }


    }
}
