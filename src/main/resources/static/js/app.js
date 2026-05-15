// Сервер рендерит этот JSON в layout.ftlh из application.yml.
// Так фронт использует те же пороги, что и Java-код: расстояние "рядом",
// порог высокого рейтинга, радиус Земли для haversine и длительность toast.
const APP_CONFIG = readAppConfig();

document.addEventListener('DOMContentLoaded', () => {
    // Spring Security кладет CSRF-токен в meta-теги общего layout.
    // Любой AJAX-запрос с изменением данных должен отправлять этот заголовок,
    // иначе сервер вернет 403.
    const csrfHeaders = () => {
        const token = document.querySelector('meta[name="_csrf"]')?.content;
        const header = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
        return token ? {[header]: token} : {};
    };

    // Проверка уникальности username/email при регистрации.
    // Поле само хранит endpoint в data-check-field, поэтому один код работает
    // и для имени пользователя, и для email.
    document.querySelectorAll('[data-check-field]').forEach((field) => {
        const target = document.querySelector(field.dataset.checkTarget);
        field.addEventListener('input', async () => {
            if (!field.value.trim() || !target) {
                return;
            }
            try {
                const payload = await requestJson(`${field.dataset.checkField}?${field.name}=${encodeURIComponent(field.value)}`);
                target.textContent = payload.available ? 'Свободно' : 'Уже занято';
                target.className = payload.available ? 'text-success small' : 'text-danger small';
            } catch (error) {
                target.textContent = error.message || 'Не удалось проверить поле';
                target.className = 'text-danger small';
            }
        });
    });

    // Добавление события в избранное работает без перезагрузки страницы.
    // Если пользователь не авторизован, вместо резкого redirect показываем
    // понятное предупреждение с кнопкой входа.
    document.querySelectorAll('.favorite-button').forEach((button) => {
        button.addEventListener('click', async () => {
            button.disabled = true;
            try {
                const payload = await requestJson(`/api/favorites/events/${button.dataset.eventId}`, {
                    method: 'POST',
                    headers: {'X-Requested-With': 'XMLHttpRequest', ...csrfHeaders()}
                });
                button.dataset.favorite = String(payload.favorite);
                button.textContent = payload.favorite ? 'В избранном' : 'В избранное';
                showNotice(payload.favorite ? 'Событие добавлено в избранное' : 'Событие убрано из избранного', 'success');
            } catch (error) {
                if (error.status === 401 || error.status === 403) {
                    showNotice('Войдите, чтобы сохранить событие в избранное.', 'warning', {label: 'Войти', href: '/login'});
                } else {
                    showNotice(error.message || 'Не удалось обновить избранное', 'danger');
                }
            } finally {
                button.disabled = false;
            }
        });
    });

    const filterForm = document.querySelector('[data-event-filter]');
    const eventsTarget = document.querySelector('[data-events-target]');
    if (filterForm && eventsTarget) {
        // Фильтры событий отправляются через AJAX. Сервер возвращает JSON,
        // а карточки перерисовываются на клиенте тем же визуальным форматом,
        // что и серверный FreeMarker-макрос.
        filterForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const params = new URLSearchParams(new FormData(filterForm));
            const submitButton = filterForm.querySelector('[type="submit"]');
            setBusy(submitButton, true);
            try {
                const events = await requestJson(`/api/events/search?${params}`, {headers: {'Accept': 'application/json'}});
                eventsTarget.innerHTML = events.length
                    ? events.map(renderEventCard).join('')
                    : '<p class="muted">По этим фильтрам ничего не найдено.</p>';
                history.replaceState(null, '', `/events?${params}`);
            } catch (error) {
                showNotice(error.message || 'Не удалось применить фильтры', 'danger');
            } finally {
                setBusy(submitButton, false);
            }
        });
    }

    initReviews(csrfHeaders);

    // Карточки событий/маршрутов/площадок являются ссылкой целиком.
    // Проверка event.target нужна, чтобы кнопки и формы внутри карточек
    // не превращались в случайный переход.
    document.addEventListener('click', (event) => {
        const card = event.target.closest('[data-card-href]');
        if (!card || event.target.closest('a, button, input, select, textarea, form')) {
            return;
        }
        window.location.href = card.dataset.cardHref;
    });

    initRouteBuilder();
    initAdminFilters();
});

// Рендер карточки события для AJAX-фильтрации. Серверные карточки выводятся
// через fragments.ftlh, но после AJAX нам нужен такой же HTML на клиенте.
function renderEventCard(event) {
    const price = Number(event.price) === 0 ? 'Бесплатно' : `${event.price} ₽`;
    const rating = Number(event.reviewsCount || 0) === 0 ? 'Нет оценок' : `Рейтинг: ${escapeHtml(event.ratingLabel || event.averageRating)}`;
    const distance = event.distanceKm == null ? '' : ` · ${event.distanceKm} км от вас`;
    const categories = (event.categories || []).map((category) => `<span class="badge text-bg-light">${escapeHtml(category)}</span>`).join(' ');
    const badges = [
        event.today ? '<span class="badge text-bg-primary">Сегодня</span>' : '',
        Number(event.price) === 0 ? '<span class="badge text-bg-success">Бесплатно</span>' : '',
        (event.nearby ?? (event.distanceKm != null && Number(event.distanceKm) <= APP_CONFIG.nearbyDistanceKm)) ? '<span class="badge text-bg-info">Рядом</span>' : '',
        (event.highRated ?? (event.averageRating != null && Number(event.averageRating) >= APP_CONFIG.highRatingThreshold)) ? '<span class="badge text-bg-warning">Высокий рейтинг</span>' : ''
    ].filter(Boolean).join('');
    return `
        <article class="col">
            <div class="card h-100 shadow-sm clickable-card" data-card-href="/events/${event.id}">
                ${event.imageUrl ? `<img class="event-img card-img-top" src="${escapeHtml(event.imageUrl)}" alt="${escapeHtml(event.title)}">` : ''}
                <div class="card-body">
                    ${badges ? `<div class="event-badges mb-2">${badges}</div>` : ''}
                    <div class="d-flex justify-content-between gap-2 mb-2">
                        <span class="rating-pill">${rating}</span>
                        <span class="fw-bold">${price}</span>
                    </div>
                    <h2 class="h5">${escapeHtml(event.title)}</h2>
                    <p class="small fw-semibold mb-2">${escapeHtml(event.timeLabel || event.startLabel || '')}</p>
                    <p class="muted">${escapeHtml(event.description || '')}</p>
                    <p class="small mb-2">${escapeHtml(event.venueName)} · ${escapeHtml(event.districtName)}${distance}</p>
                    <div class="d-flex flex-wrap gap-1">${categories}</div>
                </div>
            </div>
        </article>`;
}

// Конструктор маршрута отвечает сразу за несколько UX-сценариев:
// поиск события по списку, добавление точки, удаление точки, изменение порядка
// кнопками, drag-and-drop и живую сводку маршрута.
function initRouteBuilder() {
    const builder = document.querySelector('[data-route-builder]');
    if (!builder) {
        return;
    }

    const search = builder.querySelector('[data-route-event-search]');
    const options = [...builder.querySelectorAll('[data-route-event-options] [data-event-id]')];
    const selected = builder.querySelector('[data-route-selected]');
    const counter = builder.querySelector('[data-route-counter]');
    const durationInput = document.querySelector('input[name="durationMinutes"]');
    const budgetInput = document.querySelector('input[name="budget"]');

    // Источник истины для порядка маршрута — DOM. Каждый выбранный элемент
    // содержит hidden input name="eventIds", и браузер отправит их на сервер
    // ровно в текущем порядке элементов.
    const selectedItems = () => [...selected.querySelectorAll('.route-selected-item')];

    // Живая сводка пересчитывается после любого изменения маршрута:
    // добавления, удаления, перемещения точки, изменения бюджета или длительности.
    const updateSummary = () => {
        const items = selectedItems();
        const totalPrice = items.reduce((sum, item) => sum + numberFromData(item.dataset.price), 0);
        const estimatedDistance = estimateRouteDistance(items);
        const plannedDuration = Number(durationInput?.value || 0);
        const plannedBudget = Number(budgetInput?.value || 0);
        counter.textContent = String(items.length);
        items.forEach((item, index) => {
            const marker = item.querySelector('.route-step-number');
            if (marker) {
                marker.textContent = String(index + 1);
            }
        });
        setText('[data-route-summary-count]', String(items.length), builder);
        setText('[data-route-summary-price]', formatMoney(totalPrice), builder);
        setText('[data-route-summary-duration]', `${plannedDuration || 0} мин`, builder);
        setText('[data-route-summary-distance]', `${estimatedDistance.toFixed(1)} км`, builder);
        const budgetHint = builder.querySelector('[data-route-budget-hint]');
        if (budgetHint) {
            budgetHint.textContent = plannedBudget > 0 && totalPrice > plannedBudget
                ? `Билеты дороже заявленного бюджета на ${formatMoney(totalPrice - plannedBudget)}`
                : 'Бюджет выглядит реалистично для выбранных событий';
            budgetHint.className = plannedBudget > 0 && totalPrice > plannedBudget ? 'small mt-2 text-danger' : 'small mt-2 text-success';
        }
    };

    // В левой колонке скрываем события, которые уже добавлены в маршрут,
    // а также события, не подходящие под строку поиска.
    const refreshOptionVisibility = () => {
        const selectedIds = new Set([...selected.querySelectorAll('[data-event-id]')].map((item) => item.dataset.eventId));
        const query = (search.value || '').trim().toLowerCase();
        options.forEach((option) => {
            const matches = !query || option.dataset.search.toLowerCase().includes(query);
            option.classList.toggle('d-none', selectedIds.has(option.dataset.eventId) || !matches);
        });
        updateSummary();
    };

    // Клик по событию из левой колонки добавляет его в правую колонку
    // и сразу создает hidden input для будущей отправки формы.
    options.forEach((option) => {
        option.addEventListener('click', () => {
            selected.insertAdjacentHTML('beforeend', renderSelectedRouteEvent(option));
            refreshOptionVisibility();
        });
    });

    // Делегированный обработчик для кнопок "Убрать", "Вверх", "Вниз".
    // Делегирование нужно, потому что новые точки маршрута добавляются
    // динамически после загрузки страницы.
    selected.addEventListener('click', (event) => {
        const item = event.target.closest('.route-selected-item');
        if (!item) {
            return;
        }
        if (event.target.closest('[data-route-remove]')) {
            item.remove();
        }
        if (event.target.closest('[data-route-up]') && item.previousElementSibling) {
            selected.insertBefore(item, item.previousElementSibling);
        }
        if (event.target.closest('[data-route-down]') && item.nextElementSibling) {
            selected.insertBefore(item.nextElementSibling, item);
        }
        refreshOptionVisibility();
    });

    // dragstart только помечает текущий элемент. Само перемещение происходит
    // в dragover, где мы постоянно переставляем этот DOM-элемент относительно
    // соседних элементов.
    selected.addEventListener('dragstart', (event) => {
        const item = event.target.closest('.route-selected-item');
        if (!item) {
            return;
        }
        item.classList.add('is-dragging');
        event.dataTransfer.effectAllowed = 'move';
    });

    // Во время dragover определяем, перед каким элементом должна оказаться
    // перетаскиваемая точка. Алгоритм сравнивает курсор с серединой каждого
    // соседнего элемента: если курсор выше середины, вставляем перед ним.
    selected.addEventListener('dragover', (event) => {
        event.preventDefault();
        const dragging = selected.querySelector('.is-dragging');
        if (!dragging) {
            return;
        }
        const after = getDragAfterElement(selected, event.clientY);
        if (after == null) {
            selected.appendChild(dragging);
        } else {
            selected.insertBefore(dragging, after);
        }
    });

    // После завершения перетаскивания убираем визуальный класс и пересчитываем
    // номера шагов, цену, дистанцию и подсказку по бюджету.
    selected.addEventListener('dragend', (event) => {
        event.target.closest('.route-selected-item')?.classList.remove('is-dragging');
        refreshOptionVisibility();
    });

    search.addEventListener('input', refreshOptionVisibility);
    durationInput?.addEventListener('input', updateSummary);
    budgetInput?.addEventListener('input', updateSummary);
    refreshOptionVisibility();
}

// Создает HTML выбранной точки маршрута из кнопки события в левой колонке.
// В data-атрибуты дублируются цена и координаты, чтобы клиент мог мгновенно
// пересчитывать бюджет и примерную дистанцию без дополнительного запроса.
function renderSelectedRouteEvent(option) {
    const title = option.querySelector('.fw-semibold').textContent;
    const meta = option.querySelector('.small').textContent;
    return `
        <div class="route-selected-item" data-event-id="${option.dataset.eventId}" data-price="${option.dataset.price || 0}" data-lat="${option.dataset.lat || ''}" data-lon="${option.dataset.lon || ''}" draggable="true">
            <input type="hidden" name="eventIds" value="${option.dataset.eventId}">
            <span class="route-step-number"></span>
            <div>
                <div class="fw-semibold">${escapeHtml(title)}</div>
                <div class="small muted">${escapeHtml(meta)}</div>
            </div>
            <div class="route-selected-actions">
                <button class="btn btn-sm btn-outline-secondary" type="button" data-route-up>Вверх</button>
                <button class="btn btn-sm btn-outline-secondary" type="button" data-route-down>Вниз</button>
                <button class="btn btn-sm btn-outline-danger" type="button" data-route-remove>Убрать</button>
            </div>
        </div>`;
}

// Отзывы подгружаются отдельно через REST endpoint. Это позволяет обновлять
// список после отправки формы без полной перезагрузки страницы события.
async function initReviews(csrfHeaders) {
    const reviewsList = document.querySelector('[data-reviews-list]');
    if (!reviewsList) {
        return;
    }
    const eventId = reviewsList.dataset.eventId;
    const reviewForm = document.querySelector('[data-review-form]');
    const status = document.querySelector('[data-review-status]');

    await loadReviews(eventId, reviewsList);

    if (!reviewForm) {
        return;
    }

    reviewForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const form = new FormData(reviewForm);
        const submitButton = reviewForm.querySelector('[type="submit"]');
        setBusy(submitButton, true);
        setInlineStatus(status, '');
        try {
            await requestJson(`/api/events/${eventId}/reviews`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest', ...csrfHeaders()},
                body: JSON.stringify({rating: Number(form.get('rating')), text: form.get('text')})
            });
            reviewForm.reset();
            setInlineStatus(status, 'Отзыв сохранён', 'success');
            await loadReviews(eventId, reviewsList);
        } catch (error) {
            if (error.status === 401 || error.status === 403) {
                showNotice('Войдите, чтобы оставить отзыв.', 'warning', {label: 'Войти', href: '/login'});
            }
            setInlineStatus(status, error.message || 'Не удалось сохранить отзыв', 'danger');
        } finally {
            setBusy(submitButton, false);
        }
    });
}

// Загружает актуальный список отзывов с сервера и заменяет содержимое блока.
async function loadReviews(eventId, target) {
    target.setAttribute('aria-busy', 'true');
    try {
        const reviews = await requestJson(`/api/events/${eventId}/reviews`, {headers: {'Accept': 'application/json'}});
        target.innerHTML = reviews.length
            ? reviews.map(renderReview).join('')
            : '<p class="muted" data-reviews-empty>Отзывов пока нет.</p>';
    } catch (error) {
        target.insertAdjacentHTML('afterbegin', `<div class="alert alert-danger py-2">${escapeHtml(error.message || 'Не удалось загрузить отзывы')}</div>`);
    } finally {
        target.removeAttribute('aria-busy');
    }
}

// Один отзыв приходит как JSON-объект, поэтому перед вставкой в HTML все
// пользовательские значения экранируются через escapeHtml.
function renderReview(review) {
    const date = formatDate(review.createdAt);
    return `
        <article class="review-item border-top pt-3 mt-3">
            <div class="fw-bold">${escapeHtml(review.author)} · ${escapeHtml(review.rating)}/5</div>
            ${date ? `<div class="small muted mb-1">${date}</div>` : ''}
            <p class="mb-0">${escapeHtml(review.text)}</p>
        </article>`;
}

// Общая обертка над fetch: приводит успешный ответ к JSON/тексту, а ошибки
// превращает в AjaxError с понятным message. Благодаря этому все AJAX-блоки
// показывают пользователю одинаковые сообщения об ошибках.
async function requestJson(url, options = {}) {
    let response;
    try {
        response = await fetch(url, options);
    } catch (error) {
        throw new AjaxError('Сеть недоступна. Попробуйте ещё раз.', 0);
    }
    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : await response.text();
    if (!response.ok) {
        const message = typeof payload === 'object' && payload !== null
            ? payload.message || payload.error || 'Запрос завершился ошибкой'
            : payload || 'Запрос завершился ошибкой';
        throw new AjaxError(message, response.status, payload);
    }
    return payload;
}

class AjaxError extends Error {
    constructor(message, status, payload) {
        super(message);
        this.status = status;
        this.payload = payload;
    }
}

// Toast-уведомления создаются лениво: контейнер появляется только тогда,
// когда реально нужно что-то показать пользователю.
function showNotice(message, type = 'danger', action) {
    const host = document.querySelector('[data-notice-host]') || createNoticeHost();
    const actionHtml = action ? `<a class="btn btn-sm btn-outline-dark ms-2" href="${escapeHtml(action.href)}">${escapeHtml(action.label)}</a>` : '';
    const notice = document.createElement('div');
    notice.className = `alert alert-${type} shadow-sm d-flex align-items-center justify-content-between gap-3`;
    notice.setAttribute('role', 'alert');
    notice.innerHTML = `<span>${escapeHtml(message)}</span><span>${actionHtml}<button type="button" class="btn-close" aria-label="Закрыть"></button></span>`;
    notice.querySelector('.btn-close').addEventListener('click', () => notice.remove());
    host.appendChild(notice);
    setTimeout(() => notice.remove(), action ? APP_CONFIG.actionToastDurationMs : APP_CONFIG.toastDurationMs);
}

function createNoticeHost() {
    const host = document.createElement('div');
    host.dataset.noticeHost = '';
    host.className = 'notice-host';
    document.body.appendChild(host);
    return host;
}

function setBusy(button, busy) {
    if (!button) {
        return;
    }
    if (busy) {
        button.dataset.originalText = button.textContent;
        button.textContent = 'Подождите...';
        button.disabled = true;
    } else {
        button.textContent = button.dataset.originalText || button.textContent;
        button.disabled = false;
    }
}

// Статус рядом с формой отзыва: успешное сохранение или текст ошибки.
function setInlineStatus(target, message, type = 'muted') {
    if (!target) {
        return;
    }
    target.textContent = message;
    target.className = type === 'success' ? 'small text-success' : type === 'danger' ? 'small text-danger' : 'small muted';
}

function setText(selector, value, root = document) {
    const target = root.querySelector(selector);
    if (target) {
        target.textContent = value;
    }
}

function numberFromData(value) {
    const normalized = String(value || '0').replace(',', '.');
    const number = Number(normalized);
    return Number.isFinite(number) ? number : 0;
}

function formatMoney(value) {
    return `${Math.round(value * 100) / 100} ₽`;
}

// Предварительная длина маршрута на клиенте. Окончательный расчет все равно
// выполняется на сервере при сохранении, но этот быстрый расчет помогает
// пользователю понимать масштаб маршрута еще до отправки формы.
function estimateRouteDistance(items) {
    const points = items
        .map((item) => ({lat: numberFromData(item.dataset.lat), lon: numberFromData(item.dataset.lon)}))
        .filter((point) => point.lat && point.lon);
    if (points.length < APP_CONFIG.minRoutePoints) {
        return 0;
    }
    return points.slice(1).reduce((sum, point, index) => sum + haversine(points[index], point), 0);
}

// Haversine считает расстояние между двумя точками на сфере по широте/долготе.
// Радиус Земли приходит из application.yml через APP_CONFIG, чтобы серверный
// и клиентский расчеты использовали одинаковую настройку.
function haversine(from, to) {
    const dLat = toRadians(to.lat - from.lat);
    const dLon = toRadians(to.lon - from.lon);
    const lat1 = toRadians(from.lat);
    const lat2 = toRadians(to.lat);
    const a = Math.sin(dLat / 2) ** 2 + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) ** 2;
    const distance = 2 * APP_CONFIG.earthRadiusKm * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return roundDistance(distance);
}

function toRadians(value) {
    return value * Math.PI / 180;
}

// Находит элемент, перед которым надо вставить перетаскиваемую точку маршрута.
// Возвращает null, если курсор ниже всех элементов и нужно вставлять в конец.
function getDragAfterElement(container, y) {
    return [...container.querySelectorAll('.route-selected-item:not(.is-dragging)')]
        .reduce((closest, child) => {
            const box = child.getBoundingClientRect();
            const offset = y - box.top - box.height / 2;
            if (offset < 0 && offset > closest.offset) {
                return {offset, element: child};
            }
            return closest;
        }, {offset: Number.NEGATIVE_INFINITY, element: null}).element;
}

// LocalDateTime из Jackson иногда приходит массивом [year, month, day, ...],
// а иногда строкой. Поддерживаем оба варианта, чтобы UI не зависел от формата.
function formatDate(value) {
    if (!value) {
        return '';
    }
    const date = Array.isArray(value)
        ? new Date(value[0], value[1] - 1, value[2], value[3] || 0, value[4] || 0)
        : new Date(value);
    if (Number.isNaN(date.getTime())) {
        return '';
    }
    return date.toLocaleString('ru-RU', {day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'});
}

// Экранирование обязательно для HTML, который собирается на клиенте из JSON.
function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

// В админке фильтрация таблиц выполняется локально: каждая строка хранит
// data-filter-text, а input указывает CSS-селектор целевых строк.
function initAdminFilters() {
    document.querySelectorAll('[data-admin-filter]').forEach((input) => {
        const rows = [...document.querySelectorAll(input.dataset.adminFilterTarget || '')];
        const applyFilter = () => {
            const query = input.value.trim().toLowerCase();
            rows.forEach((row) => {
                const text = (row.dataset.filterText || row.textContent || '').toLowerCase();
                row.classList.toggle('d-none', Boolean(query) && !text.includes(query));
            });
        };
        input.addEventListener('input', applyFilter);
        applyFilter();
    });
}

function readAppConfig() {
    const fallback = {
        nearbyDistanceKm: 5,
        highRatingThreshold: 4.5,
        earthRadiusKm: 6371,
        distanceScale: 1,
        minRoutePoints: 2,
        toastDurationMs: 4500,
        actionToastDurationMs: 8000
    };
    const node = document.getElementById('app-config');
    if (!node) {
        return fallback;
    }
    try {
        return {...fallback, ...JSON.parse(node.textContent || '{}')};
    } catch (error) {
        return fallback;
    }
}

function roundDistance(distanceKm) {
    const multiplier = 10 ** APP_CONFIG.distanceScale;
    return Math.round(distanceKm * multiplier) / multiplier;
}
