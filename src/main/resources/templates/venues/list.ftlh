<#import "/layout.ftlh" as layout>
<@layout.page title="Площадки">
<main class="page-shell">
    <div class="container">
        <h1 class="mb-4">Площадки</h1>
        <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
            <#list venues as venue>
                <article class="col">
                    <div class="card h-100 shadow-sm clickable-card" data-card-href="/venues/${venue.id}">
                        <div class="card-body">
                            <h2 class="h5">${venue.name}</h2>
                            <p>${venue.description}</p>
                            <p class="muted">${venue.address}</p>
                            <p class="small mb-1"><#if venue.cityDistrict??>${venue.cityDistrict.name}<#else>Район не указан</#if></p>
                            <#if venue.latitude?? && venue.longitude??>
                                <p class="small mb-0">${venue.latitude}, ${venue.longitude}</p>
                            </#if>
                        </div>
                    </div>
                </article>
            </#list>
        </div>
    </div>
</main>
</@layout.page>
