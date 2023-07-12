-- Sample Local Developer Data
INSERT INTO comment (comment_id, contents, created_by, updated_by)
VALUES
  ('SRCLTDE9WJ871KKS', 'This is sampel comment', 'sample_tester_01', 'sample_tester_01')
, ('8KTXH9SDFHZHUDL9', 'Comments to be deleted.', 'sample_tester_01', 'sample_tester_01')
;

-- [S] UPDATE Transaction
INSERT INTO comment_audit (comment_key, comment_id, contents, view_count, deleted, updated_at, updated_by)
SELECT comment_key, comment_id, contents, view_count, deleted, updated_at, updated_by
FROM comment
WHERE comment_id = 'SRCLTDE9WJ871KKS'
;

UPDATE comment
SET contents   = 'This is sample comment'
  , updated_at = CURRENT_TIMESTAMP
  , updated_by = 'content_correct_bot'
WHERE comment_id = 'SRCLTDE9WJ871KKS'
;
-- [E] UPDATE Transaction

-- [S] DELETE Transaction
INSERT INTO comment_audit (comment_key, comment_id, contents, view_count, deleted, updated_at, updated_by)
SELECT comment_key, comment_id, contents, view_count, deleted, updated_at, updated_by
FROM comment
WHERE comment_id = '8KTXH9SDFHZHUDL9'
;

UPDATE comment
SET deleted    = true
  , updated_at = CURRENT_TIMESTAMP
  , updated_by = 'content_delete_bot'
WHERE comment_id = '8KTXH9SDFHZHUDL9'
;
-- [E] DELETE Transaction
