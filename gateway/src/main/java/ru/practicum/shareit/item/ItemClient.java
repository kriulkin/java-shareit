package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> get(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> add(Long userId, ItemDto itemDto) {
        return post("/", userId, itemDto);
    }


    public ResponseEntity<Object> update(Long userId, ItemDto itemDto) {
        return patch("/" + itemDto.getId(), userId, itemDto);
    }

    public ResponseEntity<Object> findByUserId(long userId, int from, int size) {
        return get(
                "?from={from}&size={size}",
                userId,
                Map.of(
                        "from", from,
                        "size", size
                )
        );
    }


    public ResponseEntity<Object> search(Long userId, String term, int from, int size) {
        return get(
                "/search?text={text}&from={from}&size={size}",
                userId,
                Map.of(
                        "text", term,
                        "from", from,
                        "size", size
                )
        );
    }


    public ResponseEntity<Object> addComment(Long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
