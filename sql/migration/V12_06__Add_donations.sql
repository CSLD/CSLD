CREATE TABLE donation (
  id              SERIAL PRIMARY KEY,
  donor           TEXT,
  "date"          DATE,
  amount          DOUBLE PRECISION
);