package ru.netology.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@FunctionalInterface
public interface Handler {
    void handle(HttpServletRequest  req, HttpServletResponse res) throws IOException;
}
