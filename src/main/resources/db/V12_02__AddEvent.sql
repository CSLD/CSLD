CREATE TABLE event (
  id              SERIAL PRIMARY KEY,
  name            TEXT,
  description     TEXT,
  loc             TEXT,
  source          TEXT,
  amountOfPlayers VARCHAR(20),
  "from"          DATE,
  "to"            DATE,
  language        VARCHAR(50),
  latitude        DOUBLE PRECISION,
  longitude       DOUBLE PRECISION
);

CREATE TABLE event_has_labels (
  event_id INT,
  label_id INT,

  PRIMARY KEY (event_id, label_id)
);