<#import "/layout.ftlh" as layout>
<#assign errors = .data_model["org.springframework.validation.BindingResult.eventForm"]!>
<#assign selectedCategoryIds = (eventForm.categoryIds)![]>
<@layout.page title=(eventId??)?then("Редактирование мероприятия", "Новое мероприятие")>
<main class="page-shell">
    <div class="container">
        <h1><#if eventId??>Редактировать мероприятие<#else>Создать мероприятие</#if></h1>
        <form class="card card-body shadow-sm" method="post" action="<#if eventId??>${contextPath}/admin/events/${eventId}<#else>${contextPath}/admin/events</#if>">
            <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>

            <label class="form-label">Название</label>
            <input class="form-control" name="title" value="${(eventForm.title)!}" required>
            <#if errors?has_content && errors.hasFieldErrors("title")><div class="form-error">${errors.getFieldError("title").defaultMessage}</div></#if>

            <label class="form-label mt-3">Описание</label>
            <textarea class="form-control" name="description" rows="4" required>${(eventForm.description)!}</textarea>
            <#if errors?has_content && errors.hasFieldErrors("description")><div class="form-error">${errors.getFieldError("description").defaultMessage}</div></#if>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">Начало</label>
                    <input class="form-control" type="datetime-local" name="startAt" value="${eventForm.startInputValue}" required>
                    <#if errors?has_content && errors.hasFieldErrors("startAt")><div class="form-error">${errors.getFieldError("startAt").defaultMessage}</div></#if>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Окончание</label>
                    <input class="form-control" type="datetime-local" name="endAt" value="${eventForm.endInputValue}">
                </div>
            </div>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">Стоимость</label>
                    <input class="form-control" type="number" min="0" max="1000000" step="0.01" name="price" value="${(eventForm.price)!0}" required>
                    <#if errors?has_content && errors.hasFieldErrors("price")><div class="form-error">${errors.getFieldError("price").defaultMessage}</div></#if>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Площадка</label>
                    <select class="form-select" name="venueId" required>
                        <option value="">Выберите площадку</option>
                        <#list venues as venue>
                            <option value="${venue.id}" <#if (eventForm.venueId)?? && eventForm.venueId == venue.id>selected</#if>>${venue.name}</option>
                        </#list>
                    </select>
                    <#if errors?has_content && errors.hasFieldErrors("venueId")><div class="form-error">${errors.getFieldError("venueId").defaultMessage}</div></#if>
                </div>
            </div>

            <label class="form-label mt-3">Ссылка на изображение</label>
            <input class="form-control" name="imageUrl" value="${(eventForm.imageUrl)!}">

            <fieldset class="mt-3">
                <legend class="form-label">Категории</legend>
                <div class="d-flex flex-wrap gap-3">
                    <#list categories as category>
                        <label class="form-check">
                            <input class="form-check-input" type="checkbox" name="categoryIds" value="${category.id}" <#if selectedCategoryIds?seq_contains(category.id)>checked</#if>>
                            <span class="form-check-label">${category.name}</span>
                        </label>
                    </#list>
                </div>
            </fieldset>

            <div class="d-flex gap-2 mt-4">
                <button class="btn btn-primary" type="submit">Сохранить</button>
                <a class="btn btn-outline-secondary" href="${contextPath}/admin">Отмена</a>
            </div>
        </form>
    </div>
</main>
</@layout.page>
