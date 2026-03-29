/**
 * main.js — TravelExplore Full SPA
 *
 * Architecture:
 *   - Router: navigate() shows/hides <section.view> elements
 *   - State:  user session & cache stored in memory + localStorage
 *   - API:    all fetch() calls centralised in api.*  namespace
 *   - Auth:   Bearer token sent in Authorization header
 *   - Views:  each view has a dedicated render/load function
 */

'use strict';

/* ════════════════════════════════════════════════════
   CONFIG
════════════════════════════════════════════════════ */
// Adjust to your server path, e.g.:
// const API = 'http://localhost/travelexplore/backend/api.php';
const API = '../backend/api.php';

/* ════════════════════════════════════════════════════
   STATE
════════════════════════════════════════════════════ */
const state = {
  user:        null,   // { id, name, email, role }
  token:       null,   // auth token string
  packages:    [],     // cached package list
  wishlistIds: new Set(),
  currentPkg:  null,   // package currently being viewed
  pkgPage:     1,
  pkgSearch:   '',
};

const POPULAR_DESTINATIONS = [
  { id: 'bali',             title: 'Bali, Indonesia',           location: 'Bali, Indonesia',           region: 'Asia',          theme: 'Beach',    tagline: 'Beach Paradise',     description: 'Tropical island with stunning beaches, rice terraces, and vibrant culture.', rating: 4.8, price: 850,  image: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200&q=80' },
  { id: 'kyoto',            title: 'Kyoto, Japan',              location: 'Kyoto, Japan',              region: 'Asia',          theme: 'Culture',  tagline: 'Cultural Heritage',  description: 'Ancient temples, traditional gardens, and the beauty of Japanese culture.',     rating: 4.9, price: 1100, image: 'https://images.unsplash.com/photo-1504788363733-507549153474?w=1200&q=80' },
  { id: 'swiss-alps',       title: 'Swiss Alps, Switzerland',   location: 'Swiss Alps, Switzerland',   region: 'Europe',        theme: 'Adventure',tagline: 'Adventure',            description: 'Majestic mountain peaks, skiing, and breathtaking alpine scenery.',            rating: 4.8, price: 980,  image: 'https://images.unsplash.com/photo-1508261306217-70c4e4575040?w=1200&q=80' },
  { id: 'paris',            title: 'Paris, France',             location: 'Paris, France',             region: 'Europe',        theme: 'City',     tagline: 'City Escape',        description: 'The city of lights — art, fashion, cuisine, and the Eiffel Tower.',              rating: 4.7, price: 1500, image: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=1200&q=80' },
  { id: 'safari',           title: 'Safari, Kenya',             location: 'Maasai Mara, Kenya',        region: 'Africa',        theme: 'Wildlife', tagline: 'Wildlife',             description: 'Witness the great migration and incredible African wildlife up close.',       rating: 4.6, price: 1800, image: 'https://images.unsplash.com/photo-1508675801627-066ac4346a24?w=1200&q=80' },
  { id: 'maldives',         title: 'Maldives, Maldives',        location: 'Maldives',                  region: 'Asia',          theme: 'Luxury',   tagline: 'Luxury Resort',       description: 'Crystal-clear waters, overwater villas, and world-class snorkeling.',             rating: 4.9, price: 2200, image: 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad?w=1200&q=80' },
  { id: 'santorini',        title: 'Santorini, Greece',         location: 'Santorini, Greece',         region: 'Europe',        theme: 'Romantic', tagline: 'Romantic Getaway',     description: 'Iconic white-washed buildings, stunning sunsets, and Aegean Sea views.',        rating: 4.7, price: 1300, image: 'https://images.unsplash.com/photo-1505765050516-f72dcac9c60e?w=1200&q=80' },
  { id: 'dubai',            title: 'Dubai, UAE',                location: 'Dubai, UAE',                region: 'Asia',          theme: 'City',     tagline: 'Modern Marvel',       description: 'Futuristic skyline, luxury shopping, and desert adventures.',                    rating: 4.8, price: 1050, image: 'https://images.unsplash.com/photo-1504270997636-07ddfbd48945?w=1200&q=80' },
  { id: 'machu-picchu',     title: 'Machu Picchu, Peru',        location: 'Machu Picchu, Peru',        region: 'South America', theme: 'Culture',  tagline: 'Historic Wonder',     description: 'Ancient Incan citadel high in the Andes mountains.',                             rating: 4.9, price: 1400, image: 'https://images.unsplash.com/photo-1506377247377-2a5b3b417ebb?w=1200&q=80' },
  { id: 'iceland',          title: 'Iceland, Iceland',          location: 'Iceland',                   region: 'Europe',        theme: 'Adventure',tagline: 'Northern Lights',       description: 'Aurora borealis, geysers, glaciers, and volcanic landscapes.',                 rating: 4.8, price: 1600, image: 'https://images.unsplash.com/photo-1500048993953-d23a436266cf?w=1200&q=80' },
  { id: 'new-york',         title: 'New York, USA',             location: 'New York, USA',             region: 'North America', theme: 'City',     tagline: 'Urban Explorer',      description: 'The Big Apple — Broadway, Central Park, and world-famous landmarks.',            rating: 4.7, price: 1200, image: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=1200&q=80' },
  { id: 'venice',           title: 'Venice, Italy',             location: 'Venice, Italy',             region: 'Europe',        theme: 'Romantic', tagline: 'Romantic Escape',      description: 'Canals, gondolas, architecture, and Italian charm at every corner.',            rating: 4.6, price: 950,  image: 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad?w=1200&q=80&sat=-20' },
  { id: 'everest',          title: 'Mount Everest, Nepal',      location: 'Mount Everest, Nepal',      region: 'Asia',          theme: 'Adventure',tagline: 'Extreme Adventure',     description: 'Trek through breathtaking Himalayan trails to Everest Base Camp.',             rating: 4.9, price: 2000, image: 'https://images.unsplash.com/photo-1509648076484-18f9aee9b27b?w=1200&q=80' },
  { id: 'kathmandu',        title: 'Kathmandu, Nepal',          location: 'Kathmandu, Nepal',          region: 'Asia',          theme: 'Culture',  tagline: 'Cultural Heritage',  description: 'Ancient temples, vibrant Durbar squares, and the gateway to the Himalayas.',   rating: 4.7, price: 600,  image: 'https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=1200&q=80' },
  { id: 'rio',              title: 'Rio de Janeiro, Brazil',    location: 'Rio de Janeiro, Brazil',    region: 'South America', theme: 'City',     tagline: 'Carnival Vibes',      description: 'Samba, beaches, Christ the Redeemer, and the energy of Copacabana.',             rating: 4.7, price: 1100, image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=1200&q=80' },
  { id: 'sydney',           title: 'Sydney, Australia',         location: 'Sydney, Australia',         region: 'Oceania',       theme: 'City',     tagline: 'Coastal City',        description: 'Iconic Opera House, Harbour Bridge, and stunning coastal beaches.',              rating: 4.8, price: 1350, image: 'https://images.unsplash.com/photo-1506976785307-8732e854ad89?w=1200&q=80' },
  { id: 'cairo',            title: 'Cairo, Egypt',              location: 'Cairo, Egypt',              region: 'Africa',        theme: 'Culture',  tagline: 'Ancient Wonders',     description: 'Pyramids of Giza, the Sphinx, and thousands of years of history.',              rating: 4.6, price: 750,  image: 'https://images.unsplash.com/photo-1524492449092-4025a66b8115?w=1200&q=80' },
  { id: 'bangkok',          title: 'Bangkok, Thailand',         location: 'Bangkok, Thailand',         region: 'Asia',          theme: 'Food',     tagline: 'Street Food Capital', description: 'Ornate temples, floating markets, and the world’s best street food.',            rating: 4.7, price: 700,  image: 'https://images.unsplash.com/photo-1467269204594-9661b134dd2b?w=1200&q=80' },
  { id: 'cape-town',        title: 'Cape Town, South Africa',   location: 'Cape Town, South Africa',   region: 'Africa',        theme: 'Nature',   tagline: 'Nature & City',       description: 'Table Mountain, vineyards, penguins, and stunning coastal drives.',              rating: 4.8, price: 1000, image: 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200&q=80' },
  { id: 'marrakech',        title: 'Marrakech, Morocco',        location: 'Marrakech, Morocco',        region: 'Africa',        theme: 'Culture',  tagline: 'Exotic Markets',      description: 'Vibrant souks, riads, spices, and mesmerizing Moroccan architecture.',          rating: 4.5, price: 650,  image: 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad?w=1200&q=80&sat=-10' },
  { id: 'cancun',           title: 'Cancún, Mexico',            location: 'Cancún, Mexico',            region: 'North America', theme: 'Beach',    tagline: 'Beach Resort',        description: 'Turquoise Caribbean waters, Mayan ruins, and all-inclusive resorts.',            rating: 4.6, price: 900,  image: 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad?w=1200&q=80&sat=10' },
  { id: 'petra',            title: 'Petra, Jordan',             location: 'Petra, Jordan',             region: 'Asia',          theme: 'Culture',  tagline: 'Lost City',           description: 'The rose-red city carved into rock — one of the New Seven Wonders.',             rating: 4.9, price: 850,  image: 'https://images.unsplash.com/photo-1526804507-25e8e2ee632e?w=1200&q=80' },
  { id: 'london',           title: 'London, England',           location: 'London, England',           region: 'Europe',        theme: 'City',     tagline: 'Historic Capital',    description: 'Buckingham Palace, Tower Bridge, and centuries of royal history.',               rating: 4.6, price: 1000, image: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=1200&q=80&sat=-12' },
  { id: 'toronto',          title: 'Toronto, Canada',           location: 'Toronto, Canada',           region: 'North America', theme: 'City',     tagline: 'Multicultural Hub',   description: 'CN Tower, diverse neighborhoods, and a thriving food scene.',                    rating: 4.7, price: 1150, image: 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad?w=1200&q=80&sat=15' },
  { id: 'sri-lanka',        title: 'Sri Lanka, Sri Lanka',      location: 'Sri Lanka',                 region: 'Asia',          theme: 'Beach',    tagline: 'Tropical Paradise',   description: 'Tea plantations, ancient ruins, golden beaches, and elephant safaris.',          rating: 4.7, price: 650,  image: 'https://images.unsplash.com/photo-1526481280695-3c469c2f3a38?w=1200&q=80' },
  { id: 'patagonia',        title: 'Patagonia, Argentina',      location: 'Patagonia, Argentina',      region: 'South America', theme: 'Adventure',tagline: 'Wild Frontier',        description: 'Glaciers, mountains, pristine lakes, and untouched wilderness.',                 rating: 4.8, price: 1700, image: 'https://images.unsplash.com/photo-1489515217757-5fd1be406fef?w=1200&q=80' },
  { id: 'amsterdam',        title: 'Amsterdam, Netherlands',    location: 'Amsterdam, Netherlands',    region: 'Europe',        theme: 'Culture',  tagline: 'Canal City',          description: 'Tulips, windmills, world-class museums, and charming canal houses.',            rating: 4.7, price: 950,  image: 'https://images.unsplash.com/photo-1526481280695-3c469c2f3a38?w=1200&q=80&sat=-15' },
  { id: 'hanoi',            title: 'Hanoi, Vietnam',            location: 'Hanoi, Vietnam',            region: 'Asia',          theme: 'Food',     tagline: 'Street Food Haven',   description: 'Ancient quarter, pho, lantern-lit streets, and Ha Long Bay nearby.',             rating: 4.6, price: 550,  image: 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad?w=1200&q=80&sat=-5' },
];

const popularState = { region: 'All', theme: '', search: '' };

// Static fallback data so the app can still demonstrate core flows when the PHP
// backend isn't running. Keep the shape close to the real API response.
const FALLBACK_PACKAGES = [
  {
    id: 1,
    title: 'Everest Base Camp Trek',
    description: 'Classic trekking route with panoramic Himalayan views and Sherpa culture.',
    location: 'Everest Region, Nepal',
    duration: '12 days',
    max_persons: 12,
    price: 1499,
    image_url: 'https://images.unsplash.com/photo-1509644851169-2acc08aa25b2?w=1200&q=80',
    available_dates: ['2025-04-15', '2025-05-05', '2025-10-10'],
    avg_rating: 4.8,
    review_count: 18,
    reviews: [
      { id: 101, user_name: 'Tenzing S.', rating: 5, comment: 'Lifetime trek with expert guides.', created_at: '2024-11-12' },
      { id: 102, user_name: 'Maya K.',    rating: 4, comment: 'Great acclimatization and views.',  created_at: '2024-09-08' },
    ],
  },
  {
    id: 2,
    title: 'Pokhara Lakeside Escape',
    description: 'Relaxed getaway with paragliding, boating, and sunrise at Sarangkot.',
    location: 'Pokhara, Nepal',
    duration: '4 days',
    max_persons: 8,
    price: 499,
    image_url: 'https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=1200&q=80',
    available_dates: ['2025-04-02', '2025-05-18', '2025-06-07'],
    avg_rating: 4.6,
    review_count: 12,
    reviews: [
      { id: 201, user_name: 'Arjun D.', rating: 5, comment: 'Paragliding was unforgettable.', created_at: '2024-08-15' },
      { id: 202, user_name: 'Sonia L.', rating: 4, comment: 'Peaceful lakeside mornings.',   created_at: '2024-07-04' },
    ],
  },
  {
    id: 3,
    title: 'Chitwan Jungle Safari',
    description: 'Wildlife safari with canoe rides, jeep tours, and Tharu cultural evening.',
    location: 'Chitwan, Nepal',
    duration: '3 days',
    max_persons: 10,
    price: 399,
    image_url: 'https://images.unsplash.com/photo-1618334215752-7f50c0b786d1?w=1200&q=80',
    available_dates: ['2025-04-20', '2025-05-12', '2025-06-25'],
    avg_rating: 4.5,
    review_count: 9,
    reviews: [
      { id: 301, user_name: 'Priya M.', rating: 5, comment: 'Saw rhinos and crocodiles up close!', created_at: '2024-10-03' },
      { id: 302, user_name: 'Jacob R.', rating: 4, comment: 'Great guides and comfy lodge.',       created_at: '2024-09-19' },
    ],
  },
  {
    id: 4,
    title: 'Annapurna Base Camp',
    description: 'Iconic trek through rhododendron forests to towering Annapurna peaks.',
    location: 'Annapurna Region, Nepal',
    duration: '10 days',
    max_persons: 14,
    price: 1299,
    image_url: 'https://images.unsplash.com/photo-1509648076484-18f9aee9b27b?w=1200&q=80',
    available_dates: ['2025-04-28', '2025-05-22', '2025-09-14'],
    avg_rating: 4.7,
    review_count: 15,
    reviews: [
      { id: 401, user_name: 'Lhakpa G.', rating: 5, comment: 'Sunrise at ABC is magical.', created_at: '2024-11-01' },
      { id: 402, user_name: 'Hannah T.', rating: 4, comment: 'Well-paced itinerary and food.', created_at: '2024-08-21' },
    ],
  },
];

function fallbackPackages(search = '', page = 1) {
  const term = (search || '').toLowerCase();
  const limit = 12;
  const filtered = term
    ? FALLBACK_PACKAGES.filter(pkg =>
        [pkg.title, pkg.description, pkg.location].some(field =>
          (field || '').toLowerCase().includes(term)))
    : FALLBACK_PACKAGES;

  const total = filtered.length;
  const pages = Math.max(1, Math.ceil(total / limit));
  const currentPage = Math.min(Math.max(1, page), pages);
  const start = (currentPage - 1) * limit;

  return {
    packages: filtered.slice(start, start + limit),
    total,
    page: currentPage,
    pages,
  };
}

function fallbackPackageById(id) {
  return FALLBACK_PACKAGES.find(pkg => Number(pkg.id) === Number(id)) || null;
}

function filterPopularDestinations() {
  const term = (popularState.search || '').toLowerCase();
  return POPULAR_DESTINATIONS.filter(d => {
    const matchesRegion = popularState.region === 'All' || d.region === popularState.region;
    const matchesTheme  = !popularState.theme || d.theme === popularState.theme;
    const haystack = [d.title, d.location, d.tagline, d.theme].join(' ').toLowerCase();
    const matchesSearch = !term || haystack.includes(term);
    return matchesRegion && matchesTheme && matchesSearch;
  });
}

function renderPopularDestinations() {
  const grid    = document.getElementById('popularGrid');
  const emptyEl = document.getElementById('popularEmpty');
  if (!grid) return;

  const items = filterPopularDestinations();
  if (!items.length) {
    grid.innerHTML = '';
    emptyEl?.classList.remove('d-none');
    return;
  }

  emptyEl?.classList.add('d-none');
  grid.innerHTML = items.map(d => `
    <article class="popular-card">
      <img class="popular-card__img" src="${d.image}" alt="${d.title}" loading="lazy">
      <div class="popular-card__body">
        <span class="popular-card__eyebrow">${d.tagline}</span>
        <h3 class="popular-card__title">${d.title}</h3>
        <p class="popular-card__location">${d.location}</p>
        <p class="popular-card__desc">${d.description}</p>
        <div class="popular-card__meta">
          <span class="popular-card__rating">⭐ ${d.rating.toFixed(1)}</span>
          <span class="popular-card__price">$${d.price}/person</span>
        </div>
        <button class="btn btn--outline popular-card__btn" data-view="register">Book Now</button>
      </div>
    </article>
  `).join('');
}

/* ════════════════════════════════════════════════════
   SESSION PERSISTENCE
 ════════════════════════════════════════════════════ */
function loadSession() {
  try {
    const raw = localStorage.getItem('te_session');
    if (raw) {
      const s = JSON.parse(raw);
      state.user  = s.user  || null;
      state.token = s.token || null;
    }
  } catch { /* ignore */ }
}

function saveSession(user, token) {
  state.user  = user;
  state.token = token;
  localStorage.setItem('te_session', JSON.stringify({ user, token }));
}

function clearSession() {
  state.user  = null;
  state.token = null;
  localStorage.removeItem('te_session');
}

/* ════════════════════════════════════════════════════
   API LAYER
════════════════════════════════════════════════════ */
const api = {
  _headers(withAuth = false) {
    const h = { 'Content-Type': 'application/json' };
    if (withAuth && state.token) h['Authorization'] = `Bearer ${state.token}`;
    return h;
  },

  async _req(url, opts = {}) {
    const res = await fetch(url, opts);
    const text = await res.text();
    const actionLabel = (() => {
      try {
        const u = new URL(url, location.origin);
        return u.searchParams.get('action') || u.pathname || '';
      } catch { return ''; }
    })();
    const label = actionLabel && actionLabel !== '/' ? actionLabel : 'unknown endpoint';
    const context = ` for ${label}`;

    if (!text) throw new Error(`Empty response from server (status ${res.status})${context}`);

    let data;
    try { data = JSON.parse(text); }
    catch {
      throw new Error(`Unable to parse server response (status ${res.status})${context}`);
    }

    if (!data.success) throw new Error(data.message || `HTTP ${res.status}`);
    return data.data;
  },

  register(name, email, password) {
    return this._req(`${API}?action=register`, {
      method:  'POST',
      headers: this._headers(),
      body:    JSON.stringify({ name, email, password }),
    });
  },

  login(email, password) {
    return this._req(`${API}?action=login`, {
      method:  'POST',
      headers: this._headers(),
      body:    JSON.stringify({ email, password }),
    });
  },

  logout() {
    return this._req(`${API}?action=logout`, {
      method:  'POST',
      headers: this._headers(true),
    });
  },

  getPackages(search = '', page = 1) {
    const qs = new URLSearchParams({ action: 'packages', page });
    if (search) qs.set('search', search);

    return this._req(`${API}?${qs}`).catch(err => {
      console.warn('Falling back to static packages:', err);
      return fallbackPackages(search, page);
    });
  },

  getPackage(id) {
    return this._req(`${API}?action=package&id=${id}`).catch(err => {
      console.warn('Falling back to static package detail:', err);
      const pkg = fallbackPackageById(id);
      if (pkg) return pkg;
      throw err;
    });
  },

  createPackage(data) {
    return this._req(`${API}?action=packages`, {
      method:  'POST',
      headers: this._headers(true),
      body:    JSON.stringify(data),
    });
  },

  updatePackage(id, data) {
    return this._req(`${API}?action=package&id=${id}`, {
      method:  'PUT',
      headers: this._headers(true),
      body:    JSON.stringify(data),
    });
  },

  deletePackage(id) {
    return this._req(`${API}?action=package&id=${id}`, {
      method:  'DELETE',
      headers: this._headers(true),
    });
  },

  book(packageId, travelDate, persons) {
    return this._req(`${API}?action=book`, {
      method:  'POST',
      headers: this._headers(true),
      body:    JSON.stringify({ package_id: packageId, travel_date: travelDate, persons }),
    });
  },

  myBookings() {
    return this._req(`${API}?action=my_bookings`, { headers: this._headers(true) });
  },

  allBookings() {
    return this._req(`${API}?action=all_bookings`, { headers: this._headers(true) });
  },

  updateBookingStatus(id, status) {
    return this._req(`${API}?action=update_booking_status`, {
      method:  'POST',
      headers: this._headers(true),
      body:    JSON.stringify({ id, status }),
    });
  },

  toggleWishlist(packageId) {
    return this._req(`${API}?action=wishlist`, {
      method:  'POST',
      headers: this._headers(true),
      body:    JSON.stringify({ package_id: packageId }),
    });
  },

  myWishlist() {
    return this._req(`${API}?action=my_wishlist`, { headers: this._headers(true) });
  },

  submitReview(packageId, rating, comment) {
    return this._req(`${API}?action=review`, {
      method:  'POST',
      headers: this._headers(true),
      body:    JSON.stringify({ package_id: packageId, rating, comment }),
    });
  },

  adminStats() {
    return this._req(`${API}?action=admin_stats`, { headers: this._headers(true) });
  },

  allUsers() {
    return this._req(`${API}?action=all_users`, { headers: this._headers(true) });
  },

  allInquiries() {
    return this._req(`${API}?action=all_inquiries`, { headers: this._headers(true) });
  },

  submitInquiry(name, email, subject, message) {
    return this._req(`${API}?action=inquiry`, {
      method:  'POST',
      headers: this._headers(),
      body:    JSON.stringify({ name, email, subject, message }),
    });
  },
};

/* ════════════════════════════════════════════════════
   UTILITIES
════════════════════════════════════════════════════ */
function esc(str) {
  return String(str ?? '')
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
}

function fmt(price) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', minimumFractionDigits: 0 }).format(price);
}

function fmtDate(d) {
  // Parse YYYY-MM-DD without timezone shift by splitting the string
  const [year, month, day] = d.split('-').map(Number);
  return new Date(year, month - 1, day).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}

function stars(rating, max = 5) {
  const n = Math.round(Number(rating));
  return '★'.repeat(n) + '☆'.repeat(max - n);
}

let _toastTimer = null;
function toast(msg, type = 'default') {
  clearTimeout(_toastTimer);
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = `toast toast--${type}`;
  el.classList.remove('d-none');
  _toastTimer = setTimeout(() => el.classList.add('d-none'), 3500);
}

function setLoading(btnEl, loading, text) {
  if (!btnEl) return;
  btnEl.disabled = loading;
  if (text) btnEl.textContent = loading ? `${text}…` : text;
}

function showAlert(el, msg, type = 'error') {
  if (!el) return;
  el.textContent = msg;
  el.className   = `form-alert form-alert--${type}`;
  el.classList.remove('d-none');
}

function hideAlert(el) {
  if (el) el.classList.add('d-none');
}

/* ════════════════════════════════════════════════════
   ROUTER
════════════════════════════════════════════════════ */
function navigate(viewName) {
  // Guards
  if (['dashboard', 'wishlist'].includes(viewName) && !state.user) {
    toast('Please log in first.', 'error');
    viewName = 'login';
  }
  if (viewName === 'admin' && state.user?.role !== 'admin') {
    toast('Admin access required.', 'error');
    viewName = state.user ? 'dashboard' : 'login';
  }
  if (viewName === 'login' && state.user)  { viewName = state.user.role === 'admin' ? 'admin' : 'dashboard'; }
  if (viewName === 'register' && state.user) { viewName = state.user.role === 'admin' ? 'admin' : 'dashboard'; }

  document.querySelectorAll('.view').forEach(v => v.classList.add('d-none'));
  const target = document.getElementById(`view-${viewName}`);
  if (target) target.classList.remove('d-none');

  document.getElementById('navLinks').classList.remove('is-open');
  window.scrollTo({ top: 0, behavior: 'smooth' });

  // Trigger data loading
  switch (viewName) {
    case 'destinations':   loadDestinations(); break;
    case 'dashboard':      loadDashboard();    break;
    case 'wishlist':       loadWishlistView(); break;
    case 'admin':          loadAdmin();        break;
  }
}

/* ════════════════════════════════════════════════════
   NAV UPDATE
════════════════════════════════════════════════════ */
function updateNav() {
  const loggedIn = Boolean(state.user);
  const isAdmin  = state.user?.role === 'admin';

  document.querySelectorAll('.nav-guest').forEach(el => el.classList.toggle('d-none', loggedIn));
  document.querySelectorAll('.nav-user').forEach(el  => el.classList.toggle('d-none', !(loggedIn && !isAdmin)));
  document.querySelectorAll('.nav-admin').forEach(el => el.classList.toggle('d-none', !isAdmin));
  document.querySelectorAll('.nav-auth').forEach(el  => el.classList.toggle('d-none', !loggedIn));

  const greet = document.getElementById('navGreeting');
  if (greet && state.user) greet.textContent = `Hi, ${state.user.name.split(' ')[0]}`;
}

/* ════════════════════════════════════════════════════
   DESTINATIONS VIEW
════════════════════════════════════════════════════ */
async function loadDestinations() {
  const grid  = document.getElementById('packagesGrid');
  const empty = document.getElementById('packagesEmpty');
  const label = document.getElementById('searchLabel');
  const pgn   = document.getElementById('pagination');

  grid.innerHTML  = Array(6).fill('<div class="skeleton-card"></div>').join('');
  empty.classList.add('d-none');
  if (pgn) pgn.innerHTML = '';

  try {
    const result = await api.getPackages(state.pkgSearch, state.pkgPage);
    state.packages = result.packages;

    // Show search label
    if (state.pkgSearch && label) {
      label.textContent = `Showing ${result.total} result${result.total !== 1 ? 's' : ''} for "${state.pkgSearch}"`;
      label.classList.remove('d-none');
    } else if (label) {
      label.classList.add('d-none');
    }

    grid.innerHTML = '';
    if (!result.packages.length) {
      empty.classList.remove('d-none');
      return;
    }
    result.packages.forEach(pkg => {
      grid.insertAdjacentHTML('beforeend', renderPkgCard(pkg));
    });

    // Pagination
    if (result.pages > 1 && pgn) {
      renderPagination(result.page, result.pages, pgn);
    }
  } catch (err) {
    grid.innerHTML = '';
    empty.classList.remove('d-none');
    toast('Could not load packages. Is the backend running?', 'error');
  }
}

function renderPkgCard(pkg) {
  const wishlisted = state.wishlistIds.has(Number(pkg.id));
  const avgStars   = pkg.avg_rating ? stars(pkg.avg_rating) : '';
  return `
    <article class="pkg-card">
      <div class="pkg-card__img-wrap">
        <img class="pkg-card__img" src="${esc(pkg.image_url)}" alt="${esc(pkg.title)}" loading="lazy"
             onerror="this.src='https://images.unsplash.com/photo-1488085061387-422e29b40080?w=800&q=80'" />
        <span class="pkg-card__badge">Featured</span>
        <button class="pkg-card__wishlist-btn ${wishlisted ? 'is-wishlisted' : ''}"
                title="${wishlisted ? 'Remove from wishlist' : 'Save to wishlist'}"
                onclick="handleWishlistToggle(${Number(pkg.id)}, this)">
          ${wishlisted ? '❤️' : '🤍'}
        </button>
      </div>
      <div class="pkg-card__body">
        <div class="pkg-card__meta">
          <span>📍 ${esc(pkg.location || '—')}</span>
          <span>🕒 ${esc(pkg.duration || '—')}</span>
          ${avgStars ? `<span class="pkg-card__stars">${avgStars}</span>` : ''}
        </div>
        <h3 class="pkg-card__title">${esc(pkg.title)}</h3>
        <p class="pkg-card__desc">${esc(pkg.description)}</p>
        <div class="pkg-card__footer">
          <div class="pkg-card__price">${fmt(pkg.price)} <span>/ person</span></div>
          <button class="btn btn--primary btn--sm" onclick="openPackageDetail(${Number(pkg.id)})">View Details</button>
        </div>
      </div>
    </article>
  `;
}

function renderPagination(current, total, container) {
  container.innerHTML = '';
  for (let i = 1; i <= total; i++) {
    const btn = document.createElement('button');
    btn.className = `page-btn${i === current ? ' page-btn--active' : ''}`;
    btn.textContent = i;
    btn.addEventListener('click', () => {
      state.pkgPage = i;
      loadDestinations();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
    container.appendChild(btn);
  }
}

async function openPackageDetail(id) {
  navigate('package-detail');
  const content = document.getElementById('pkgDetailContent');
  content.innerHTML = '<div class="loading-spinner" style="padding:4rem">Loading…</div>';

  try {
    const pkg = await api.getPackage(id);
    state.currentPkg = pkg;
    content.innerHTML = renderPkgDetail(pkg);
    initBookingCard(pkg);
  } catch (err) {
    content.innerHTML = `<div class="empty-state"><p>${esc(err.message)}</p></div>`;
  }
}
window.openPackageDetail = openPackageDetail;

function renderPkgDetail(pkg) {
  const dates = Array.isArray(pkg.available_dates) ? pkg.available_dates : [];
  const reviewsHtml = pkg.reviews && pkg.reviews.length
    ? pkg.reviews.map(r => `
        <div class="review-card">
          <div class="review-card__header">
            <span class="review-card__author">${esc(r.user_name)}</span>
            <span class="review-card__date">${new Date(r.created_at).toLocaleDateString()}</span>
          </div>
          <div class="review-card__stars">${stars(r.rating)}</div>
          <p class="review-card__text">${esc(r.comment)}</p>
        </div>`).join('')
    : '<p class="text-muted text-sm">No reviews yet. Be the first!</p>';

  return `
    <div class="pkg-detail-hero">
      <img src="${esc(pkg.image_url)}" alt="${esc(pkg.title)}"
           onerror="this.src='https://images.unsplash.com/photo-1488085061387-422e29b40080?w=800&q=80'" />
      <div class="pkg-detail-hero__overlay"></div>
      <div class="pkg-detail-hero__content">
        <h1>${esc(pkg.title)}</h1>
        <div class="pkg-detail-meta">
          <span class="pkg-detail-meta__item">📍 ${esc(pkg.location)}</span>
          <span class="pkg-detail-meta__item">🕒 ${esc(pkg.duration)}</span>
          <span class="pkg-detail-meta__item">👥 Max ${esc(pkg.max_persons)} persons</span>
          ${pkg.avg_rating > 0 ? `<span class="pkg-detail-meta__item">⭐ ${pkg.avg_rating} (${pkg.review_count} reviews)</span>` : ''}
        </div>
      </div>
    </div>

    <div class="container py-10">
      <div class="pkg-detail-grid">
        <!-- Left: description + reviews -->
        <div>
          <h2 style="font-size:1.5rem;font-weight:700;margin-bottom:1rem;">About This Package</h2>
          <p class="pkg-detail-desc">${esc(pkg.description)}</p>

          ${pkg.avg_rating > 0 ? `
          <div class="avg-rating">
            <span class="avg-rating__score">${pkg.avg_rating}</span>
            <span class="avg-rating__stars">${stars(pkg.avg_rating)}</span>
            <span class="avg-rating__count">${pkg.review_count} review${pkg.review_count !== 1 ? 's' : ''}</span>
          </div>` : ''}

          <div class="reviews-section">
            <h3>Traveller Reviews</h3>
            ${reviewsHtml}
          </div>

          <!-- Review form for logged-in users -->
          ${state.user && state.user.role !== 'admin' ? `
          <div class="review-form" id="reviewFormWrapper">
            <h4>Write a Review</h4>
            <div class="star-rating" id="starRating">
              ${[1,2,3,4,5].map(n => `<button type="button" class="star-btn" data-val="${n}" onclick="setReviewStar(${n})">★</button>`).join('')}
            </div>
            <textarea id="reviewComment" class="form-input form-textarea" rows="3" placeholder="Share your experience…"></textarea>
            <div class="form-alert d-none" id="reviewAlert"></div>
            <button class="btn btn--primary btn--sm mt-4" id="submitReviewBtn" onclick="submitReview(${Number(pkg.id)})">Submit Review</button>
          </div>` : ''}
        </div>

        <!-- Right: booking card -->
        <div>
          <div class="booking-card">
            <div class="booking-card__price">${fmt(pkg.price)} <span>/ person</span></div>
            <h4>Select Date</h4>
            ${dates.length ? `
            <div class="date-grid" id="dateGrid">
              ${dates.map(d => `<button type="button" class="date-btn" data-date="${esc(d)}" onclick="selectDate(this)">${fmtDate(d)}</button>`).join('')}
            </div>` : '<p class="text-sm text-muted mb-4">No fixed dates — contact us.</p>'}

            <div class="form-group">
              <label class="form-label" for="personCount">Number of Persons</label>
              <input type="number" id="personCount" class="form-input" value="1" min="1" max="${esc(pkg.max_persons)}" />
            </div>

            <div class="booking-total">
              <span>Total</span>
              <strong id="bookingTotal">${fmt(pkg.price)}</strong>
            </div>

            <button class="btn btn--primary btn--full mt-4" id="confirmBookingBtn" onclick="confirmBooking(${Number(pkg.id)})">
              ${state.user ? 'Confirm Booking' : 'Login to Book'}
            </button>
            ${state.user ? '' : '<p class="text-sm text-muted text-center mt-2">You need an account to book.</p>'}

            <button class="btn btn--outline btn--full mt-4" onclick="handleWishlistToggle(${Number(pkg.id)}, null, true)" id="detailWishlistBtn">
              ${state.wishlistIds.has(Number(pkg.id)) ? '❤️ Remove from Wishlist' : '🤍 Save to Wishlist'}
            </button>
          </div>
        </div>
      </div>
    </div>
  `;
}

let selectedDate = null;
let selectedStarVal = 0;

function initBookingCard(pkg) {
  selectedDate = null;

  const personInput = document.getElementById('personCount');
  const totalEl     = document.getElementById('bookingTotal');
  if (!personInput || !totalEl) return;

  function updateTotal() {
    const persons = Math.max(1, parseInt(personInput.value) || 1);
    totalEl.textContent = fmt(pkg.price * persons);
  }

  personInput.addEventListener('input', updateTotal);
}

window.selectDate = function(btn) {
  document.querySelectorAll('.date-btn').forEach(b => b.classList.remove('selected'));
  btn.classList.add('selected');
  selectedDate = btn.dataset.date;
};

window.setReviewStar = function(val) {
  selectedStarVal = val;
  document.querySelectorAll('.star-btn').forEach((btn, i) => {
    btn.classList.toggle('active', i < val);
  });
};

async function confirmBooking(pkgId) {
  if (!state.user) { navigate('login'); return; }

  const personInput = document.getElementById('personCount');
  const persons     = Math.max(1, parseInt(personInput?.value) || 1);
  const pkg         = state.currentPkg;

  if (!selectedDate && pkg.available_dates && pkg.available_dates.length > 0) {
    toast('Please select a travel date.', 'error'); return;
  }

  const MS_PER_DAY = 24 * 60 * 60 * 1000;
  const travelDate = selectedDate || new Date(Date.now() + 30 * MS_PER_DAY).toISOString().split('T')[0];

  const btn = document.getElementById('confirmBookingBtn');
  setLoading(btn, true, 'Confirm Booking');

  try {
    await api.book(pkgId, travelDate, persons);
    toast(`Booking confirmed! Check your dashboard for details.`, 'success');
    btn.textContent = '✅ Booked!';
    btn.disabled    = true;
  } catch (err) {
    toast(err.message, 'error');
    setLoading(btn, false, 'Confirm Booking');
  }
}
window.confirmBooking = confirmBooking;

async function submitReview(pkgId) {
  if (!state.user) { navigate('login'); return; }

  const comment   = document.getElementById('reviewComment')?.value.trim();
  const alertEl   = document.getElementById('reviewAlert');
  const submitBtn = document.getElementById('submitReviewBtn');

  if (!selectedStarVal) { showAlert(alertEl, 'Please select a star rating.'); return; }
  if (!comment)         { showAlert(alertEl, 'Please write a comment.');        return; }
  hideAlert(alertEl);

  setLoading(submitBtn, true, 'Submit Review');
  try {
    await api.submitReview(pkgId, selectedStarVal, comment);
    toast('Review submitted! Thank you.', 'success');
    // Refresh detail
    openPackageDetail(pkgId);
  } catch (err) {
    showAlert(alertEl, err.message);
    setLoading(submitBtn, false, 'Submit Review');
  }
}
window.submitReview = submitReview;

/* ════════════════════════════════════════════════════
   WISHLIST
════════════════════════════════════════════════════ */
async function loadWishlistIds() {
  if (!state.user || state.user.role === 'admin') return;
  try {
    const data = await api.myWishlist();
    state.wishlistIds = new Set(data.wishlist.map(w => Number(w.id)));
    // Update nav badge
    const badge = document.getElementById('navWishlistCount');
    if (badge) badge.textContent = state.wishlistIds.size || '';
  } catch { /* ignore */ }
}

async function handleWishlistToggle(pkgId, btnEl, fromDetail = false) {
  if (!state.user) { toast('Please log in to save packages.', 'error'); navigate('login'); return; }
  if (state.user.role === 'admin') { toast('Admins cannot use the wishlist.', 'warning'); return; }

  try {
    const data = await api.toggleWishlist(pkgId);
    if (data.wishlisted) {
      state.wishlistIds.add(pkgId);
      toast('Added to wishlist! ❤️', 'success');
    } else {
      state.wishlistIds.delete(pkgId);
      toast('Removed from wishlist.', 'default');
    }

    // Update badge
    const badge = document.getElementById('navWishlistCount');
    if (badge) badge.textContent = state.wishlistIds.size || '';

    // Update card button
    if (btnEl) {
      btnEl.classList.toggle('is-wishlisted', data.wishlisted);
      btnEl.textContent = data.wishlisted ? '❤️' : '🤍';
      btnEl.title       = data.wishlisted ? 'Remove from wishlist' : 'Save to wishlist';
    }

    // Update detail page button
    if (fromDetail) {
      const detailBtn = document.getElementById('detailWishlistBtn');
      if (detailBtn) detailBtn.textContent = data.wishlisted ? '❤️ Remove from Wishlist' : '🤍 Save to Wishlist';
    }
  } catch (err) {
    toast(err.message, 'error');
  }
}
window.handleWishlistToggle = handleWishlistToggle;

async function loadWishlistView() {
  const container = document.getElementById('wishlistGrid');
  if (!container) return;
  container.innerHTML = '<div class="loading-spinner">Loading…</div>';

  try {
    const data = await api.myWishlist();
    if (!data.wishlist.length) {
      container.innerHTML = `
        <div class="empty-state">
          <span class="empty-state__icon">🤍</span>
          <p>Your wishlist is empty.</p>
          <button class="btn btn--primary mt-4" data-view="destinations">Browse Packages</button>
        </div>`;
      return;
    }
    container.innerHTML = `<div class="packages-grid">${data.wishlist.map(pkg => renderPkgCard(pkg)).join('')}</div>`;
  } catch (err) {
    container.innerHTML = `<div class="empty-state"><p>${esc(err.message)}</p></div>`;
  }
}

/* ════════════════════════════════════════════════════
   USER DASHBOARD
════════════════════════════════════════════════════ */
async function loadDashboard() {
  const greet = document.getElementById('dashboardGreeting');
  if (greet && state.user) greet.textContent = `Welcome back, ${state.user.name}!`;

  loadBookingHistory();
  loadWishlistTab();
}

async function loadBookingHistory() {
  const container = document.getElementById('bookingsList');
  if (!container) return;
  container.innerHTML = '<div class="loading-spinner">Loading bookings…</div>';

  try {
    const data = await api.myBookings();
    if (!data.bookings.length) {
      container.innerHTML = `
        <div class="empty-state">
          <span class="empty-state__icon">📋</span>
          <p>You haven't made any bookings yet.</p>
          <button class="btn btn--primary mt-4" data-view="destinations">Browse Packages</button>
        </div>`;
      return;
    }
    container.innerHTML = data.bookings.map(b => `
      <div class="booking-history-card">
        <img src="${esc(b.image_url)}" alt="${esc(b.title)}"
             onerror="this.src='https://images.unsplash.com/photo-1488085061387-422e29b40080?w=800&q=80'" />
        <div class="booking-history-card__info">
          <div class="booking-history-card__title">${esc(b.title)}</div>
          <div class="booking-history-card__meta">
            <span>📍 ${esc(b.location)}</span>
            <span>📅 ${fmtDate(b.travel_date)}</span>
            <span>👥 ${esc(b.persons)} person${b.persons > 1 ? 's' : ''}</span>
            <span>🕒 ${esc(b.duration)}</span>
          </div>
          <div class="booking-history-card__footer">
            <span class="booking-history-card__price">${fmt(b.total_price)}</span>
            <span class="badge badge--${esc(b.status)}">${esc(b.status)}</span>
          </div>
        </div>
      </div>`).join('');
  } catch (err) {
    container.innerHTML = `<div class="empty-state"><p>${esc(err.message)}</p></div>`;
  }
}

async function loadWishlistTab() {
  const container = document.getElementById('wishlistContent');
  if (!container) return;
  container.innerHTML = '<div class="loading-spinner">Loading wishlist…</div>';

  try {
    const data = await api.myWishlist();
    if (!data.wishlist.length) {
      container.innerHTML = `
        <div class="empty-state">
          <span class="empty-state__icon">🤍</span>
          <p>No saved packages yet.</p>
          <button class="btn btn--primary mt-4" data-view="destinations">Explore Packages</button>
        </div>`;
      return;
    }
    container.innerHTML = `<div class="packages-grid">${data.wishlist.map(pkg => renderPkgCard(pkg)).join('')}</div>`;
  } catch (err) {
    container.innerHTML = `<div class="empty-state"><p>${esc(err.message)}</p></div>`;
  }
}

/* ════════════════════════════════════════════════════
   ADMIN PANEL
════════════════════════════════════════════════════ */
async function loadAdmin() {
  loadAdminStats();
  loadAdminPackages();
  loadAdminBookings();
  loadAdminUsers();
  loadAdminInquiries();
}

async function loadAdminStats() {
  try {
    const s = await api.adminStats();
    document.getElementById('stat-packages').textContent = s.packages;
    document.getElementById('stat-users').textContent    = s.users;
    document.getElementById('stat-bookings').textContent = s.bookings;
    document.getElementById('stat-revenue').textContent  = fmt(s.revenue);
  } catch { /* ignore */ }
}

async function loadAdminPackages() {
  const tbody = document.getElementById('adminPkgTbody');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">Loading…</td></tr>';

  try {
    const data = await api.getPackages('', 1);
    // Load all pages if needed — for demo just grab page 1
    if (!data.packages.length) {
      tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No packages found.</td></tr>';
      return;
    }
    tbody.innerHTML = data.packages.map(p => `
      <tr>
        <td>${Number(p.id)}</td>
        <td><img src="${esc(p.image_url)}" alt="${esc(p.title)}"
                 onerror="this.src='https://images.unsplash.com/photo-1488085061387-422e29b40080?w=200&q=60'" /></td>
        <td><strong>${esc(p.title)}</strong><br><span class="text-muted text-sm">${esc(p.location)}</span></td>
        <td>${esc(p.location)}</td>
        <td>${fmt(p.price)}</td>
        <td>${Array.isArray(p.available_dates) ? p.available_dates.length : 0} dates</td>
        <td>
          <div class="table-actions">
            <button class="btn btn--outline btn--sm" onclick="adminEditPackage(${Number(p.id)})">Edit</button>
            <button class="btn btn--danger btn--sm"  onclick="adminDeletePackage(${Number(p.id)}, '${esc(p.title)}')">Delete</button>
          </div>
        </td>
      </tr>`).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">${esc(err.message)}</td></tr>`;
  }
}

async function loadAdminBookings() {
  const tbody = document.getElementById('adminBookingsTbody');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Loading…</td></tr>';

  try {
    const data = await api.allBookings();
    if (!data.bookings.length) {
      tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No bookings yet.</td></tr>';
      return;
    }
    tbody.innerHTML = data.bookings.map(b => `
      <tr>
        <td>${Number(b.id)}</td>
        <td>${esc(b.user_name)}<br><span class="text-muted text-sm">${esc(b.user_email)}</span></td>
        <td>${esc(b.package_title)}<br><span class="text-muted text-sm">${esc(b.location)}</span></td>
        <td>${fmtDate(b.travel_date)}</td>
        <td>${esc(b.persons)}</td>
        <td>${fmt(b.total_price)}</td>
        <td><span class="badge badge--${esc(b.status)}">${esc(b.status)}</span></td>
        <td>
          <select class="form-input" style="padding:.25rem .5rem;font-size:.78rem"
                  onchange="adminUpdateBooking(${Number(b.id)}, this.value)">
            <option value="pending"   ${b.status==='pending'   ?'selected':''}>Pending</option>
            <option value="confirmed" ${b.status==='confirmed' ?'selected':''}>Confirmed</option>
            <option value="cancelled" ${b.status==='cancelled' ?'selected':''}>Cancelled</option>
          </select>
        </td>
      </tr>`).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted">${esc(err.message)}</td></tr>`;
  }
}

async function loadAdminUsers() {
  const tbody = document.getElementById('adminUsersTbody');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Loading…</td></tr>';

  try {
    const data = await api.allUsers();
    tbody.innerHTML = data.users.map(u => `
      <tr>
        <td>${Number(u.id)}</td>
        <td>${esc(u.name)}</td>
        <td>${esc(u.email)}</td>
        <td><span class="badge badge--${esc(u.role)}">${esc(u.role)}</span></td>
        <td>${new Date(u.created_at).toLocaleDateString()}</td>
      </tr>`).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">${esc(err.message)}</td></tr>`;
  }
}

async function loadAdminInquiries() {
  const tbody = document.getElementById('adminInquiriesTbody');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Loading…</td></tr>';

  try {
    const data = await api.allInquiries();
    if (!data.inquiries.length) {
      tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No inquiries yet.</td></tr>';
      return;
    }
    tbody.innerHTML = data.inquiries.map(i => `
      <tr>
        <td>${Number(i.id)}</td>
        <td>${esc(i.name)}</td>
        <td>${esc(i.email)}</td>
        <td>${esc(i.subject)}</td>
        <td>${new Date(i.created_at).toLocaleDateString()}</td>
        <td><span class="badge badge--${i.status==='new'?'pending':'confirmed'}">${esc(i.status)}</span></td>
      </tr>`).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">${esc(err.message)}</td></tr>`;
  }
}

// Admin: update booking status
window.adminUpdateBooking = async function(id, status) {
  try {
    await api.updateBookingStatus(id, status);
    toast('Booking status updated.', 'success');
    loadAdminBookings();
    loadAdminStats();
  } catch (err) {
    toast(err.message, 'error');
  }
};

// Admin: edit package (pre-fill modal)
window.adminEditPackage = async function(id) {
  try {
    const pkg = await api.getPackage(id);
    document.getElementById('pkgModalTitle').textContent = 'Edit Package';
    document.getElementById('pkgFormId').value        = pkg.id;
    document.getElementById('pkgTitle').value         = pkg.title;
    document.getElementById('pkgLocation').value      = pkg.location;
    document.getElementById('pkgDescription').value   = pkg.description;
    document.getElementById('pkgPrice').value         = pkg.price;
    document.getElementById('pkgDuration').value      = pkg.duration;
    document.getElementById('pkgMaxPersons').value    = pkg.max_persons;
    document.getElementById('pkgImageUrl').value      = pkg.image_url;
    document.getElementById('pkgDates').value         = Array.isArray(pkg.available_dates) ? pkg.available_dates.join(', ') : '';
    document.getElementById('pkgFormSubmit').textContent = 'Update Package';
    openPkgModal();
  } catch (err) {
    toast(err.message, 'error');
  }
};

// Admin: delete package
window.adminDeletePackage = async function(id, title) {
  if (!confirm(`Delete "${title}"? This cannot be undone.`)) return;
  try {
    await api.deletePackage(id);
    toast('Package deleted.', 'success');
    loadAdminPackages();
    loadAdminStats();
  } catch (err) {
    toast(err.message, 'error');
  }
};

/* ════════════════════════════════════════════════════
   PACKAGE MODAL
════════════════════════════════════════════════════ */
function openPkgModal() {
  document.getElementById('pkgModal').classList.remove('d-none');
  document.getElementById('pkgTitle').focus();
}

function closePkgModal() {
  document.getElementById('pkgModal').classList.add('d-none');
  document.getElementById('pkgForm').reset();
  document.getElementById('pkgFormId').value = '';
  document.getElementById('pkgModalTitle').textContent = 'Add Package';
  document.getElementById('pkgFormSubmit').textContent = 'Save Package';
  hideAlert(document.getElementById('pkgFormAlert'));
}

document.getElementById('addPackageBtn')?.addEventListener('click', () => {
  closePkgModal(); // reset first
  openPkgModal();
});
document.getElementById('pkgModalClose')?.addEventListener('click', closePkgModal);
document.getElementById('pkgFormCancel')?.addEventListener('click', closePkgModal);
document.getElementById('pkgModal')?.addEventListener('click', e => {
  if (e.target === document.getElementById('pkgModal')) closePkgModal();
});

document.getElementById('pkgForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const alertEl = document.getElementById('pkgFormAlert');
  const submitBtn = document.getElementById('pkgFormSubmit');
  hideAlert(alertEl);

  const id           = document.getElementById('pkgFormId').value;
  const title        = document.getElementById('pkgTitle').value.trim();
  const location     = document.getElementById('pkgLocation').value.trim();
  const description  = document.getElementById('pkgDescription').value.trim();
  const price        = parseFloat(document.getElementById('pkgPrice').value);
  const duration     = document.getElementById('pkgDuration').value.trim();
  const max_persons  = parseInt(document.getElementById('pkgMaxPersons').value) || 10;
  const image_url    = document.getElementById('pkgImageUrl').value.trim();
  const datesRaw     = document.getElementById('pkgDates').value.trim();
  const available_dates = datesRaw
    ? datesRaw.split(',').map(d => d.trim()).filter(d => /^\d{4}-\d{2}-\d{2}$/.test(d))
    : [];

  if (!title || !description || !price || !image_url) {
    showAlert(alertEl, 'Please fill all required fields.');
    return;
  }

  const data = { title, location, description, price, duration, max_persons, image_url, available_dates };

  setLoading(submitBtn, true, submitBtn.textContent);
  try {
    if (id) {
      await api.updatePackage(id, data);
      toast('Package updated!', 'success');
    } else {
      await api.createPackage(data);
      toast('Package created!', 'success');
    }
    closePkgModal();
    loadAdminPackages();
    loadAdminStats();
  } catch (err) {
    showAlert(alertEl, err.message);
  } finally {
    setLoading(submitBtn, false, id ? 'Update Package' : 'Save Package');
  }
});

/* ════════════════════════════════════════════════════
   AUTH FORMS
════════════════════════════════════════════════════ */

// Register
document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const alertEl  = document.getElementById('registerAlert');
  const submitBtn = document.getElementById('registerSubmit');
  hideAlert(alertEl);

  const name     = document.getElementById('regName').value.trim();
  const email    = document.getElementById('regEmail').value.trim();
  const password = document.getElementById('regPassword').value;
  const confirm  = document.getElementById('regPasswordConfirm').value;

  if (!name || !email || !password) { showAlert(alertEl, 'All fields are required.'); return; }
  if (password.length < 6)          { showAlert(alertEl, 'Password must be at least 6 characters.'); return; }
  if (password !== confirm)          { showAlert(alertEl, 'Passwords do not match.'); return; }

  setLoading(submitBtn, true, 'Create Account');
  try {
    const data = await api.register(name, email, password);
    saveSession(data.user, data.token);
    updateNav();
    await loadWishlistIds();
    toast(`Welcome, ${data.user.name}! 🎉`, 'success');
    navigate('dashboard');
  } catch (err) {
    showAlert(alertEl, err.message);
  } finally {
    setLoading(submitBtn, false, 'Create Account');
  }
});

// Login
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const alertEl  = document.getElementById('loginAlert');
  const submitBtn = document.getElementById('loginSubmit');
  hideAlert(alertEl);

  const email    = document.getElementById('loginEmail').value.trim();
  const password = document.getElementById('loginPassword').value;

  if (!email || !password) { showAlert(alertEl, 'Email and password are required.'); return; }

  setLoading(submitBtn, true, 'Sign In');
  try {
    const data = await api.login(email, password);
    saveSession(data.user, data.token);
    updateNav();
    await loadWishlistIds();
    toast(`Welcome back, ${data.user.name}!`, 'success');
    navigate(data.user.role === 'admin' ? 'admin' : 'dashboard');
  } catch (err) {
    showAlert(alertEl, err.message);
  } finally {
    setLoading(submitBtn, false, 'Sign In');
  }
});

// Logout
document.getElementById('logoutBtn')?.addEventListener('click', async () => {
  try { await api.logout(); } catch { /* ignore */ }
  clearSession();
  state.wishlistIds.clear();
  updateNav();
  toast('You have been logged out.', 'default');
  navigate('home');
});

// Password toggle (works for any input-group button with data-target)
document.addEventListener('click', (e) => {
  const btn = e.target.closest('.input-group__toggle');
  if (!btn) return;
  const targetId = btn.dataset.target;
  if (!targetId) return;
  const input = document.getElementById(targetId);
  if (!input) return;
  const isPass = input.type === 'password';
  input.type   = isPass ? 'text' : 'password';
  btn.textContent = isPass ? '🙈' : '👁';
});

// Password strength indicator
document.getElementById('regPassword')?.addEventListener('input', function() {
  const bar = document.getElementById('regPasswordStrength');
  if (!bar) return;
  const len = this.value.length;
  bar.className = 'password-strength ' + (len === 0 ? '' : len < 6 ? 'weak' : len < 10 ? 'medium' : 'strong');
});

/* ════════════════════════════════════════════════════
   CONTACT FORM
════════════════════════════════════════════════════ */
document.getElementById('contactForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const alertEl  = document.getElementById('contactAlert');
  const submitBtn = e.target.querySelector('button[type=submit]');
  hideAlert(alertEl);

  const name    = document.getElementById('contactName').value.trim();
  const email   = document.getElementById('contactEmail').value.trim();
  const subject = document.getElementById('contactSubject').value.trim();
  const message = document.getElementById('contactMessage').value.trim();

  if (!name || !email || !subject || !message) { showAlert(alertEl, 'All fields are required.'); return; }

  setLoading(submitBtn, true, 'Send Message');
  try {
    await api.submitInquiry(name, email, subject, message);
    showAlert(alertEl, 'Message sent! We\'ll get back to you soon. ✅', 'success');
    e.target.reset();
  } catch (err) {
    showAlert(alertEl, err.message);
  } finally {
    setLoading(submitBtn, false, 'Send Message');
  }
});

/* ════════════════════════════════════════════════════
   HERO SEARCH
════════════════════════════════════════════════════ */
document.getElementById('heroSearchBtn')?.addEventListener('click', () => {
  const val = document.getElementById('heroSearch').value.trim();
  if (val) {
    state.pkgSearch = val;
    state.pkgPage   = 1;
    navigate('destinations');
    document.getElementById('pkgSearchInput').value = val;
  } else {
    navigate('destinations');
  }
});

document.getElementById('heroSearch')?.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') document.getElementById('heroSearchBtn').click();
});

/* ════════════════════════════════════════════════════
   POPULAR DESTINATIONS (HOME)
════════════════════════════════════════════════════ */
document.getElementById('popularSearch')?.addEventListener('input', (e) => {
  popularState.search = e.target.value.trim();
  renderPopularDestinations();
});

document.getElementById('popularRegionFilters')?.addEventListener('click', (e) => {
  const btn = e.target.closest('[data-popular-category]');
  if (!btn) return;
  popularState.region = btn.dataset.popularCategory;
  document.querySelectorAll('[data-popular-category]').forEach(b => b.classList.toggle('chip--active', b === btn));
  renderPopularDestinations();
});

document.getElementById('popularThemeFilters')?.addEventListener('click', (e) => {
  const btn = e.target.closest('[data-popular-theme]');
  if (!btn) return;
  const newTheme = btn.dataset.popularTheme;
  popularState.theme = popularState.theme === newTheme ? '' : newTheme;
  document.querySelectorAll('[data-popular-theme]').forEach(b => b.classList.toggle('chip--active', b.dataset.popularTheme === popularState.theme));
  renderPopularDestinations();
});

renderPopularDestinations();

/* ════════════════════════════════════════════════════
   DESTINATION SEARCH
════════════════════════════════════════════════════ */
document.getElementById('pkgSearchBtn')?.addEventListener('click', () => {
  const val = document.getElementById('pkgSearchInput').value.trim();
  state.pkgSearch = val;
  state.pkgPage   = 1;
  document.getElementById('pkgSearchClear').classList.toggle('d-none', !val);
  loadDestinations();
});

document.getElementById('pkgSearchInput')?.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') document.getElementById('pkgSearchBtn').click();
});

document.getElementById('pkgSearchClear')?.addEventListener('click', () => {
  document.getElementById('pkgSearchInput').value = '';
  state.pkgSearch = '';
  state.pkgPage   = 1;
  document.getElementById('pkgSearchClear').classList.add('d-none');
  loadDestinations();
});

document.getElementById('clearSearchBtn')?.addEventListener('click', () => {
  document.getElementById('pkgSearchInput').value = '';
  state.pkgSearch = '';
  state.pkgPage   = 1;
  document.getElementById('pkgSearchClear').classList.add('d-none');
  loadDestinations();
});

/* ════════════════════════════════════════════════════
   TABS
════════════════════════════════════════════════════ */
document.addEventListener('click', (e) => {
  const btn = e.target.closest('.tab-btn');
  if (!btn) return;
  const tabId = btn.dataset.tab;
  if (!tabId) return;

  const parent = btn.closest('.tabs');
  if (!parent) return;

  // Deactivate all tabs in the same group
  parent.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('tab-btn--active'));
  btn.classList.add('tab-btn--active');

  // Find and toggle content panels
  // Look for siblings within the same container (next .tab-content elements)
  const container = parent.parentElement;
  container.querySelectorAll('.tab-content').forEach(tc => {
    tc.classList.toggle('d-none', tc.id !== tabId);
  });
});

/* ════════════════════════════════════════════════════
   GLOBAL CLICK DELEGATION (data-view)
════════════════════════════════════════════════════ */
document.addEventListener('click', (e) => {
  const trigger = e.target.closest('[data-view]');
  if (!trigger) return;
  e.preventDefault();
  navigate(trigger.dataset.view);
});

/* ════════════════════════════════════════════════════
   HAMBURGER
════════════════════════════════════════════════════ */
document.getElementById('navToggle')?.addEventListener('click', () => {
  document.getElementById('navLinks').classList.toggle('is-open');
});

/* ════════════════════════════════════════════════════
   KEYBOARD: close modal with Escape
════════════════════════════════════════════════════ */
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') closePkgModal();
});

/* ════════════════════════════════════════════════════
   INIT
════════════════════════════════════════════════════ */
(async function init() {
  loadSession();
  updateNav();
  if (state.user) await loadWishlistIds();
  navigate('home');
})();
