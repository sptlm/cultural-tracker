<#import "/layout.ftlh" as layout>
<@layout.page title="Доступ запрещён">
<main class="page-shell">
    <div class="container">
        <div class="card card-body shadow-sm">
            <h1>403</h1>
            <p class="lead">${(message)!"Недостаточно прав для этого действия"}</p>
            <a class="btn btn-primary" href="/">На главную</a>
        </div>
    </div>
</main>
</@layout.page>
