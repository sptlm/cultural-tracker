<#import "/layout.ftlh" as layout>
<#import "/fragments.ftlh" as ui>
<@layout.page title=route.title()>
<main class="page-shell">
    <div class="container">
        <div class="d-flex justify-content-between gap-3 align-items-start mb-4">
            <div>
                <h1>${route.title()}</h1>
                <p class="lead muted">${route.description()}</p>
                <p class="small">
                    ${route.authorUsername()} · <#if route.isPublic()>публичный<#else>личный</#if> · ${route.events()?size} точек · ${route.durationMinutes()} мин · ${route.budget()} ₽
                    <#if route.distanceKm()?? && route.distanceKm() gt 0> · ${route.distanceKm()} км</#if>
                </p>
            </div>
            <#if username?? && username == route.authorUsername()>
                <div class="d-flex gap-2">
                    <a class="btn btn-outline-secondary" href="/routes/${route.id()}/edit">Редактировать</a>
                    <form method="post" action="/routes/${route.id()}/delete">
                        <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                        <button class="btn btn-outline-danger" type="submit">Удалить</button>
                    </form>
                </div>
            </#if>
        </div>
        <section class="route-timeline mb-4">
            <h2 class="h4">Маршрут по шагам</h2>
            <ol class="route-timeline-list">
                <#list route.events() as event>
                    <li>
                        <div class="route-timeline-marker">${event?index + 1}</div>
                        <div class="route-timeline-content">
                            <h3 class="h6 mb-1"><a href="/events/${event.id()}">${event.title()}</a></h3>
                            <p class="small muted mb-1">${event.timeLabel()} · ${event.venueName()} · ${event.districtName()}</p>
                            <p class="small mb-0"><#if event.price() == 0>Бесплатно<#else>${event.price()} ₽</#if><#if event.distanceKm()??> · ${event.distanceKm()} км от вас</#if></p>
                        </div>
                    </li>
                </#list>
            </ol>
        </section>
        <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
            <#list route.events() as event>
                <@ui.eventCard event=event />
            </#list>
        </div>
    </div>
</main>
</@layout.page>
