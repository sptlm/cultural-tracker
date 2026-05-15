<#import "/layout.ftlh" as layout>
<#assign errors = .data_model["org.springframework.validation.BindingResult.profileForm"]!>
<#assign selectedCategoryIds = (profileForm.favoriteCategoryIds)![]>
<@layout.page title="Редактирование профиля">
<main class="page-shell">
    <div class="container">
        <h1>Редактировать профиль</h1>
        <form class="card card-body shadow-sm" method="post" action="${contextPath}/profile">
            <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
            <label class="form-label">Имя пользователя</label>
            <input class="form-control" value="${username}" disabled>

            <label class="form-label mt-3">Email</label>
            <input class="form-control" type="email" name="email" value="${(profileForm.email)!}" required>
            <#if errors?has_content && errors.hasFieldErrors("email")><div class="form-error">${errors.getFieldError("email").defaultMessage}</div></#if>

            <label class="form-label mt-3">Любимый бюджет</label>
            <input class="form-control" type="number" min="0" max="1000000" step="0.01" name="preferredBudget" value="${(profileForm.preferredBudget)!}">
            <#if errors?has_content && errors.hasFieldErrors("preferredBudget")><div class="form-error">${errors.getFieldError("preferredBudget").defaultMessage}</div></#if>

            <label class="form-label mt-3">Адрес для рекомендаций</label>
            <input class="form-control" name="preferredAddress" value="${(profileForm.preferredAddress)!}" placeholder="Казань, Кремлевская, 18">

            <fieldset class="mt-3">
                <legend class="form-label">Любимые категории</legend>
                <div class="d-flex flex-wrap gap-3">
                    <#list categories as category>
                        <label class="form-check">
                            <input class="form-check-input" type="checkbox" name="favoriteCategoryIds" value="${category.id}" <#if selectedCategoryIds?seq_contains(category.id)>checked</#if>>
                            <span class="form-check-label">${category.name}</span>
                        </label>
                    </#list>
                </div>
            </fieldset>

            <div class="d-flex gap-2 mt-4">
                <button class="btn btn-primary" type="submit">Сохранить</button>
                <a class="btn btn-outline-secondary" href="${contextPath}/profile">Отмена</a>
            </div>
        </form>
    </div>
</main>
</@layout.page>
