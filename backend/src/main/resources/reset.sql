TRUNCATE TABLE tbl_user CASCADE;

select poem_id, count(*) as total from annotation
    group by poem_id order by total desc;