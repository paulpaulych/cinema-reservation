select
    s.id as id,
    json_object_agg(rr.row_num, rr.seat_count) as rows
from sessions s
    join rooms r on s.room_id = r.id
    join room_rows rr on r.id = rr.room_id
where s.id = ?
group by s.id
