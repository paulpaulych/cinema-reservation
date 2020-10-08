insert into rooms(id) values (1);
insert into room_rows(room_id, row_num, seat_count)
values (1, 1, 5), (1, 2, 4);
insert into sessions(id, room_id) values (1, 1);
insert into customers(id) values (1), (2);
insert into reservations(session_id, customer_id) values (1,1);
insert into reservations(session_id, customer_id) values (1,2);
insert into reservation_seats(reservation_id, row_num, seat_num) values (1, 1, 4), (1, 2, 1);
insert into reservation_seats(reservation_id, row_num, seat_num) values (2, 1, 3), (2, 1, 2);