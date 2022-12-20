package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static List<ItemDto> ToItemDtos(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public static ItemInfoDto toItemInfoDto(Item item, Booking lastBooking, Booking nextBooking) {
        ItemInfoDto itemInfoDto = new ItemInfoDto();
        itemInfoDto.setId(item.getId());
        itemInfoDto.setName(item.getName());
        itemInfoDto.setDescription(item.getDescription());
        itemInfoDto.setAvailable(item.getAvailable());

        ItemInfoDto.BookingInfoDto lastBookingInfoDto = toBookingInfoDto(lastBooking);
        ItemInfoDto.BookingInfoDto nextBookingInfoDto = toBookingInfoDto(nextBooking);

        itemInfoDto.setLastBooking(lastBookingInfoDto);
        itemInfoDto.setNextBooking(nextBookingInfoDto);

        return itemInfoDto;
    }

    private static ItemInfoDto.BookingInfoDto toBookingInfoDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        ItemInfoDto.BookingInfoDto bookingInfoDto = new ItemInfoDto.BookingInfoDto();
        bookingInfoDto.setId(booking.getId());
        bookingInfoDto.setStart(booking.getStart());
        bookingInfoDto.setEnd(booking.getEnd());
        bookingInfoDto.setBookerId(booking.getBooker().getId());
        return bookingInfoDto;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(comment.getItem());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

}
