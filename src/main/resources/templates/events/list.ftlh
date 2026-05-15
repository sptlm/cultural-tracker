<#import "/layout.ftlh" as layout>
<#import "/fragments.ftlh" as ui>
<#assign selectedTime = ((filter.time)!"ALL")?string>
<#assign selectedRating = "">
<#if (filter.ratingMin)??>
    <#assign selectedRating = filter.ratingMin?c>
</#if>
<@layout.page title="События Казани">
<main class="page-shell">
    <div class="container">
        <div class="row g-4">
            <aside class="col-lg-3">
                <form class="card card-body shadow-sm" method="get" action="/events" data-event-filter>
                    <h1 class="h4">Фильтры</h1>
                    <label class="form-label mt-2">Поиск</label>
                    <input class="form-control" type="search" name="query" value="${(filter.query)!}">
                    <label class="form-label mt-3">Категория</label>
                    <select class="form-select" name="categoryId">
                        <option value="">Все</option>
                        <#list categories as category>
                            <option value="${category.id}" <#if (filter.categoryId)?? && filter.categoryId == category.id>selected</#if>>${category.name}</option>
                        </#list>
                    </select>
                    <label class="form-label mt-3">Район</label>
                    <select class="form-select" name="districtId">
                        <option value="">Все</option>
                        <#list districts as district>
                            <option value="${district.id}" <#if (filter.districtId)?? && filter.districtId == district.id>selected</#if>>${district.name}</option>
                        </#list>
                    </select>

                    <label class="form-label mt-3">Период</label>
                    <div class="row g-2">
                        <div class="col-6">
                            <input class="form-control" type="date" name="dateFrom" value="${(filter.dateFrom)!}" aria-label="Дата от">
                        </div>
                        <div class="col-6">
                            <input class="form-control" type="date" name="dateTo" value="${(filter.dateTo)!}" aria-label="Дата до">
                        </div>
                    </div>

                    <label class="form-label mt-3">Время суток</label>
                    <select class="form-select" name="time">
                        <option value="ALL" <#if selectedTime == "ALL">selected</#if>>Любое время</option>
                        <option value="UPCOMING" <#if selectedTime == "UPCOMING">selected</#if>>Ближайшие</option>
                        <option value="TODAY" <#if selectedTime == "TODAY">selected</#if>>Сегодня</option>
                        <option value="EVENING" <#if selectedTime == "EVENING">selected</#if>>Вечером</option>
                        <option value="WEEKEND" <#if selectedTime == "WEEKEND">selected</#if>>В выходные</option>
                    </select>

                    <label class="form-label mt-3">Цена</label>
                    <div class="row g-2">
                        <div class="col-6">
                            <input class="form-control" type="number" min="0" max="1000000" step="0.01" name="priceMin" value="${(filter.priceMin)!}" placeholder="от">
                        </div>
                        <div class="col-6">
                            <input class="form-control" type="number" min="0" max="1000000" step="0.01" name="priceMax" value="${(filter.priceMax)!}" placeholder="до">
                        </div>
                    </div>

                    <label class="form-label mt-3">Рейтинг от</label>
                    <select class="form-select" name="ratingMin">
                        <option value="" <#if selectedRating == "">selected</#if>>Любой рейтинг</option>
                        <#list [
                            {"value": "5", "label": "5"},
                            {"value": "4.5", "label": "4.5"},
                            {"value": "4", "label": "4"},
                            {"value": "3.5", "label": "3.5"},
                            {"value": "3", "label": "3"}
                        ] as rating>
                            <option value="${rating.value}" <#if selectedRating == rating.value>selected</#if>>${rating.label}</option>
                        </#list>
                    </select>

                    <label class="form-check mt-3">
                        <input class="form-check-input" type="checkbox" name="freeOnly" value="true" <#if filter.freeOnly>checked</#if>>
                        <span class="form-check-label">Только бесплатные</span>
                    </label>
                    <button class="btn btn-primary mt-3" type="submit">Применить</button>
                </form>
            </aside>
            <section class="col-lg-9">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div>
                        <h1 class="h2 mb-1">Культурный навигатор Казани</h1>
                        <p class="muted mb-0">Афиша мероприятий по районам и площадкам Казани.</p>
                    </div>
                    <a class="btn btn-outline-secondary" href="/routes/new">Собрать маршрут</a>
                </div>
                <div class="row row-cols-1 row-cols-md-2 g-4" data-events-target>
                    <#list events as event>
                        <@ui.eventCard event=event />
                    <#else>
                        <p class="muted">По этим фильтрам ничего не найдено.</p>
                    </#list>
                </div>
            </section>
        </div>
    </div>
</main>
</@layout.page>
