select
    rr.row_num as row_num,
    rr.seat_count as seat_count
from rooms r
    join room_rows rr on r.id = rr.room_id
where room_id = ?