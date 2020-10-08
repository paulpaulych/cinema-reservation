--кинозал
create table rooms(
    id bigint primary key
);

--ряд в кинозале
create table room_rows(
    -- ссылка на кинозал
    room_id bigint references rooms on delete cascade,

    row_num integer check ( row_num > 0),
    -- количество мест в ряду
    seat_count integer check ( seat_count > 0 )
);
create unique index room_rows_uniq on room_rows(room_id, row_num);

--клиент кинотеатра
create table customers(
    id bigint primary key
);

--киносеанс
create table sessions(
    id bigint primary key,
    room_id bigint references rooms
);

--бронирование
create table reservations(
    id bigint primary key generated always as identity,
    session_id bigint references sessions,
    customer_id bigint references customers
);

--забронированный места
create table reservation_seats(
    reservation_id bigint references reservations,

    -- номер ряда
    row_num integer check ( row_num > 0 ),

    -- номер места
    seat_num integer check ( seat_num > 0)
);
create unique index reservation_seats_uniq on reservation_seats(reservation_id, row_num, seat_num);
