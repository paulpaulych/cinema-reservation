--кинозал
create table rooms(
    id bigint primary key
);

--ряд в кинозале
create table room_rows(
    -- ссылка на кинозал
    room_id bigint not null references rooms on delete cascade,

    -- номер ряда
    row_num integer not null check ( row_num > 0),

    -- количество мест в ряду
    seat_count integer not null check ( seat_count > 0 )
);
create unique index room_rows_uniq on room_rows(room_id, row_num);

--киносеанс
create table sessions(
    id bigint primary key,
    room_id bigint not null references rooms on delete cascade
);

--бронирование
create table reservations(
    id bigint primary key generated always as identity,
    session_id bigint not null references sessions on delete cascade,
    customer_id bigint not null
);
create index reserv_session_id_idx on reservations(session_id);

--забронированное место
create table reservation_seats(
    reservation_id bigint not null references reservations,

    -- номер ряда
    row_num integer not null check ( row_num > 0 ),

    -- номер места
    seat_num integer not null check ( seat_num > 0)
);
create unique index reservation_seats_uniq on reservation_seats(reservation_id, row_num, seat_num);
