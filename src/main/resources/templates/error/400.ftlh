<#import "/layout.ftlh" as layout>
<@layout.page title="Некорректный запрос">
<main class="page-shell">
    <div class="container">
        <div class="card card-body shadow-sm">
            <h1>400</h1>
            <p class="lead">${(message)!"Некорректный запрос"}</p>
            <a class="btn btn-primary" href="${contextPath}/">На главную</a>
        </div>
    </div>
</main>
</@layout.page>
