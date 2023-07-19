package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.exception.UnsupportedMethodException;
import ru.netology.handler.HandlerKeyPair;
import ru.netology.repository.PostRepository;
import ru.netology.repository.PostRepositoryImpl;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private Map< String, List<HandlerKeyPair>> routerMap;

    @Override
    public void init() {
        final PostRepository repository = new PostRepositoryImpl();
        final var service = new PostService(repository);
        controller = new PostController(service);

        initRouterMap();
    }

    public void initRouterMap() {
        routerMap = new HashMap<>();
        routerMap.put("GET", new ArrayList<>(
                List.of(
                        new HandlerKeyPair(
                                "/api/posts",
                                (req, res) -> controller.all(res)
                        ),
                        new HandlerKeyPair(
                                "/api/posts/\\d+",
                                (req, res) -> {
                                    final var path = req.getRequestURI();
                                    final var id = parseIdFromPath(path);
                                    controller.getById(id, res);
                                }
                        )
                )
        ));
        routerMap.put("POST", new ArrayList<>(
                List.of(
                        new HandlerKeyPair(
                                "/api/posts",
                                (req, res) -> controller.save(req.getReader(), res)
                        )
                )
        ));
        routerMap.put("DELETE", new ArrayList<>(
                List.of(
                        new HandlerKeyPair(
                                "/api/posts/\\d+",
                                (req, res) -> {
                                    final var path = req.getRequestURI();
                                    final var id = parseIdFromPath(path);
                                    controller.removeById(id, res);
                                }
                        )
                )
        ));
    }

    private long parseIdFromPath(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            final var handlerKeyPairs = routerMap.get(method);

            handlerKeyPairs.stream().
                    filter(pair -> path.matches(pair.getFirst())).
                    findFirst()
                    .orElseThrow(UnsupportedMethodException::new)
                    .getSecond()
                    .handle(req, res);

        } catch (UnsupportedMethodException | NotFoundException e) {
            res.setContentType(PostController.APPLICATION_JSON);
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().print("{ \"errorMessage\" : \"%s\" }".formatted(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
