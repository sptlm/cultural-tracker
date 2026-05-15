<#import "/layout.ftlh" as layout>
<@layout.page title="Маршруты">
<main class="page-shell">
    <div class="container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="mb-0">Маршруты</h1>
            <a class="btn btn-primary" href="/routes/new">Создать маршрут</a>
        </div>
        <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
            <#list routes as route>
                <article class="col">
                    <div class="card h-100 shadow-sm clickable-card" data-card-href="/routes/${route.id()}">
                        <div class="card-body">
                            <h2 class="h5">${route.title()}</h2>
                            <p class="muted">${route.description()}</p>
                            <p class="small mb-1">${route.authorUsername()} · ${route.events()?size} точек</p>
                            <p class="small mb-0">
                                ${route.durationMinutes()} мин · ${route.budget()} ₽
                                <#if route.distanceKm()?? && route.distanceKm() gt 0> · ${route.distanceKm()} км</#if>
                            </p>
                        </div>
                    </div>
                </article>
            <#else>
                <p class="muted">Публичных маршрутов пока нет.</p>
            </#list>
        </div>
    </div>
</main>
</@layout.page>
