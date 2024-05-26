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

-- Create indexes
CREATE INDEX idx_car_park_availability_no_lots
    ON car_park_availability (car_park_no, available_lots);

CREATE INDEX idx_latitude_longitude_info ON car_park_info(latitude, longitude);

