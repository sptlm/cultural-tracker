<#assign errors = .data_model["org.springframework.validation.BindingResult.registrationRequest"]!>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Регистрация | Культурный навигатор</title>
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>
<main class="shell">
    <section class="panel auth-panel">
        <a class="back-link" href="/">На главную</a>
        <p class="eyebrow">Новый аккаунт</p>
        <h1>Регистрация</h1>

        <form action="/register" method="post" class="form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <label class="field">
                <span>Имя пользователя</span>
                <input type="text" name="username" value="${((registrationRequest.username)!)?html}" autocomplete="username" required autofocus>
                <#if errors?has_content && errors.hasFieldErrors("username")>
                    <small>${errors.getFieldError("username").defaultMessage}</small>
                </#if>
            </label>

            <label class="field">
                <span>Email</span>
                <input type="email" name="email" value="${((registrationRequest.email)!)?html}" autocomplete="email" required>
                <#if errors?has_content && errors.hasFieldErrors("email")>
                    <small>${errors.getFieldError("email").defaultMessage}</small>
                </#if>
            </label>

            <label class="field">
                <span>Пароль</span>
                <input type="password" name="password" autocomplete="new-password" required>
                <#if errors?has_content && errors.hasFieldErrors("password")>
                    <small>${errors.getFieldError("password").defaultMessage}</small>
                </#if>
            </label>

            <label class="field">
                <span>Повторите пароль</span>
                <input type="password" name="passwordConfirmation" autocomplete="new-password" required>
                <#if errors?has_content && errors.hasFieldErrors("passwordConfirmation")>
                    <small>${errors.getFieldError("passwordConfirmation").defaultMessage}</small>
                </#if>
            </label>

            <button type="submit" class="button">Создать аккаунт</button>
        </form>

        <p class="switch">Уже есть аккаунт? <a href="/login">Войти</a></p>
    </section>
</main>
</body>
</html>
