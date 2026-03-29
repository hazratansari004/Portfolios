<?php
/**
 * api.php — REST-like API for TravelExplore.
 *
 * Endpoints:
 *   GET  api.php?action=packages        → list all travel packages
 *   POST api.php?action=login           → authenticate user
 *
 * All responses are JSON.
 */

declare(strict_types=1);

require_once __DIR__ . '/db.php';

// ── CORS & response headers ────────────────────────────────────────────────
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Pre-flight request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

// ── Router ─────────────────────────────────────────────────────────────────
$action = $_GET['action'] ?? '';

switch ($action) {
    case 'packages':
        handlePackages();
        break;

    case 'login':
        handleLogin();
        break;

    default:
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Unknown action.']);
        break;
}

// ── Handlers ───────────────────────────────────────────────────────────────

/**
 * GET api.php?action=packages
 * Returns all travel packages ordered by id.
 */
function handlePackages(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
        http_response_code(405);
        echo json_encode(['success' => false, 'message' => 'Method not allowed.']);
        return;
    }

    $pdo  = getPDO();
    $stmt = $pdo->query('SELECT id, title, description, price, image_url FROM packages ORDER BY id ASC');
    $rows = $stmt->fetchAll();

    echo json_encode(['success' => true, 'data' => $rows]);
}

/**
 * POST api.php?action=login
 * Body (JSON): { "email": "...", "password": "..." }
 * Returns user data (without password hash) and their role on success.
 */
function handleLogin(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        http_response_code(405);
        echo json_encode(['success' => false, 'message' => 'Method not allowed.']);
        return;
    }

    // Read JSON body
    $body = json_decode(file_get_contents('php://input'), true);

    $email    = trim($body['email']    ?? '');
    $password = trim($body['password'] ?? '');

    if ($email === '' || $password === '') {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Email and password are required.']);
        return;
    }

    $pdo  = getPDO();
    $stmt = $pdo->prepare('SELECT id, email, password, role FROM users WHERE email = ? LIMIT 1');
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if (!$user || !password_verify($password, $user['password'])) {
        http_response_code(401);
        echo json_encode(['success' => false, 'message' => 'Invalid email or password.']);
        return;
    }

    // Never return the password hash to the client
    unset($user['password']);

    echo json_encode(['success' => true, 'data' => $user]);
}
