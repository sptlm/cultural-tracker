<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход | Культурный навигатор</title>
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>
<main class="shell">
    <section class="panel auth-panel">
        <a class="back-link" href="/">На главную</a>
        <p class="eyebrow">Добро пожаловать</p>
        <h1>Вход</h1>

        <#if registered??>
            <div class="notice success">Регистрация прошла успешно. Теперь можно войти.</div>
        </#if>
        <#if (RequestParameters.error)??>
            <div class="notice error">Неверное имя пользователя или пароль.</div>
        </#if>
        <#if (RequestParameters.logout)??>
            <div class="notice success">Вы вышли из аккаунта.</div>
        </#if>

        <form action="/login" method="post" class="form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <label class="field">
                <span>Имя пользователя</span>
                <input type="text" name="username" autocomplete="username" required autofocus>
            </label>

            <label class="field">
                <span>Пароль</span>
                <input type="password" name="password" autocomplete="current-password" required>
            </label>

            <button type="submit" class="button">Войти</button>
        </form>

        <p class="switch">Еще нет аккаунта? <a href="/register">Зарегистрироваться</a></p>
    </section>
</main>
</body>
</html>
