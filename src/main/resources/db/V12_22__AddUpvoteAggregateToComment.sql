ALTER TABLE csld_comment
    ADD COLUMN amount_of_upvotes INT;

update csld_comment
set amount_of_upvotes = (Select count(*) from csld_comment_upvote where comment_id = csld_comment.id);