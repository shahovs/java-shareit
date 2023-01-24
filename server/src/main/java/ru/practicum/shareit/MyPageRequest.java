package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class MyPageRequest extends PageRequest {
    private final int from;

    public MyPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    public static PageRequest of(int from, int size, Sort sort) {
        return new MyPageRequest(from, size, sort);
    }

    @Override
    public long getOffset() {
        return from;
    }

}
