select
    r.customer_id as customer_id,
    json_object_agg(rs.row_num, rs.seat_num) as seat
from reservations r
    join reservation_seats rs on r.id = rs.reservation_id
where r.session_id = ?
group by r.customer_id;
