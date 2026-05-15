<#import "/layout.ftlh" as layout>
<@layout.page title=event.title()>
<main class="page-shell">
    <div class="container">
        <div class="row g-4">
            <div class="col-lg-8">
                <#if event.imageUrl()?has_content>
                    <img class="event-img rounded-2 mb-4" src="${event.imageUrl()}" alt="">
                </#if>
                <h1>${event.title()}</h1>
                <p class="lead muted">${event.description()}</p>
                <div class="d-flex flex-wrap gap-2 mb-3">
                    <#if event.today()><span class="badge text-bg-primary">Сегодня</span></#if>
                    <#if event.price() == 0><span class="badge text-bg-success">Бесплатно</span></#if>
                    <#if event.nearby()><span class="badge text-bg-info">Рядом</span></#if>
                    <#if event.highRated()><span class="badge text-bg-warning">Высокий рейтинг</span></#if>
                    <#list event.categories() as category>
                        <span class="badge text-bg-light">${category}</span>
                    </#list>
                </div>
                <p><strong>Время:</strong> ${event.timeLabel()}</p>
                <p><strong>Площадка:</strong> <a href="${contextPath}/venues/${event.venueId()}">${event.venueName()}</a></p>
                <p><strong>Район:</strong> ${event.districtName()}</p>
                <p><strong>Стоимость:</strong> <#if event.price() == 0>Бесплатно<#else>${event.price()} ₽</#if></p>
                <p><strong>Отзывы:</strong> <#if event.reviewsCount() == 0>нет оценок<#else>рейтинг ${event.ratingLabel()} на основе ${event.reviewsCount()} отзывов</#if></p>
                <button class="btn btn-outline-danger favorite-button" data-event-id="${event.id()}" data-favorite="${event.favorite()?string('true','false')}">
                    <#if event.favorite()>В избранном<#else>В избранное</#if>
                </button>
            </div>
            <aside class="col-lg-4">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h2 class="h4">Отзывы</h2>
                        <#if username??>
                            <form class="mb-3" data-review-form data-event-id="${event.id()}">
                                <label class="form-label">Оценка</label>
                                <select class="form-select" name="rating">
                                    <#list 1..5 as rating>
                                        <option value="${rating}">${rating}</option>
                                    </#list>
                                </select>
                                <label class="form-label mt-3">Текст</label>
                                <textarea class="form-control" name="text" rows="3" required></textarea>
                                <button class="btn btn-primary mt-3" type="submit">Отправить</button>
                            </form>
                            <div class="small" data-review-status aria-live="polite"></div>
                        <#else>
                            <p><a href="${contextPath}/login">Войдите</a>, чтобы оставить отзыв.</p>
                        </#if>
                        <div data-reviews-list data-event-id="${event.id()}">
                            <#list reviews as review>
                                <article class="review-item border-top pt-3 mt-3">
                                    <div class="fw-bold">${review.author} · ${review.rating}/5</div>
                                    <p class="mb-0">${review.text}</p>
                                </article>
                            <#else>
                                <p class="muted" data-reviews-empty>Отзывов пока нет.</p>
                            </#list>
                        </div>
                    </div>
                </div>
            </aside>
        </div>
    </div>
</main>
</@layout.page>
