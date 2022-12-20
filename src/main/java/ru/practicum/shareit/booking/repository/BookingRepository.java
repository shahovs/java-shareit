package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getAllByItem_Owner_Id(long ownerId);

    boolean existsByItemAndBookerAndEndBefore(Item item, User booker, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemAndStartBeforeOrderByStartDesc(Item item, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemAndStartAfterOrderByStart(Item item, LocalDateTime localDateTime);


    //    getAllBookingsByBookerId (5 методов)
    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    @Query("" +
            "select b " +
            "from Booking b " +
            "where b.booker = :booker " +
            "and :now between b.start and b.end " +
            "order by b.start desc ")
    List<Booking> findAllByBookerAndCurrentOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);


    //    getAllBookingsByOwnerId (5 методов)
    List<Booking> findAllByItem_OwnerOrderByStartDesc(User owner);

    @Query(value = "" +
            "select * " +
            "from bookings b " +
            "join items i on b.item_id = i.id " +
            "where i.owner_id = :ownerId " +
            "and :now between b.start_date and b.end_date " +
            "order by b.start_date desc ",
            nativeQuery = true)
    List<Booking> findAllByItem_OwnerAndCurrentOrderByStartDesc(long ownerId, LocalDateTime now);

    List<Booking> findAllByItem_OwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now);

    List<Booking> findAllByItem_OwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime now);

    List<Booking> findAllByItem_OwnerAndStatusOrderByStartDesc(User owner, BookingStatus status);

}
