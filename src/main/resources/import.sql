INSERT INTO users (external_user_id)
VALUES ('qhoekman');
INSERT INTO files (
    user_id,
    name,
    size,
    bucket,
    updated_at
  )
VALUES (
    1,
    'file1',
    100,
    'public',
    '2021-01-01 00:00:00'
  );