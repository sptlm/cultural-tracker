<#import "/layout.ftlh" as layout>
<@layout.page title="Администрирование">
<main class="page-shell">
    <div class="container">
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-4">
            <div>
                <h1 class="mb-1">Администрирование</h1>
                <p class="muted mb-0">Управление афишей, площадками, маршрутами и справочниками.</p>
            </div>
            <div class="d-flex gap-2">
                <a class="btn btn-outline-primary" href="${contextPath}/admin/events/new">Добавить мероприятие</a>
                <a class="btn btn-primary" href="${contextPath}/admin/venues/new">Добавить площадку</a>
            </div>
        </div>

        <ul class="nav nav-tabs admin-tabs" id="adminTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="events-tab" data-bs-toggle="tab" data-bs-target="#events-pane" type="button" role="tab">Мероприятия</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="venues-tab" data-bs-toggle="tab" data-bs-target="#venues-pane" type="button" role="tab">Площадки</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="reviews-tab" data-bs-toggle="tab" data-bs-target="#reviews-pane" type="button" role="tab">Отзывы</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="routes-tab" data-bs-toggle="tab" data-bs-target="#routes-pane" type="button" role="tab">Маршруты</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="categories-tab" data-bs-toggle="tab" data-bs-target="#categories-pane" type="button" role="tab">Категории</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="districts-tab" data-bs-toggle="tab" data-bs-target="#districts-pane" type="button" role="tab">Районы</button>
            </li>
        </ul>

        <div class="tab-content bg-white border border-top-0 rounded-bottom shadow-sm p-4">
            <section class="tab-pane fade show active" id="events-pane" role="tabpanel" aria-labelledby="events-tab">
                <div class="admin-section-head">
                    <div>
                        <h2 class="h4 mb-1">Мероприятия</h2>
                        <p class="muted mb-0">${events?size} записей</p>
                    </div>
                    <input class="form-control admin-filter-input" type="search" placeholder="Поиск по названию, площадке, времени" data-admin-filter data-admin-filter-target="#events-pane [data-admin-row]">
                </div>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead><tr><th>Название</th><th>Время</th><th>Площадка</th><th>Район</th><th>Рейтинг</th><th></th></tr></thead>
                        <tbody>
                        <#list events as event>
                            <tr data-admin-row data-filter-text="${event.title()} ${event.timeLabel()} ${event.venueName()} ${event.districtName()} ${event.ratingLabel()}">
                                <td><a href="${contextPath}/events/${event.id()}">${event.title()}</a></td>
                                <td>${event.timeLabel()}</td>
                                <td><a href="${contextPath}/venues/${event.venueId()}">${event.venueName()}</a></td>
                                <td>${event.districtName()}</td>
                                <td><#if event.reviewsCount() == 0>Нет оценок<#else>${event.ratingLabel()}</#if></td>
                                <td class="text-end"><a class="btn btn-sm btn-outline-secondary" href="${contextPath}/admin/events/${event.id()}/edit">Править</a></td>
                            </tr>
                        <#else>
                            <tr><td colspan="6" class="muted">Мероприятий пока нет.</td></tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="tab-pane fade" id="venues-pane" role="tabpanel" aria-labelledby="venues-tab">
                <div class="admin-section-head">
                    <div>
                        <h2 class="h4 mb-1">Площадки</h2>
                        <p class="muted mb-0">${venues?size} записей</p>
                    </div>
                    <input class="form-control admin-filter-input" type="search" placeholder="Поиск по названию, адресу, району" data-admin-filter data-admin-filter-target="#venues-pane [data-admin-row]">
                </div>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead><tr><th>Название</th><th>Адрес</th><th>Район</th><th>Координаты</th><th></th></tr></thead>
                        <tbody>
                        <#list venues as venue>
                            <#assign districtName = (venue.cityDistrict.name)!"Не указан">
                            <#assign coords><#if venue.latitude?? && venue.longitude??>${venue.latitude}, ${venue.longitude}<#else>Не указаны</#if></#assign>
                            <tr data-admin-row data-filter-text="${venue.name} ${venue.address} ${districtName} ${coords}">
                                <td><a href="${contextPath}/venues/${venue.id}">${venue.name}</a></td>
                                <td>${venue.address}</td>
                                <td>${districtName}</td>
                                <td>${coords}</td>
                                <td class="text-end"><a class="btn btn-sm btn-outline-secondary" href="${contextPath}/admin/venues/${venue.id}/edit">Править</a></td>
                            </tr>
                        <#else>
                            <tr><td colspan="5" class="muted">Площадок пока нет.</td></tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="tab-pane fade" id="reviews-pane" role="tabpanel" aria-labelledby="reviews-tab">
                <div class="admin-section-head">
                    <div>
                        <h2 class="h4 mb-1">Отзывы</h2>
                        <p class="muted mb-0">${reviews?size} записей</p>
                    </div>
                    <input class="form-control admin-filter-input" type="search" placeholder="Поиск по автору, мероприятию, тексту" data-admin-filter data-admin-filter-target="#reviews-pane [data-admin-row]">
                </div>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead><tr><th>Автор</th><th>Мероприятие</th><th>Оценка</th><th>Текст</th><th></th></tr></thead>
                        <tbody>
                        <#list reviews as review>
                            <tr data-admin-row data-filter-text="${review.author} ${review.eventTitle} ${review.rating} ${review.text}">
                                <td>${review.author}</td>
                                <td><a href="${contextPath}/events/${review.eventId}">${review.eventTitle}</a></td>
                                <td>${review.rating}/5</td>
                                <td>${review.text}</td>
                                <td class="text-end">
                                    <form method="post" action="${contextPath}/admin/reviews/${review.id}/delete">
                                        <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                                        <button class="btn btn-sm btn-outline-danger" type="submit">Удалить</button>
                                    </form>
                                </td>
                            </tr>
                        <#else>
                            <tr><td colspan="5" class="muted">Отзывов пока нет.</td></tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="tab-pane fade" id="routes-pane" role="tabpanel" aria-labelledby="routes-tab">
                <div class="admin-section-head">
                    <div>
                        <h2 class="h4 mb-1">Маршруты</h2>
                        <p class="muted mb-0">${routes?size} записей</p>
                    </div>
                    <input class="form-control admin-filter-input" type="search" placeholder="Поиск по названию, автору, статусу" data-admin-filter data-admin-filter-target="#routes-pane [data-admin-row]">
                </div>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead><tr><th>Название</th><th>Автор</th><th>Статус</th><th>Состав</th><th></th></tr></thead>
                        <tbody>
                        <#list routes as route>
                            <#assign routeStatus = route.isPublic()?then("Публичный", "Личный")>
                            <tr data-admin-row data-filter-text="${route.title()} ${route.description()} ${route.authorUsername()} ${routeStatus}">
                                <td><a href="${contextPath}/routes/${route.id()}">${route.title()}</a></td>
                                <td>${route.authorUsername()}</td>
                                <td>${routeStatus}</td>
                                <td>${route.events()?size} точек · ${route.durationMinutes()} мин · ${route.budget()} ₽<#if route.distanceKm()?? && route.distanceKm() gt 0> · ${route.distanceKm()} км</#if></td>
                                <td class="text-end">
                                    <div class="d-flex justify-content-end gap-2">
                                        <a class="btn btn-sm btn-outline-secondary" href="${contextPath}/routes/${route.id()}/edit">Править</a>
                                        <form method="post" action="${contextPath}/admin/routes/${route.id()}/delete">
                                            <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                                            <button class="btn btn-sm btn-outline-danger" type="submit">Удалить</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <#else>
                            <tr><td colspan="5" class="muted">Маршрутов пока нет.</td></tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="tab-pane fade" id="categories-pane" role="tabpanel" aria-labelledby="categories-tab">
                <div class="admin-section-head">
                    <div>
                        <h2 class="h4 mb-1">Категории</h2>
                        <p class="muted mb-0">${categories?size} записей</p>
                    </div>
                    <form class="d-flex gap-2 admin-inline-form" method="post" action="${contextPath}/admin/categories">
                        <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                        <input class="form-control" name="name" placeholder="Новая категория" required>
                        <button class="btn btn-primary" type="submit">Добавить</button>
                    </form>
                </div>
                <div class="d-grid gap-2">
                    <#list categories as category>
                        <div class="admin-category-row">
                            <form class="d-flex gap-2" method="post" action="${contextPath}/admin/categories/${category.id}">
                                <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                                <input class="form-control form-control-sm" name="name" value="${category.name}" required>
                                <button class="btn btn-sm btn-outline-secondary" type="submit">Сохранить</button>
                            </form>
                            <form method="post" action="${contextPath}/admin/categories/${category.id}/delete">
                                <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                                <button class="btn btn-sm btn-outline-danger" type="submit">Удалить</button>
                            </form>
                        </div>
                    <#else>
                        <p class="muted mb-0">Категорий пока нет.</p>
                    </#list>
                </div>
            </section>

            <section class="tab-pane fade" id="districts-pane" role="tabpanel" aria-labelledby="districts-tab">
                <div class="admin-section-head">
                    <div>
                        <h2 class="h4 mb-1">Районы</h2>
                        <p class="muted mb-0">${districts?size} записей</p>
                    </div>
                    <form class="d-flex gap-2 admin-inline-form" method="post" action="${contextPath}/admin/districts">
                        <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                        <input class="form-control" name="name" placeholder="Новый район" required>
                        <button class="btn btn-primary" type="submit">Добавить</button>
                    </form>
                </div>
                <div class="d-grid gap-2">
                    <#list districts as district>
                        <div class="admin-category-row">
                            <form class="d-flex gap-2" method="post" action="${contextPath}/admin/districts/${district.id}">
                                <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                                <input class="form-control form-control-sm" name="name" value="${district.name}" required>
                                <button class="btn btn-sm btn-outline-secondary" type="submit">Сохранить</button>
                            </form>
                            <form method="post" action="${contextPath}/admin/districts/${district.id}/delete">
                                <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                                <button class="btn btn-sm btn-outline-danger" type="submit">Удалить</button>
                            </form>
                        </div>
                    <#else>
                        <p class="muted mb-0">Районов пока нет.</p>
                    </#list>
                </div>
            </section>
        </div>
    </div>
</main>
</@layout.page>
