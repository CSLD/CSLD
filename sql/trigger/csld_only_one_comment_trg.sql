CREATE TRIGGER csld_only_one_comment_trg
  BEFORE INSERT
  ON comment
  FOR EACH ROW
  EXECUTE PROCEDURE csld_only_one_comment();