<#import "/layout.ftlh" as layout>
<#assign errors = .data_model["org.springframework.validation.BindingResult.routeForm"]!>
<#assign selectedEventIds = (routeForm.eventIds)![]>
<@layout.page title=(routeId??)?then("Редактирование маршрута", "Новый маршрут")>
<main class="page-shell">
    <div class="container">
        <h1><#if routeId??>Редактировать маршрут<#else>Создать маршрут</#if></h1>
        <form class="card card-body shadow-sm" method="post" action="<#if routeId??>/routes/${routeId}<#else>/routes</#if>">
            <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
            <label class="form-label">Название</label>
            <input class="form-control" name="title" value="${(routeForm.title)!}" required>
            <#if errors?has_content && errors.hasFieldErrors("title")><div class="form-error">${errors.getFieldError("title").defaultMessage}</div></#if>

            <label class="form-label mt-3">Описание</label>
            <textarea class="form-control" name="description" rows="4" required>${(routeForm.description)!}</textarea>
            <#if errors?has_content && errors.hasFieldErrors("description")><div class="form-error">${errors.getFieldError("description").defaultMessage}</div></#if>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">Длительность, минут</label>
                    <input class="form-control" type="number" min="15" name="durationMinutes" value="${(routeForm.durationMinutes)!90}">
                </div>
                <div class="col-md-6">
                    <label class="form-label">Бюджет</label>
                    <input class="form-control" type="number" min="0" max="1000000" step="0.01" name="budget" value="${(routeForm.budget)!0}">
                </div>
            </div>

            <label class="form-check mt-3">
                <input type="hidden" name="_isPublic" value="on">
                <input class="form-check-input" type="checkbox" name="isPublic" value="true" <#if (routeForm.isPublic)!true>checked</#if>>
                <span class="form-check-label">Публичный маршрут</span>
            </label>

            <div class="route-builder mt-4" data-route-builder>
                <div class="row g-3">
                    <section class="col-lg-6">
                        <label class="form-label" for="eventSearch">Найти мероприятие</label>
                        <input id="eventSearch" class="form-control" type="search" placeholder="Название, площадка или район" data-route-event-search>
                        <div class="route-event-list mt-3" data-route-event-options>
                            <#list events as event>
                                <button class="route-event-option <#if selectedEventIds?seq_contains(event.id)>d-none</#if>" type="button"
                                        data-event-id="${event.id}"
                                        data-price="${event.price}"
                                        data-lat="<#if event.venue.latitude??>${event.venue.latitude}</#if>"
                                        data-lon="<#if event.venue.longitude??>${event.venue.longitude}</#if>"
                                        data-search="${event.title} ${event.venue.name} <#if event.venue.cityDistrict??>${event.venue.cityDistrict.name}</#if>">
                                    <span class="fw-semibold">${event.title}</span>
                                    <span class="small muted">${event.venue.name} · ${event.timeLabel} · <#if event.price == 0>Бесплатно<#else>${event.price} ₽</#if></span>
                                </button>
                            </#list>
                        </div>
                    </section>
                    <section class="col-lg-6">
                        <div class="d-flex justify-content-between align-items-center">
                            <label class="form-label mb-0">Порядок маршрута</label>
                            <span class="small muted" data-route-counter>${selectedEventIds?size}</span>
                        </div>
                        <div class="route-selected-list mt-3" data-route-selected>
                            <#list selectedEventIds as selectedId>
                                <#list events as event>
                                    <#if event.id == selectedId>
                                        <div class="route-selected-item" data-event-id="${event.id}" data-price="${event.price}" data-lat="<#if event.venue.latitude??>${event.venue.latitude}</#if>" data-lon="<#if event.venue.longitude??>${event.venue.longitude}</#if>" draggable="true">
                                            <input type="hidden" name="eventIds" value="${event.id}">
                                            <span class="route-step-number"></span>
                                            <div>
                                                <div class="fw-semibold">${event.title}</div>
                                                <div class="small muted">${event.venue.name} · ${event.timeLabel} · <#if event.price == 0>Бесплатно<#else>${event.price} ₽</#if></div>
                                            </div>
                                            <div class="route-selected-actions">
                                                <button class="btn btn-sm btn-outline-secondary" type="button" data-route-up>Вверх</button>
                                                <button class="btn btn-sm btn-outline-secondary" type="button" data-route-down>Вниз</button>
                                                <button class="btn btn-sm btn-outline-danger" type="button" data-route-remove>Убрать</button>
                                            </div>
                                        </div>
                                    </#if>
                                </#list>
                            </#list>
                        </div>
                        <p class="small muted mt-2 mb-0">Перетаскивайте точки или меняйте порядок кнопками.</p>
                        <div class="route-summary-panel mt-3" data-route-summary>
                            <div><strong>Точек:</strong> <span data-route-summary-count>0</span></div>
                            <div><strong>Билеты:</strong> <span data-route-summary-price>0 ₽</span></div>
                            <div><strong>План:</strong> <span data-route-summary-duration>0 мин</span></div>
                            <div><strong>Дистанция:</strong> <span data-route-summary-distance>0 км</span></div>
                            <div class="small mt-2" data-route-budget-hint></div>
                        </div>
                    </section>
                </div>
            </div>
            <#if errors?has_content && errors.hasFieldErrors("eventIds")><div class="form-error">${errors.getFieldError("eventIds").defaultMessage}</div></#if>

            <div class="d-flex gap-2 mt-4">
                <button class="btn btn-primary" type="submit">Сохранить</button>
                <a class="btn btn-outline-secondary" href="/routes">Отмена</a>
            </div>
        </form>
    </div>
</main>
</@layout.page>
