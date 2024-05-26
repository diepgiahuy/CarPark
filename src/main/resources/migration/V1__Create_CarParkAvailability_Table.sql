CREATE TABLE IF NOT EXISTS car_park_info (
      car_park_no VARCHAR(8) PRIMARY KEY NOT NULL,
      latitude DECIMAL,
      longitude DECIMAL,
      address VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS car_park_availability (
   car_park_no VARCHAR(8) PRIMARY KEY NOT NULL,
   total_lots INT,
   available_lots INT
);

-- for now we don't need to use index on the small dataset but can use in the future
--CREATE INDEX idx_covering_car_park_availability ON car_park_availability (car_park_no, available_lots);
