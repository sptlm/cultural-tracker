<#import "/layout.ftlh" as layout>
<#assign errors = .data_model["org.springframework.validation.BindingResult.venueForm"]!>
<@layout.page title=(venueId??)?then("Редактирование площадки", "Новая площадка")>
<main class="page-shell">
    <div class="container">
        <h1><#if venueId??>Редактировать площадку<#else>Новая площадка</#if></h1>
        <form class="card card-body shadow-sm" method="post" action="<#if venueId??>${contextPath}/admin/venues/${venueId}<#else>${contextPath}/admin/venues</#if>">
            <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
            <label class="form-label">Название</label>
            <input class="form-control" name="name" value="${(venueForm.name)!}" required>
            <#if errors?has_content && errors.hasFieldErrors("name")><div class="form-error">${errors.getFieldError("name").defaultMessage}</div></#if>
            <label class="form-label mt-3">Описание</label>
            <textarea class="form-control" name="description" rows="4" required>${(venueForm.description)!}</textarea>
            <#if errors?has_content && errors.hasFieldErrors("description")><div class="form-error">${errors.getFieldError("description").defaultMessage}</div></#if>
            <label class="form-label mt-3">Адрес</label>
            <input class="form-control" name="address" value="${(venueForm.address)!}" required>
            <#if errors?has_content && errors.hasFieldErrors("address")><div class="form-error">${errors.getFieldError("address").defaultMessage}</div></#if>
            <label class="form-label mt-3">Район</label>
            <select class="form-select" name="cityDistrictId">
                <option value="">Не указан</option>
                <#list districts as district>
                    <option value="${district.id}" <#if (venueForm.cityDistrictId)?? && venueForm.cityDistrictId == district.id>selected</#if>>${district.name}</option>
                </#list>
            </select>
            <div class="d-flex gap-2 mt-4">
                <button class="btn btn-primary" type="submit">Сохранить</button>
                <a class="btn btn-outline-secondary" href="${contextPath}/admin">Отмена</a>
            </div>
        </form>
    </div>
</main>
</@layout.page>
