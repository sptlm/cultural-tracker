<#macro page title>
<#assign cp = contextPath!''>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <#if _csrf??>
        <meta name="_csrf" content="${_csrf.token}">
        <meta name="_csrf_header" content="${_csrf.headerName}">
    </#if>
    <title>${title} | Культурный навигатор</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${cp}/css/app.css">
</head>
<body>
<nav class="navbar navbar-expand-lg bg-white border-bottom">
    <div class="container">
        <a class="navbar-brand" href="${cp}/">Культурный навигатор</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="${cp}/events">События</a></li>
                <li class="nav-item"><a class="nav-link" href="${cp}/venues">Площадки</a></li>
                <li class="nav-item"><a class="nav-link" href="${cp}/routes">Маршруты</a></li>
            </ul>
            <div class="d-flex gap-2">
                <#if username??>
                    <#if isAdmin?? && isAdmin>
                        <a class="btn btn-outline-primary" href="${cp}/admin">Админка</a>
                    </#if>
                    <a class="btn btn-outline-secondary" href="${cp}/profile">Профиль</a>
                    <form action="${cp}/logout" method="post">
                        <#if _csrf??><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"></#if>
                        <button class="btn btn-outline-secondary" type="submit">Выйти</button>
                    </form>
                <#else>
                    <a class="btn btn-outline-secondary" href="${cp}/login">Войти</a>
                    <a class="btn btn-primary" href="${cp}/register">Регистрация</a>
                </#if>
            </div>
        </div>
    </div>
</nav>
<#if success??>
    <div class="container mt-3"><div class="alert alert-success">${success}</div></div>
</#if>
<div class="site-main">
<#nested>
</div>
<footer class="site-footer border-top bg-white py-4">
    <div class="container d-flex flex-wrap justify-content-between gap-2 small muted">
        <span>Культурный навигатор города</span>
        <span>Spring MVC · Freemarker · PostgreSQL · Redis</span>
    </div>
</footer>
<script id="app-config" type="application/json">
{
    "nearbyDistanceKm": ${(frontendConfig.nearbyDistanceKm!5)?c},
    "highRatingThreshold": ${(frontendConfig.highRatingThreshold!4.5)?c},
    "earthRadiusKm": ${(frontendConfig.earthRadiusKm!6371)?c},
    "distanceScale": ${(frontendConfig.distanceScale!1)?c},
    "minRoutePoints": ${(frontendConfig.minRoutePoints!2)?c},
    "toastDurationMs": ${(frontendConfig.toastDurationMs!4500)?c},
    "actionToastDurationMs": ${(frontendConfig.actionToastDurationMs!8000)?c},
    "contextPath": "${cp?js_string}"
}
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${cp}/js/app.js"></script>
</body>
</html>
</#macro>
