-- TravelExplore Database Schema
-- Run this file once to create the database, tables, and seed data.

CREATE DATABASE IF NOT EXISTS travelexplore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE travelexplore;

-- --------------------------------------------------------
-- Table: users
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,          -- bcrypt hash
    role     ENUM('admin','user') NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: packages
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS packages (
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: bookings
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id    INT UNSIGNED NOT NULL,
    package_id INT UNSIGNED NOT NULL,
    booked_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status     ENUM('pending','confirmed','cancelled') NOT NULL DEFAULT 'pending',
    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Seed: users
-- admin123  -> bcrypt hash
-- user123   -> bcrypt hash
-- --------------------------------------------------------
INSERT INTO users (email, password, role) VALUES
  ('admin@travelexplore.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin'),
  ('user@travelexplore.com',  '$2y$10$TKh8H1.PfbuNIlgvVB1mZO3Qs7sHRInGXVlP7lnHOTbYMJpJOJhCa', 'user');
-- Note: the hash above for admin is the bcrypt of 'password' (Laravel default test hash).
-- For production replace with proper bcrypt hashes of 'admin123' and 'user123'.
-- The api.php uses password_verify() so any valid bcrypt hash works.

-- --------------------------------------------------------
-- Seed: packages
-- --------------------------------------------------------
INSERT INTO packages (title, description, price, image_url) VALUES
  (
    'Bali, Indonesia',
    'Experience the magic of Bali — lush rice terraces, ancient temples, vibrant nightlife, and world-class surfing. A perfect tropical escape.',
    1299.00,
    'https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=800&q=80'
  ),
  (
    'Santorini, Greece',
    'Iconic white-washed buildings, breathtaking caldera views, and golden sunsets over the Aegean Sea. Romance and beauty at every turn.',
    2199.00,
    'https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=800&q=80'
  ),
  (
    'Machu Picchu, Peru',
    'Trek through the misty Andes to the legendary Inca citadel. Discover ancient history, dramatic mountain scenery, and breathtaking altitude.',
    1849.00,
    'https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800&q=80'
  ),
  (
    'Safari, Kenya',
    'Witness the Great Migration on the Masai Mara plains. Encounter the Big Five, vast savannahs, and unforgettable African sunrises.',
    3499.00,
    'https://images.unsplash.com/photo-1612686635542-2244ed9f8ddc?w=800&q=80'
  ),
  (
    'Kyoto, Japan',
    'Wander through thousands of vermilion torii gates, serene bamboo forests, and centuries-old geisha districts in Japan\'s cultural heart.',
    2099.00,
    'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800&q=80'
  ),
  (
    'Patagonia, Argentina',
    'Hike among jagged granite peaks, turquoise lakes, and ancient glaciers at the edge of the world in Torres del Paine.',
    2649.00,
    'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=800&q=80'
  );
