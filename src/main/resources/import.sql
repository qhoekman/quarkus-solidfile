INSERT INTO users (external_user_id)
VALUES ('qhoekman');
INSERT INTO folders (user_id, name)
VALUES (1, 'Root');
INSERT INTO files (
    user_id,
    folder_id,
    object_storage_ref,
    name,
    size,
    created_at
  )
VALUES (
    1,
    1,
    'file1',
    'file1',
    100,
    '2021-01-01 00:00:00'
  );