<#import "/layout.ftlh" as layout>
<#import "/fragments.ftlh" as ui>
<@layout.page title="Профиль">
<main class="page-shell">
    <div class="container">
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-4">
            <div>
                <h1>Личный кабинет</h1>
                <p class="muted mb-0">${username} · ${(profileForm.email)!}</p>
            </div>
            <div class="d-flex gap-2">
                <a class="btn btn-outline-secondary" href="${contextPath}/profile/edit">Редактировать профиль</a>
                <a class="btn btn-primary" href="${contextPath}/routes/new">Создать маршрут</a>
            </div>
        </div>

        <section class="card card-body shadow-sm mb-5">
            <h2 class="h4">Предпочтения</h2>
            <p class="mb-1"><strong>Бюджет:</strong> <#if profileForm.preferredBudget??>${profileForm.preferredBudget} ₽<#else>не указан</#if></p>
            <p class="mb-2"><strong>Адрес:</strong> <#if profileForm.preferredAddress?? && profileForm.preferredAddress?has_content>${profileForm.preferredAddress}<#else>не указан</#if></p>
            <div>
                <strong>Любимые категории:</strong>
                <#if profileForm.favoriteCategoryNames?has_content>
                    <div class="d-flex flex-wrap gap-1 mt-2">
                        <#list profileForm.favoriteCategoryNames as categoryName>
                            <span class="badge text-bg-light">${categoryName}</span>
                        </#list>
                    </div>
                <#else>
                    <span class="muted">не выбраны</span>
                </#if>
            </div>
        </section>

        <h2 class="h4">Мои маршруты</h2>
        <div class="row row-cols-1 row-cols-md-2 g-4 mb-5">
            <#list routes as route>
                <article class="col">
                    <div class="card h-100 shadow-sm clickable-card" data-card-href="${contextPath}/routes/${route.id()}">
                        <div class="card-body">
                            <div class="d-flex justify-content-between gap-2">
                                <h3 class="h5">${route.title()}</h3>
                                <span class="badge <#if route.isPublic()>text-bg-success<#else>text-bg-secondary</#if>"><#if route.isPublic()>Публичный<#else>Личный</#if></span>
                            </div>
                            <p class="muted">${route.description()}</p>
                            <p class="small muted">
                                ${route.durationMinutes()} мин · ${route.budget()} ₽ · ${route.events()?size} точек
                                <#if route.distanceKm()?? && route.distanceKm() gt 0> · ${route.distanceKm()} км</#if>
                            </p>
                            <div class="d-flex gap-2">
                                <a class="btn btn-sm btn-outline-secondary" href="${contextPath}/routes/${route.id()}/edit">Редактировать</a>
                                <form method="post" action="${contextPath}/routes/${route.id()}/delete">
                                    <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                                    <button class="btn btn-sm btn-outline-danger" type="submit">Удалить</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </article>
            <#else>
                <p class="muted">Вы ещё не создали маршруты.</p>
            </#list>
        </div>

        <h2 class="h4">Избранные события</h2>
        <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
            <#list favorites as event>
                <@ui.eventCard event=event />
            <#else>
                <p class="muted">Избранное пока пустое.</p>
            </#list>
        </div>
    </div>
</main>
</@layout.page>
