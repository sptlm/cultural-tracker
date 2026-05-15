<#import "/layout.ftlh" as layout>
<@layout.page title="Страница не найдена">
<main class="page-shell">
    <div class="container">
        <div class="card card-body shadow-sm">
            <h1>404</h1>
            <p class="lead">${(message)!"Страница не найдена"}</p>
            <a class="btn btn-primary" href="${contextPath}/">На главную</a>
        </div>
    </div>
</main>
</@layout.page>
