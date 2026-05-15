<#import "/layout.ftlh" as layout>
<@layout.page title="Вход">
<main class="page-shell auth-shell">
    <section class="card auth-card shadow-sm">
        <div class="card-body">
            <p class="auth-eyebrow">Добро пожаловать</p>
            <h1>Вход</h1>

            <#if registered??>
                <div class="alert alert-success">Регистрация прошла успешно. Теперь можно войти.</div>
            </#if>
            <#if loginError?? && loginError>
                <div class="alert alert-danger">Неверное имя пользователя или пароль.</div>
            </#if>
            <#if loggedOut?? && loggedOut>
                <div class="alert alert-success">Вы вышли из аккаунта.</div>
            </#if>

            <form action="/login" method="post" class="auth-form">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

                <label class="form-label" for="username">Имя пользователя</label>
                <input id="username" class="form-control" type="text" name="username" autocomplete="username" required autofocus>

                <label class="form-label mt-3" for="password">Пароль</label>
                <input id="password" class="form-control" type="password" name="password" autocomplete="current-password" required>

                <button type="submit" class="btn btn-primary w-100 mt-4">Войти</button>
            </form>

            <p class="auth-switch">Еще нет аккаунта? <a href="/register">Зарегистрироваться</a></p>
        </div>
    </section>
</main>
</@layout.page>
