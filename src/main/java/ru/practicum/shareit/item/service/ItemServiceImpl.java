package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectDidntFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemInfoDto getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectDidntFoundException("Item не найдена. itemId = " + itemId));
        final ItemInfoDto itemInfoDto;
         Booking lastBooking = null;
         Booking nextBooking = null;
        // если вещь запрашивает владелец, то добавляем даты последнего и следующего бронирований (иначе - не добавляем)
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemAndStartBeforeOrderByStartDesc(item,
                    LocalDateTime.now()).orElse(null);
            nextBooking = bookingRepository.findFirstByItemAndStartAfterOrderByStart(item,
                    LocalDateTime.now()).orElse(null);
        }
        itemInfoDto = ItemMapper.toItemInfoDto(item, lastBooking, nextBooking);
        addComments(itemInfoDto, item);
        return itemInfoDto;
    }

    @Override
    public List<ItemInfoDto> getAllItemsByOwnerId(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Booking> bookings = bookingRepository.getAllByItem_Owner_Id(userId);

        Map<Item, List<Booking>> bookingsByItems = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getItem));
        for (Item item : items) {
            bookingsByItems.putIfAbsent(item, null);
        }
        return bookingsByItems.entrySet().stream()
                .map(ItemServiceImpl::entryToItemInfoDto)
                .sorted(Comparator.comparing((ItemInfoDto::getId)))
                .collect(toList());
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User owner  = userRepository.findById(userId).orElseThrow(
                () -> new ObjectDidntFoundException("User не найден. userId = " + userId));
        Item item = ItemMapper.toItem(itemDto, owner);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long itemId, ItemDto itemDto, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectDidntFoundException("Item не найдена. itemId = " + itemId));
        if (item.getOwner().getId() != userId) {
            throw new ObjectDidntFoundException("Данная вещь не принадлежит пользователю. " +
                    "Редактировать вещь может только ее владелец. " +
                    "item = " + item/* + " owner = " + owner*/);
        }

        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Текстовый запрос для поиска не должен быть null.");
        }
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> foundItems = itemRepository.search(text);
        return ItemMapper.toItemDtos(foundItems);
    }

    @Override
    public ItemInfoDto.CommentInfoDto createComment(long itemId, CommentDto commentDto, long authorId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectDidntFoundException("Item не найден"));
        User author = userRepository.findById(authorId).orElseThrow(
                () -> new ObjectDidntFoundException("Пользователь не найден"));
        if (!bookingRepository.existsByItemAndBookerAndEndBefore(item, author, LocalDateTime.now())) {
            throw new IllegalArgumentException("У автора нет завершенных бронирований вещи.");
        }
        Comment comment = new Comment(null, commentDto.getText(), item, author, LocalDate.now());
        comment = commentRepository.save(comment);
        return ItemMapper.toCommentInfoDto(comment);
    }

    private static ItemInfoDto entryToItemInfoDto(Map.Entry<Item, List<Booking>> entry) {
        Item item = entry.getKey();
        List<Booking> bookingsOfOneItem = entry.getValue();
        if (bookingsOfOneItem == null) {
            return ItemMapper.toItemInfoDto(item, null, null);
        }
        bookingsOfOneItem.sort(Comparator.comparing(Booking::getStart));
        Booking lastBooking = null;
        Booking nextBooking = null;
        LocalDateTime start;
        for (Booking booking : bookingsOfOneItem) {
            start = booking.getStart();
            if (start.isBefore(LocalDateTime.now())) {
                lastBooking = booking;
            } else {
                nextBooking = booking;
                break;
            }
        }
        return ItemMapper.toItemInfoDto(item, lastBooking, nextBooking);
    }

    private void addComments(ItemInfoDto itemInfoDto, Item item) {
        List<Comment> comments = commentRepository.findAllByItem(item);
        List<ItemInfoDto.CommentInfoDto> commentInfoDtos = comments.stream()
                .map(ItemMapper::toCommentInfoDto)
                .collect(toList());
        itemInfoDto.setComments(commentInfoDtos);
    }

}
