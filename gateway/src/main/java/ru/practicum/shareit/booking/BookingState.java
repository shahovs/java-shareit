package ru.practicum.shareit.booking;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED, ILLEGAL_STATE

//    ;

//    public static Optional<BookingState> from(String stringState) {
//        for (BookingState state : values()) {
//            if (state.name().equalsIgnoreCase(stringState)) {
//                return Optional.of(state);
//            }
//        }
//        return Optional.empty();
//    }

}
