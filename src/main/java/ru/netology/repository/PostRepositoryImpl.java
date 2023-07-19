package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepositoryImpl implements PostRepository {
    private final int NEW_POST = 0;

    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post save(Post post) throws NotFoundException {
        if (post.getId() == NEW_POST) {
            post.setId(idCounter.incrementAndGet());
        } else if (!posts.containsKey(post.getId()))
            throw new NotFoundException("Post with id %d not found".formatted(post.getId()));

        posts.put(post.getId(), post);
        return post;
    }

    public void removeById(long id) throws NotFoundException {
        if (!posts.containsKey(id))
            throw new NotFoundException("Post with id %d not found".formatted(id));
        posts.remove(id);
    }
}
