<#import "/layout.ftlh" as layout>
<@layout.page title="Ошибка сервера">
<main class="page-shell">
    <div class="container">
        <div class="card card-body shadow-sm">
            <h1>500</h1>
            <p class="lead">${(message)!"Внутренняя ошибка сервера"}</p>
            <a class="btn btn-primary" href="${contextPath}/">На главную</a>
        </div>
    </div>
</main>
</@layout.page>
