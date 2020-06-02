package org.step.tinder.servlet;

import org.step.tinder.DAO.DaoMessage;
import org.step.tinder.DAO.DaoUsers;
import org.step.tinder.cookies.Crip;
import org.step.tinder.entity.Message;
import org.step.tinder.entity.TemplateEngine;
import org.step.tinder.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Chat extends HttpServlet {
    private final TemplateEngine engine;
    private final Connection conn;

    public Chat(TemplateEngine engine, Connection conn) {
        this.engine = engine;
        this.conn = conn;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        HashMap<String, Object> data = new HashMap<>();
        HttpSession session = req.getSession();
        String from = (String) session.getAttribute("uname");
        DaoMessage dao = new DaoMessage(conn);
        DaoUsers daoUsers = new DaoUsers(conn);
        String to = req.getParameter("to");
        List<Message> messages = dao.getMessages(from,to);
        data.put("messages", messages);
        data.put("to",to);
        data.put("from",from);
        data.put("image",daoUsers.get(to).map(User::getImage));

        engine.render("chat.ftl", data, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        HashMap<String, Object> data = new HashMap<>();
        DaoMessage dao = new DaoMessage(conn);
        String mes = req.getParameter("mes");
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        dao.put(new Message(from,to,LocalDateTime.now(),mes));
        List<Message> messages = dao.getMessages(from,to);
        data.put("messages", messages);
        data.put("to",to);
        data.put("from",from);

        engine.render("chat.ftl", data, resp);

    }
}
