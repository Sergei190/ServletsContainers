package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//инициализирует все необходимые объекты
//диспетчеризация запросов

public class MainServlet extends HttpServlet {
    public static final String API_POSTS_D = "/api/posts/\\d+";
    public static final String POSTS = "/api/posts";
    private PostController controller;

    @Override
    public void init() {
        //инициализируем необходимые сущности
        //отдаём список пакетов, в которых необходимо искать аннотационные классы
        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext("ru.netology.servlet");//передаем пакет, в котором необходимо искать бины
        //получаем по классу бина
        controller = configApplicationContext.getBean(PostController.class);
        final PostRepository repository = configApplicationContext.getBean(PostRepository.class);
        final PostService service = configApplicationContext.getBean(PostService.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final String path = req.getRequestURI();
            final String method = req.getMethod();
            final long id = Long.parseLong(path.substring(path.lastIndexOf("/")));

            // primitive routing
            if (method.equals("GET") && path.equals(POSTS)) {
                controller.all(resp);
                return;
            }

            if (method.equals("GET") && path.matches(API_POSTS_D)) {
                // easy way
                controller.getById(id, resp);
                return;
            }
            if (method.equals("POST") && path.equals(POSTS)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.matches(API_POSTS_D)) {
                // easy way
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
