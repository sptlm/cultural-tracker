<#import "/layout.ftlh" as layout>
<#import "/fragments.ftlh" as ui>
<@layout.page title="Главная">
<section class="hero-band">
    <div class="container">
        <div class="row align-items-center g-4">
            <div class="col-lg-7">
                <p class="text-uppercase fw-bold text-primary mb-2">Городской культурный сервис</p>
                <h1 class="display-5 fw-bold">Культурный навигатор</h1>
                <p class="lead muted">Ищите события, сохраняйте избранное и собирайте маршруты по музеям, лекциям, выставкам и концертам.</p>
                <div class="d-flex flex-wrap gap-2">
                    <a class="btn btn-primary" href="${contextPath}/events">Найти событие</a>
                    <a class="btn btn-outline-secondary" href="${contextPath}/routes">Посмотреть маршруты</a>
                </div>
            </div>
            <div class="col-lg-5">
                <div class="p-4 bg-light border rounded-2">
                    <h2 class="h5">Быстрые сценарии</h2>
                    <div class="list-group list-group-flush quick-scenarios">
                        <a class="list-group-item list-group-item-action px-0 bg-transparent" href="${contextPath}/events?time=EVENING">
                            <span class="fw-semibold">Маршрут на вечер</span>
                            <span class="small muted d-block">События после 18:00</span>
                        </a>
                        <a class="list-group-item list-group-item-action px-0 bg-transparent" href="${contextPath}/events?freeOnly=true">
                            <span class="fw-semibold">Бесплатные события</span>
                            <span class="small muted d-block">Афиша без входных билетов</span>
                        </a>
                        <a class="list-group-item list-group-item-action px-0 bg-transparent" href="${contextPath}/events<#if centerDistrictId??>?districtId=${centerDistrictId}<#else>?query=центр</#if>">
                            <span class="fw-semibold">Площадки рядом с центром</span>
                            <span class="small muted d-block">События в центральном районе</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<main class="page-shell">
    <div class="container">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="h3 mb-0"><#if personalized?? && personalized>Рекомендации для вас<#else>Популярные события</#if></h2>
            <a href="${contextPath}/events">Все события</a>
        </div>
        <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4 mb-5">
            <#list popularEvents as event>
                <@ui.eventCard event=event />
            <#else>
                <p class="muted">События появятся после наполнения базы.</p>
            </#list>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="h3 mb-0">Готовые маршруты</h2>
            <a href="${contextPath}/routes/new">Создать маршрут</a>
        </div>
        <div class="row row-cols-1 row-cols-md-3 g-4">
            <#list routes as route>
                <article class="col">
                    <div class="card h-100 shadow-sm clickable-card" data-card-href="${contextPath}/routes/${route.id()}">
                        <div class="card-body">
                            <h3 class="h5">${route.title()}</h3>
                            <p class="muted">${route.description()}</p>
                            <p class="small mb-0">${route.durationMinutes()} мин · ${route.budget()} ₽</p>
                        </div>
                    </div>
                </article>
            </#list>
        </div>
    </div>
</main>
</@layout.page>
