<#import "/layout.ftlh" as layout>
<#import "/fragments.ftlh" as ui>
<@layout.page title=venue.name>
<main class="page-shell">
    <div class="container">
        <div class="row g-4">
            <section class="col-lg-8">
                <a class="text-decoration-none" href="${contextPath}/venues">Все площадки</a>
                <h1 class="mt-3">${venue.name}</h1>
                <p class="lead muted">${venue.description}</p>
                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <p class="mb-2"><strong>Адрес:</strong> ${venue.address}</p>
                        <p class="mb-2"><strong>Район:</strong> <#if venue.cityDistrict??>${venue.cityDistrict.name}<#else>Не указан</#if></p>
                        <#if venue.latitude?? && venue.longitude??>
                            <p class="mb-0"><strong>Координаты:</strong> ${venue.latitude}, ${venue.longitude}</p>
                        </#if>
                    </div>
                </div>
            </section>
            <aside class="col-lg-4">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h2 class="h5">События на площадке</h2>
                        <p class="muted mb-0">${events?size} в афише</p>
                    </div>
                </div>
            </aside>
        </div>

        <h2 class="h3 mt-4 mb-3">Мероприятия</h2>
        <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
            <#list events as event>
                <@ui.eventCard event=event />
            <#else>
                <p class="muted">У этой площадки пока нет запланированных мероприятий.</p>
            </#list>
        </div>
    </div>
</main>
</@layout.page>
