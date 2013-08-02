CREATE TRIGGER csld_only_one_rating_trg
  BEFORE INSERT
  ON rating
  FOR EACH ROW
  EXECUTE PROCEDURE csld_only_one_rating();