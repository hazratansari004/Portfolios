-- TravelExplore Database Schema
-- Run this file once to set up the full database, tables, and seed data.

CREATE DATABASE IF NOT EXISTS travelexplore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE travelexplore;

-- --------------------------------------------------------
-- Table: users
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,   -- bcrypt hash
    role       ENUM('admin','user') NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: sessions  (token-based auth, stateless)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS sessions (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id    INT UNSIGNED NOT NULL,
    token      VARCHAR(64)  NOT NULL UNIQUE,
    expires_at DATETIME     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: packages
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS packages (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255)     NOT NULL,
    description     TEXT             NOT NULL,
    location        VARCHAR(255)     NOT NULL DEFAULT '',
    duration        VARCHAR(100)     NOT NULL DEFAULT '',
    max_persons     TINYINT UNSIGNED NOT NULL DEFAULT 10,
    price           DECIMAL(10,2)    NOT NULL,
    image_url       VARCHAR(500)     NOT NULL,
    available_dates JSON             NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: bookings
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id     INT UNSIGNED NOT NULL,
    package_id  INT UNSIGNED NOT NULL,
    travel_date DATE         NOT NULL,
    persons     TINYINT UNSIGNED NOT NULL DEFAULT 1,
    total_price DECIMAL(10,2)    NOT NULL,
    status      ENUM('pending','confirmed','cancelled') NOT NULL DEFAULT 'pending',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: reviews
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id    INT UNSIGNED NOT NULL,
    package_id INT UNSIGNED NOT NULL,
    rating     TINYINT NOT NULL,
    comment    TEXT    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_package (user_id, package_id),
    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: wishlists
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS wishlists (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id    INT UNSIGNED NOT NULL,
    package_id INT UNSIGNED NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_wishlist (user_id, package_id),
    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: inquiries  (contact form)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS inquiries (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    subject    VARCHAR(255) NOT NULL,
    message    TEXT         NOT NULL,
    status     ENUM('new','read','replied') NOT NULL DEFAULT 'new',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================================
-- SEED DATA
-- =========================================================

-- Passwords: admin123 | user123
INSERT INTO users (name, email, password, role) VALUES
  ('Admin User',     'admin@travelexplore.com',
   '$2y$10$2rTxCLbCgX/KRyIZkdw1VuGTGaCvx/bnGld8aVVHZSW/HHZjpDPb2', 'admin'),
  ('John Traveller', 'user@travelexplore.com',
   '$2y$10$tPaC7VjKtFiCMj7OLB/MuOTHOZLNjx./n5ldM4EKndO89Kfje4t4O', 'user');

INSERT INTO packages (title, description, location, duration, max_persons, price, image_url, available_dates) VALUES
(
  'Bali Paradise Retreat',
  'Experience the magic of Bali — lush rice terraces, ancient Hindu temples, vibrant arts scene, world-class surfing and a thriving nightlife. Includes guided temple tours, cooking class, and sunset beach dinner.',
  'Bali, Indonesia', '7 days / 6 nights', 12, 1299.00,
  'https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=800&q=80',
  '["2025-07-01","2025-07-15","2025-08-01","2025-08-15","2025-09-01"]'
),
(
  'Santorini Sunset Experience',
  'Iconic white-washed buildings perched on volcanic cliffs, breathtaking caldera views, and legendary golden sunsets over the Aegean Sea. Includes private villa, wine-tasting tour, and sailing excursion.',
  'Santorini, Greece', '5 days / 4 nights', 8, 2199.00,
  'https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=800&q=80',
  '["2025-06-10","2025-06-24","2025-07-08","2025-07-22","2025-08-05"]'
),
(
  'Machu Picchu Trek',
  'Trek through the misty Andes to the legendary Inca citadel. Discover ancient history, dramatic mountain scenery, and breathtaking altitude. Includes Inca Trail permit, professional guide, and train return.',
  'Cusco, Peru', '8 days / 7 nights', 10, 1849.00,
  'https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800&q=80',
  '["2025-05-15","2025-06-01","2025-06-15","2025-07-01","2025-09-15"]'
),
(
  'Kenya Big Five Safari',
  'Witness the Great Migration on the Masai Mara plains. Encounter the Big Five on daily game drives, experience a traditional Maasai village, and sleep under the stars in a luxury tented camp.',
  'Masai Mara, Kenya', '6 days / 5 nights', 6, 3499.00,
  'https://images.unsplash.com/photo-1612686635542-2244ed9f8ddc?w=800&q=80',
  '["2025-07-10","2025-07-24","2025-08-07","2025-08-21","2025-09-04"]'
),
(
  'Kyoto Cultural Journey',
  'Wander through thousands of vermilion torii gates at Fushimi Inari, serene bamboo forests in Arashiyama, and the historic geisha district of Gion. Includes tea ceremony, kimono rental, and Nishiki market tour.',
  'Kyoto, Japan', '6 days / 5 nights', 10, 2099.00,
  'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800&q=80',
  '["2025-04-01","2025-04-15","2025-10-01","2025-10-15","2025-11-01"]'
),
(
  'Patagonia Wilderness Hike',
  'Hike among jagged granite peaks, turquoise lakes, and ancient glaciers at the southern tip of the Americas. Includes W-trek in Torres del Paine, glacier hike, and boat to Perito Moreno.',
  'Patagonia, Argentina', '10 days / 9 nights', 8, 2649.00,
  'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=800&q=80',
  '["2025-11-15","2025-12-01","2025-12-15","2026-01-05","2026-01-20"]'
),
(
  'Maldives Overwater Escape',
  'Sleep in an overwater bungalow above crystal-clear turquoise lagoons teeming with marine life. Includes snorkelling with manta rays, sunset dolphin cruise, and couples spa treatment.',
  'South Male Atoll, Maldives', '5 days / 4 nights', 4, 3299.00,
  'https://images.unsplash.com/photo-1514282401047-d79a71a590e8?w=800&q=80',
  '["2025-06-05","2025-07-05","2025-08-05","2025-09-05","2025-10-05"]'
),
(
  'Amalfi Coast Drive',
  'Wind along one of the world''s most dramatic coastal roads, stopping at Positano, Ravello, and Amalfi. Includes boat trip to Capri, limoncello distillery visit, and Neapolitan cooking class.',
  'Amalfi, Italy', '7 days / 6 nights', 10, 1999.00,
  'https://images.unsplash.com/photo-1533587851505-d119e13fa0d7?w=800&q=80',
  '["2025-05-20","2025-06-10","2025-07-01","2025-08-10","2025-09-10"]'
);
