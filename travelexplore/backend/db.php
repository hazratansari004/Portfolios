<?php
/**
 * db.php — PDO database connection + auth helper for TravelExplore.
 */

declare(strict_types=1);

define('DB_HOST',    'localhost');
define('DB_PORT',    '3306');
define('DB_NAME',    'travelexplore');
define('DB_USER',    'root');      // Change for production
define('DB_PASS',    '');          // Change for production
define('DB_CHARSET', 'utf8mb4');

function getPDO(): PDO
{
    static $pdo = null;
    if ($pdo !== null) {
        return $pdo;
    }

    $dsn = sprintf(
        'mysql:host=%s;port=%s;dbname=%s;charset=%s',
        DB_HOST, DB_PORT, DB_NAME, DB_CHARSET
    );

    $options = [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
    ];

    try {
        $pdo = new PDO($dsn, DB_USER, DB_PASS, $options);
    } catch (PDOException $e) {
        http_response_code(500);
        header('Content-Type: application/json');
        echo json_encode(['success' => false, 'message' => 'Database connection failed.']);
        exit;
    }

    return $pdo;
}

/**
 * Validates the Bearer token from the Authorization header.
 * Returns the user row (id, name, email, role) or null if invalid.
 */
function getAuthUser(): ?array
{
    $headers = getallheaders();
    $auth    = $headers['Authorization'] ?? $headers['authorization'] ?? '';

    if (!preg_match('/^Bearer\s+(\S+)$/i', $auth, $m)) {
        return null;
    }

    $token = $m[1];
    $pdo   = getPDO();
    $stmt  = $pdo->prepare(
        'SELECT u.id, u.name, u.email, u.role
           FROM sessions s
           JOIN users u ON s.user_id = u.id
          WHERE s.token = ? AND s.expires_at > NOW()
          LIMIT 1'
    );
    $stmt->execute([$token]);
    $user = $stmt->fetch();

    return $user ?: null;
}

/**
 * Require an authenticated user; abort with 401 if not.
 */
function requireAuth(): array
{
    $user = getAuthUser();
    if (!$user) {
        http_response_code(401);
        echo json_encode(['success' => false, 'message' => 'Authentication required.']);
        exit;
    }
    return $user;
}

/**
 * Require admin role; abort with 403 if not.
 */
function requireAdmin(): array
{
    $user = requireAuth();
    if ($user['role'] !== 'admin') {
        http_response_code(403);
        echo json_encode(['success' => false, 'message' => 'Admin access required.']);
        exit;
    }
    return $user;
}
