TRUNCATE TABLE tbl_user CASCADE;

select poem_id, count(*) as total from annotation
    group by poem_id order by total desc;

SELECT p.id, p.title
FROM poem p
WHERE p.complexity IS NOT NULL
  AND p.school_grade IS NOT NULL
  AND EXISTS (
    SELECT 1
    FROM annotation a
    WHERE a.poem_id = p.id
)
  AND EXISTS (
    SELECT 1
    FROM poem_topic pt
    WHERE pt.poem_id = p.id
);

