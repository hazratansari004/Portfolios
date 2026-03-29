<?php
/**
 * api.php — REST-like API for TravelExplore.
 *
 * Routing: ?action=<name>  +  HTTP method
 *
 * Public:
 *   POST  ?action=register          Register new user
 *   POST  ?action=login             Authenticate, receive token
 *   POST  ?action=logout            Invalidate token
 *   GET   ?action=packages          List packages (optional ?search=, ?page=)
 *   GET   ?action=package&id=X      Single package details + reviews
 *   POST  ?action=inquiry           Submit contact/inquiry form
 *
 * Auth (any logged-in user):
 *   POST  ?action=book              Create booking
 *   GET   ?action=my_bookings       Current user's bookings
 *   POST  ?action=wishlist          Toggle wishlist (add/remove)
 *   GET   ?action=my_wishlist       Current user's wishlist
 *   POST  ?action=review            Post a review
 *
 * Admin only:
 *   POST  ?action=package           Create package
 *   PUT   ?action=package&id=X      Update package
 *   DELETE?action=package&id=X      Delete package
 *   GET   ?action=all_bookings      All bookings
 *   GET   ?action=all_users         All users
 *   GET   ?action=admin_stats       Dashboard counts
 *   GET   ?action=all_inquiries     All inquiries
 *   POST  ?action=update_booking_status  Update booking status
 */

declare(strict_types=1);

require_once __DIR__ . '/db.php';

// ── Headers ────────────────────────────────────────────────────────────────
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

// ── Router ─────────────────────────────────────────────────────────────────
$action = $_GET['action'] ?? '';
$method = $_SERVER['REQUEST_METHOD'];

switch ($action) {
    // Public
    case 'register':          handleRegister();         break;
    case 'login':             handleLogin();            break;
    case 'logout':            handleLogout();           break;
    case 'packages':          handlePackages();         break;
    case 'package':           handlePackage();          break;
    case 'inquiry':           handleInquiry();          break;

    // Auth user
    case 'book':              handleBook();             break;
    case 'my_bookings':       handleMyBookings();       break;
    case 'wishlist':          handleWishlist();         break;
    case 'my_wishlist':       handleMyWishlist();       break;
    case 'review':            handleReview();           break;

    // Admin
    case 'all_bookings':      handleAllBookings();      break;
    case 'all_users':         handleAllUsers();         break;
    case 'admin_stats':       handleAdminStats();       break;
    case 'all_inquiries':     handleAllInquiries();     break;
    case 'update_booking_status': handleUpdateBookingStatus(); break;

    default:
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Unknown action.']);
        break;
}

// ══════════════════════════════════════════════════════════════════════════
// HELPERS
// ══════════════════════════════════════════════════════════════════════════

function jsonBody(): array
{
    $data = json_decode(file_get_contents('php://input'), true);
    return is_array($data) ? $data : [];
}

function ok(array $data = [], string $message = 'OK'): void
{
    echo json_encode(['success' => true, 'message' => $message, 'data' => $data]);
}

function fail(string $message, int $code = 400): void
{
    http_response_code($code);
    echo json_encode(['success' => false, 'message' => $message]);
    exit;
}

function generateToken(): string
{
    return bin2hex(random_bytes(32));
}

// ══════════════════════════════════════════════════════════════════════════
// AUTH HANDLERS
// ══════════════════════════════════════════════════════════════════════════

function handleRegister(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') { fail('Method not allowed.', 405); }

    $body     = jsonBody();
    $name     = trim($body['name']     ?? '');
    $email    = trim($body['email']    ?? '');
    $password = trim($body['password'] ?? '');

    if ($name === '' || $email === '' || $password === '') {
        fail('Name, email, and password are required.');
    }
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        fail('Invalid email address.');
    }
    if (strlen($password) < 6) {
        fail('Password must be at least 6 characters.');
    }

    $pdo  = getPDO();
    $chk  = $pdo->prepare('SELECT id FROM users WHERE email = ? LIMIT 1');
    $chk->execute([$email]);
    if ($chk->fetch()) {
        fail('Email address is already registered.', 409);
    }

    $hash = password_hash($password, PASSWORD_DEFAULT);
    $ins  = $pdo->prepare('INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, "user")');
    $ins->execute([$name, $email, $hash]);
    $userId = (int)$pdo->lastInsertId();

    $token = generateToken();
    $exp   = date('Y-m-d H:i:s', strtotime('+7 days'));
    $pdo->prepare('INSERT INTO sessions (user_id, token, expires_at) VALUES (?, ?, ?)')->execute([$userId, $token, $exp]);

    ok(['user' => ['id' => $userId, 'name' => $name, 'email' => $email, 'role' => 'user'], 'token' => $token], 'Registration successful.');
}

function handleLogin(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') { fail('Method not allowed.', 405); }

    $body     = jsonBody();
    $email    = trim($body['email']    ?? '');
    $password = trim($body['password'] ?? '');

    if ($email === '' || $password === '') {
        fail('Email and password are required.');
    }

    $pdo  = getPDO();
    $stmt = $pdo->prepare('SELECT id, name, email, password, role FROM users WHERE email = ? LIMIT 1');
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if (!$user || !password_verify($password, $user['password'])) {
        fail('Invalid email or password.', 401);
    }

    $token = generateToken();
    $exp   = date('Y-m-d H:i:s', strtotime('+7 days'));

    // Remove old sessions for this user
    $pdo->prepare('DELETE FROM sessions WHERE user_id = ?')->execute([$user['id']]);
    $pdo->prepare('INSERT INTO sessions (user_id, token, expires_at) VALUES (?, ?, ?)')->execute([$user['id'], $token, $exp]);

    unset($user['password']);
    ok(['user' => $user, 'token' => $token], 'Login successful.');
}

function handleLogout(): void
{
    $user = requireAuth();
    $pdo  = getPDO();
    $pdo->prepare('DELETE FROM sessions WHERE user_id = ?')->execute([$user['id']]);
    ok([], 'Logged out successfully.');
}

// ══════════════════════════════════════════════════════════════════════════
// PACKAGE HANDLERS
// ══════════════════════════════════════════════════════════════════════════

function handlePackages(): void
{
    $method = $_SERVER['REQUEST_METHOD'];

    if ($method === 'GET') {
        // List packages with optional search + pagination
        $pdo    = getPDO();
        $search = trim($_GET['search'] ?? '');
        $page   = max(1, (int)($_GET['page'] ?? 1));
        $limit  = 12;
        $offset = ($page - 1) * $limit;

        if ($search !== '') {
            $like = "%{$search}%";
            $total = $pdo->prepare('SELECT COUNT(*) FROM packages WHERE title LIKE ? OR description LIKE ? OR location LIKE ?');
            $total->execute([$like, $like, $like]);
            $stmt  = $pdo->prepare('SELECT id, title, description, location, duration, max_persons, price, image_url, available_dates FROM packages WHERE title LIKE ? OR description LIKE ? OR location LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?');
            $stmt->execute([$like, $like, $like, $limit, $offset]);
        } else {
            $total = $pdo->query('SELECT COUNT(*) FROM packages');
            $stmt  = $pdo->prepare('SELECT id, title, description, location, duration, max_persons, price, image_url, available_dates FROM packages ORDER BY id DESC LIMIT ? OFFSET ?');
            $stmt->execute([$limit, $offset]);
        }

        $totalCount = (int)$total->fetchColumn();
        $packages   = $stmt->fetchAll();

        // Decode available_dates JSON
        foreach ($packages as &$pkg) {
            $pkg['available_dates'] = $pkg['available_dates'] ? json_decode($pkg['available_dates'], true) : [];
        }
        unset($pkg);

        ok(['packages' => $packages, 'total' => $totalCount, 'page' => $page, 'pages' => (int)ceil($totalCount / $limit)]);
        return;
    }

    if ($method === 'POST') {
        // Create package (admin only)
        $admin = requireAdmin();
        createPackage();
        return;
    }

    fail('Method not allowed.', 405);
}

function handlePackage(): void
{
    $method = $_SERVER['REQUEST_METHOD'];
    $id     = (int)($_GET['id'] ?? 0);

    if ($method === 'GET') {
        if ($id <= 0) { fail('Package ID is required.'); }
        $pdo  = getPDO();
        $stmt = $pdo->prepare('SELECT * FROM packages WHERE id = ? LIMIT 1');
        $stmt->execute([$id]);
        $pkg  = $stmt->fetch();
        if (!$pkg) { fail('Package not found.', 404); }

        $pkg['available_dates'] = $pkg['available_dates'] ? json_decode($pkg['available_dates'], true) : [];

        // Fetch reviews
        $rev  = $pdo->prepare(
            'SELECT r.id, r.rating, r.comment, r.created_at, u.name AS user_name
               FROM reviews r JOIN users u ON r.user_id = u.id
              WHERE r.package_id = ? ORDER BY r.created_at DESC'
        );
        $rev->execute([$id]);
        $pkg['reviews'] = $rev->fetchAll();

        // Avg rating
        $avg  = $pdo->prepare('SELECT ROUND(AVG(rating),1) as avg_rating, COUNT(*) as review_count FROM reviews WHERE package_id = ?');
        $avg->execute([$id]);
        $avgRow = $avg->fetch();
        $pkg['avg_rating']    = $avgRow['avg_rating'] ?? 0;
        $pkg['review_count']  = $avgRow['review_count'] ?? 0;

        ok($pkg);
        return;
    }

    if ($method === 'PUT' || ($method === 'POST' && ($_GET['_method'] ?? '') === 'PUT')) {
        requireAdmin();
        if ($id <= 0) { fail('Package ID is required.'); }
        updatePackage($id);
        return;
    }

    if ($method === 'DELETE' || ($method === 'POST' && ($_GET['_method'] ?? '') === 'DELETE')) {
        requireAdmin();
        if ($id <= 0) { fail('Package ID is required.'); }
        deletePackage($id);
        return;
    }

    fail('Method not allowed.', 405);
}

function createPackage(): void
{
    $body           = jsonBody();
    $title          = trim($body['title']       ?? '');
    $description    = trim($body['description'] ?? '');
    $location       = trim($body['location']    ?? '');
    $duration       = trim($body['duration']    ?? '');
    $max_persons    = max(1, (int)($body['max_persons'] ?? 10));
    $price          = (float)($body['price']    ?? 0);
    $image_url      = trim($body['image_url']   ?? '');
    $available_dates = $body['available_dates'] ?? [];

    if ($title === '' || $description === '' || $price <= 0 || $image_url === '') {
        fail('Title, description, price, and image URL are required.');
    }

    $pdo  = getPDO();
    $stmt = $pdo->prepare(
        'INSERT INTO packages (title, description, location, duration, max_persons, price, image_url, available_dates)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?)'
    );
    $stmt->execute([
        $title, $description, $location, $duration, $max_persons, $price, $image_url,
        json_encode(is_array($available_dates) ? $available_dates : [])
    ]);
    $newId = (int)$pdo->lastInsertId();

    $fetch = $pdo->prepare('SELECT * FROM packages WHERE id = ? LIMIT 1');
    $fetch->execute([$newId]);
    $pkg   = $fetch->fetch();
    $pkg['available_dates'] = json_decode($pkg['available_dates'], true);
    ok($pkg, 'Package created successfully.');
}

function updatePackage(int $id): void
{
    $body           = jsonBody();
    $title          = trim($body['title']       ?? '');
    $description    = trim($body['description'] ?? '');
    $location       = trim($body['location']    ?? '');
    $duration       = trim($body['duration']    ?? '');
    $max_persons    = max(1, (int)($body['max_persons'] ?? 10));
    $price          = (float)($body['price']    ?? 0);
    $image_url      = trim($body['image_url']   ?? '');
    $available_dates = $body['available_dates'] ?? [];

    if ($title === '' || $description === '' || $price <= 0 || $image_url === '') {
        fail('Title, description, price, and image URL are required.');
    }

    $pdo  = getPDO();
    $stmt = $pdo->prepare(
        'UPDATE packages SET title=?, description=?, location=?, duration=?, max_persons=?,
         price=?, image_url=?, available_dates=? WHERE id=?'
    );
    $stmt->execute([
        $title, $description, $location, $duration, $max_persons, $price, $image_url,
        json_encode(is_array($available_dates) ? $available_dates : []),
        $id
    ]);

    if ($stmt->rowCount() === 0) {
        // Verify the package exists even if no columns changed
        $existCheck = $pdo->prepare('SELECT id FROM packages WHERE id = ?');
        $existCheck->execute([$id]);
        if (!$existCheck->fetch()) { fail('Package not found.', 404); }
    }

    $fetch = $pdo->prepare('SELECT * FROM packages WHERE id = ? LIMIT 1');
    $fetch->execute([$id]);
    $pkg   = $fetch->fetch();
    if (!$pkg) { fail('Package not found.', 404); }
    $pkg['available_dates'] = json_decode($pkg['available_dates'], true);
    ok($pkg, 'Package updated successfully.');
}

function deletePackage(int $id): void
{
    $pdo  = getPDO();
    $stmt = $pdo->prepare('DELETE FROM packages WHERE id = ?');
    $stmt->execute([$id]);
    if ($stmt->rowCount() === 0) { fail('Package not found.', 404); }
    ok([], 'Package deleted successfully.');
}

// ══════════════════════════════════════════════════════════════════════════
// BOOKING HANDLERS
// ══════════════════════════════════════════════════════════════════════════

function handleBook(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') { fail('Method not allowed.', 405); }
    $user = requireAuth();
    $body = jsonBody();

    $package_id  = (int)($body['package_id']  ?? 0);
    $travel_date = trim($body['travel_date']  ?? '');
    $persons     = max(1, (int)($body['persons'] ?? 1));

    if ($package_id <= 0 || $travel_date === '') {
        fail('Package ID and travel date are required.');
    }

    // Validate date format
    $dateObj = DateTime::createFromFormat('Y-m-d', $travel_date);
    if (!$dateObj || $dateObj->format('Y-m-d') !== $travel_date) {
        fail('Invalid date format. Use YYYY-MM-DD.');
    }

    $pdo  = getPDO();
    $pkg  = $pdo->prepare('SELECT id, title, price, max_persons, available_dates FROM packages WHERE id = ? LIMIT 1');
    $pkg->execute([$package_id]);
    $package = $pkg->fetch();
    if (!$package) { fail('Package not found.', 404); }

    // Check max persons
    if ($persons > $package['max_persons']) {
        fail("Maximum {$package['max_persons']} persons allowed for this package.");
    }

    // Validate travel date is in available_dates if set
    $available = $package['available_dates'] ? json_decode($package['available_dates'], true) : [];
    if (!empty($available) && !in_array($travel_date, $available, true)) {
        fail('Selected date is not available for this package.');
    }

    $total_price = round($package['price'] * $persons, 2);

    $ins = $pdo->prepare(
        'INSERT INTO bookings (user_id, package_id, travel_date, persons, total_price, status)
         VALUES (?, ?, ?, ?, ?, "confirmed")'
    );
    $ins->execute([$user['id'], $package_id, $travel_date, $persons, $total_price]);
    $bookingId = (int)$pdo->lastInsertId();

    ok([
        'booking_id'  => $bookingId,
        'package'     => $package['title'],
        'travel_date' => $travel_date,
        'persons'     => $persons,
        'total_price' => $total_price,
        'status'      => 'confirmed',
    ], 'Booking confirmed!');
}

function handleMyBookings(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') { fail('Method not allowed.', 405); }
    $user = requireAuth();
    $pdo  = getPDO();

    $stmt = $pdo->prepare(
        'SELECT b.id, b.travel_date, b.persons, b.total_price, b.status, b.created_at,
                p.id AS package_id, p.title, p.location, p.image_url, p.duration
           FROM bookings b
           JOIN packages p ON b.package_id = p.id
          WHERE b.user_id = ?
          ORDER BY b.created_at DESC'
    );
    $stmt->execute([$user['id']]);
    ok(['bookings' => $stmt->fetchAll()]);
}

function handleAllBookings(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') { fail('Method not allowed.', 405); }
    requireAdmin();
    $pdo  = getPDO();

    $stmt = $pdo->query(
        'SELECT b.id, b.travel_date, b.persons, b.total_price, b.status, b.created_at,
                u.name AS user_name, u.email AS user_email,
                p.title AS package_title, p.location
           FROM bookings b
           JOIN users u ON b.user_id = u.id
           JOIN packages p ON b.package_id = p.id
          ORDER BY b.created_at DESC'
    );
    ok(['bookings' => $stmt->fetchAll()]);
}

function handleUpdateBookingStatus(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') { fail('Method not allowed.', 405); }
    requireAdmin();
    $body   = jsonBody();
    $id     = (int)($body['id']     ?? 0);
    $status = trim($body['status']  ?? '');

    if ($id <= 0 || !in_array($status, ['pending','confirmed','cancelled'], true)) {
        fail('Valid booking ID and status are required.');
    }

    $pdo  = getPDO();
    $stmt = $pdo->prepare('UPDATE bookings SET status = ? WHERE id = ?');
    $stmt->execute([$status, $id]);
    if ($stmt->rowCount() === 0) { fail('Booking not found.', 404); }
    ok([], 'Booking status updated.');
}

// ══════════════════════════════════════════════════════════════════════════
// WISHLIST HANDLERS
// ══════════════════════════════════════════════════════════════════════════

function handleWishlist(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') { fail('Method not allowed.', 405); }
    $user       = requireAuth();
    $body       = jsonBody();
    $package_id = (int)($body['package_id'] ?? 0);

    if ($package_id <= 0) { fail('Package ID is required.'); }

    $pdo  = getPDO();
    $chk  = $pdo->prepare('SELECT id FROM wishlists WHERE user_id = ? AND package_id = ?');
    $chk->execute([$user['id'], $package_id]);

    if ($chk->fetch()) {
        $pdo->prepare('DELETE FROM wishlists WHERE user_id = ? AND package_id = ?')->execute([$user['id'], $package_id]);
        ok(['wishlisted' => false], 'Removed from wishlist.');
    } else {
        // Verify package exists
        $pkgChk = $pdo->prepare('SELECT id FROM packages WHERE id = ?');
        $pkgChk->execute([$package_id]);
        if (!$pkgChk->fetch()) { fail('Package not found.', 404); }

        $pdo->prepare('INSERT INTO wishlists (user_id, package_id) VALUES (?, ?)')->execute([$user['id'], $package_id]);
        ok(['wishlisted' => true], 'Added to wishlist.');
    }
}

function handleMyWishlist(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') { fail('Method not allowed.', 405); }
    $user = requireAuth();
    $pdo  = getPDO();

    $stmt = $pdo->prepare(
        'SELECT p.id, p.title, p.location, p.price, p.image_url, p.duration
           FROM wishlists w JOIN packages p ON w.package_id = p.id
          WHERE w.user_id = ? ORDER BY w.created_at DESC'
    );
    $stmt->execute([$user['id']]);
    ok(['wishlist' => $stmt->fetchAll()]);
}

// ══════════════════════════════════════════════════════════════════════════
// REVIEW HANDLERS
// ══════════════════════════════════════════════════════════════════════════

function handleReview(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') { fail('Method not allowed.', 405); }
    $user       = requireAuth();
    $body       = jsonBody();
    $package_id = (int)($body['package_id'] ?? 0);
    $rating     = (int)($body['rating']     ?? 0);
    $comment    = trim($body['comment']     ?? '');

    if ($package_id <= 0 || $rating < 1 || $rating > 5 || $comment === '') {
        fail('Package ID, rating (1-5), and comment are required.');
    }

    $pdo  = getPDO();
    // Check package exists
    $pkgChk = $pdo->prepare('SELECT id FROM packages WHERE id = ?');
    $pkgChk->execute([$package_id]);
    if (!$pkgChk->fetch()) { fail('Package not found.', 404); }

    // Upsert review
    $stmt = $pdo->prepare(
        'INSERT INTO reviews (user_id, package_id, rating, comment)
         VALUES (?, ?, ?, ?)
         ON DUPLICATE KEY UPDATE rating = VALUES(rating), comment = VALUES(comment), created_at = NOW()'
    );
    $stmt->execute([$user['id'], $package_id, $rating, $comment]);
    ok([], 'Review submitted successfully.');
}

// ══════════════════════════════════════════════════════════════════════════
// ADMIN HANDLERS
// ══════════════════════════════════════════════════════════════════════════

function handleAllUsers(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') { fail('Method not allowed.', 405); }
    requireAdmin();
    $pdo  = getPDO();
    $stmt = $pdo->query('SELECT id, name, email, role, created_at FROM users ORDER BY id ASC');
    ok(['users' => $stmt->fetchAll()]);
}

function handleAdminStats(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') { fail('Method not allowed.', 405); }
    requireAdmin();
    $pdo = getPDO();

    $stats = [
        'packages'    => (int)$pdo->query('SELECT COUNT(*) FROM packages')->fetchColumn(),
        'users'       => (int)$pdo->query('SELECT COUNT(*) FROM users')->fetchColumn(),
        'bookings'    => (int)$pdo->query('SELECT COUNT(*) FROM bookings')->fetchColumn(),
        'revenue'     => (float)($pdo->query('SELECT COALESCE(SUM(total_price),0) FROM bookings WHERE status="confirmed"')->fetchColumn()),
        'inquiries'   => (int)$pdo->query('SELECT COUNT(*) FROM inquiries WHERE status="new"')->fetchColumn(),
    ];
    ok($stats);
}

// ══════════════════════════════════════════════════════════════════════════
// INQUIRY HANDLERS
// ══════════════════════════════════════════════════════════════════════════

function handleInquiry(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') { fail('Method not allowed.', 405); }

    $body    = jsonBody();
    $name    = trim($body['name']    ?? '');
    $email   = trim($body['email']   ?? '');
    $subject = trim($body['subject'] ?? '');
    $message = trim($body['message'] ?? '');

    if ($name === '' || $email === '' || $subject === '' || $message === '') {
        fail('All fields are required.');
    }
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        fail('Invalid email address.');
    }

    $pdo  = getPDO();
    $stmt = $pdo->prepare('INSERT INTO inquiries (name, email, subject, message) VALUES (?, ?, ?, ?)');
    $stmt->execute([$name, $email, $subject, $message]);
    ok([], 'Your inquiry has been submitted. We will get back to you soon!');
}

function handleAllInquiries(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') { fail('Method not allowed.', 405); }
    requireAdmin();
    $pdo  = getPDO();
    $stmt = $pdo->query('SELECT * FROM inquiries ORDER BY created_at DESC');
    ok(['inquiries' => $stmt->fetchAll()]);
}
