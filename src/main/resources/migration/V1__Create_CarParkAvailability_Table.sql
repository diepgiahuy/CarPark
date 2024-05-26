CREATE TABLE car_park_info (
      car_park_no VARCHAR(8) PRIMARY KEY NOT NULL,
      latitude DECIMAL,
      longitude DECIMAL,
      address VARCHAR(256)
);

CREATE TABLE car_park_availability (
   car_park_no VARCHAR(8) PRIMARY KEY NOT NULL,
   total_lots INT,
   available_lots INT,
);