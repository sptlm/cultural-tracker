<#assign errors = .data_model["org.springframework.validation.BindingResult.registrationRequest"]!>
<#import "/layout.ftlh" as layout>
<@layout.page title="Регистрация">
<main class="page-shell auth-shell">
    <section class="card auth-card shadow-sm">
        <div class="card-body">
            <p class="auth-eyebrow">Новый аккаунт</p>
            <h1>Регистрация</h1>

            <form action="${contextPath}/register" method="post" class="auth-form">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

                <label class="form-label" for="username">Имя пользователя</label>
                <input id="username" class="form-control" type="text" name="username" value="${(registrationRequest.username)!}" autocomplete="username" required autofocus data-check-field="${contextPath}/api/auth/check-username" data-check-target="#usernameAvailability">
                <span id="usernameAvailability" class="small"></span>
                <#if errors?has_content && errors.hasFieldErrors("username")>
                    <div class="form-error">${errors.getFieldError("username").defaultMessage}</div>
                </#if>

                <label class="form-label mt-3" for="email">Email</label>
                <input id="email" class="form-control" type="email" name="email" value="${(registrationRequest.email)!}" autocomplete="email" required data-check-field="${contextPath}/api/auth/check-email" data-check-target="#emailAvailability">
                <span id="emailAvailability" class="small"></span>
                <#if errors?has_content && errors.hasFieldErrors("email")>
                    <div class="form-error">${errors.getFieldError("email").defaultMessage}</div>
                </#if>

                <label class="form-label mt-3" for="password">Пароль</label>
                <input id="password" class="form-control" type="password" name="password" autocomplete="new-password" required>
                <#if errors?has_content && errors.hasFieldErrors("password")>
                    <div class="form-error">${errors.getFieldError("password").defaultMessage}</div>
                </#if>

                <label class="form-label mt-3" for="passwordConfirmation">Повторите пароль</label>
                <input id="passwordConfirmation" class="form-control" type="password" name="passwordConfirmation" autocomplete="new-password" required>
                <#if errors?has_content && errors.hasFieldErrors("passwordConfirmation")>
                    <div class="form-error">${errors.getFieldError("passwordConfirmation").defaultMessage}</div>
                </#if>

                <button type="submit" class="btn btn-primary w-100 mt-4">Создать аккаунт</button>
            </form>

            <p class="auth-switch">Уже есть аккаунт? <a href="${contextPath}/login">Войти</a></p>
        </div>
    </section>
</main>
</@layout.page>
