<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Культурный навигатор</title>
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>
<main class="shell">
    <section class="panel home-panel">
        <p class="eyebrow">Городской культурный сервис</p>
        <h1>Культурный навигатор</h1>
        <p class="lead">Собирайте маршруты по событиям, местам и районам города.</p>

        <#if username??>
            <div class="notice success">Вы вошли как ${username}.</div>
            <form action="/logout" method="post" class="inline-actions">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <button type="submit" class="button secondary">Выйти</button>
            </form>
        <#else>
            <div class="inline-actions">
                <a class="button" href="/login">Войти</a>
                <a class="button secondary" href="/register">Зарегистрироваться</a>
            </div>
        </#if>
    </section>
</main>
</body>
</html>
