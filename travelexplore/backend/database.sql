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
  'Mount Everest Region',
  'Legendary Himalayan trekking past Sherpa villages toward the world’s highest peak. Sunrise views, prayer flags, and the iconic Everest Base Camp trail.',
  'Solukhumbu, Nepal', '12 days / 11 nights', 12, 3200.00,
  'https://images.unsplash.com/photo-1601062224947-3ca636754fb2?w=1080&q=80',
  '["2025-10-05","2025-10-19","2025-11-02","2026-03-15","2026-04-05"]'
),
(
  'Kathmandu Valley Heritage',
  'UNESCO-listed temples, vibrant bazaars, and living culture across Kathmandu, Patan, and Bhaktapur. Perfect for food, art, and history lovers.',
  'Kathmandu Valley, Nepal', '5 days / 4 nights', 16, 750.00,
  'https://images.unsplash.com/photo-1676873785328-6918536b7096?w=1080&q=80',
  '["2025-06-01","2025-06-15","2025-07-01","2025-08-01","2025-09-01"]'
),
(
  'Pokhara Lakeside Escape',
  'Serene Phewa Lake sunsets, paragliding over Annapurna, and sunrise at Sarangkot. A restful yet adventure-ready base in Nepal’s west.',
  'Pokhara, Nepal', '6 days / 5 nights', 14, 1100.00,
  'https://images.unsplash.com/photo-1647679208171-85d25dcc22c2?w=1080&q=80',
  '["2025-05-10","2025-06-05","2025-07-10","2025-09-05","2025-10-10"]'
),
(
  'Chitwan Jungle Safari',
  'Nepal’s first national park—spot one-horned rhinos, Bengal tigers, and rich birdlife. Includes jeep safari, canoe ride, and Tharu cultural evening.',
  'Chitwan, Nepal', '4 days / 3 nights', 10, 900.00,
  'https://images.unsplash.com/photo-1748343200591-3971d003dff4?w=1080&q=80',
  '["2025-11-01","2025-11-15","2025-12-01","2026-02-01","2026-03-01"]'
),
(
  'Lumbini Pilgrimage',
  'Sacred birthplace of Lord Buddha. Visit Mayadevi Temple, ancient monasteries, and the Ashoka Pillar amid peaceful gardens.',
  'Lumbini, Nepal', '3 days / 2 nights', 20, 450.00,
  'https://images.unsplash.com/photo-1625366877201-5f143a7b3118?w=1080&q=80',
  '["2025-05-05","2025-06-05","2025-09-05","2025-10-05","2025-11-05"]'
),
(
  'Bhaktapur Heritage Walk',
  'Medieval Newari architecture, pottery squares, and stunning temples. A time-travel stroll through Nepal’s living museum city.',
  'Bhaktapur, Nepal', '2 days / 1 night', 18, 300.00,
  'https://images.unsplash.com/photo-1745972263116-c9515bbf28c3?w=1080&q=80',
  '["2025-04-15","2025-05-15","2025-08-15","2025-09-15","2025-10-15"]'
),
(
  'Annapurna Circuit Highlights',
  'Classic Himalayan circuit with sweeping vistas, apple orchards of Manang, and the Thorong La high pass experience in a shorter format.',
  'Annapurna Region, Nepal', '10 days / 9 nights', 12, 2400.00,
  'https://images.unsplash.com/photo-1509644851169-2acc08aa25b2?w=1080&q=80',
  '["2025-10-10","2025-10-24","2025-11-07","2026-03-20","2026-04-10"]'
),
(
  'Langtang Valley Trek',
  'Alpine meadows, red panda habitats, and Tamang culture near Kathmandu. A rewarding trek with glaciers and sweeping valley views.',
  'Langtang, Nepal', '8 days / 7 nights', 14, 1800.00,
  'https://images.unsplash.com/photo-1500534623283-312aade485b7?w=1080&q=80',
  '["2025-09-20","2025-10-04","2025-10-18","2026-03-10","2026-04-02"]'
);
