<#macro eventCard event>
<article class="col">
    <div class="card h-100 shadow-sm clickable-card" data-card-href="/events/${event.id()}">
        <#if event.imageUrl()?has_content>
            <img class="event-img card-img-top" src="${event.imageUrl()}" alt="${event.title()}">
        </#if>
        <div class="card-body">
            <div class="event-badges mb-2">
                <#if event.today()><span class="badge text-bg-primary">Сегодня</span></#if>
                <#if event.price() == 0><span class="badge text-bg-success">Бесплатно</span></#if>
                <#if event.nearby()><span class="badge text-bg-info">Рядом</span></#if>
                <#if event.highRated()><span class="badge text-bg-warning">Высокий рейтинг</span></#if>
            </div>
            <div class="d-flex justify-content-between align-items-center gap-2 mb-2">
                <span class="rating-pill"><#if event.reviewsCount() == 0>Нет оценок<#else>Рейтинг: ${event.ratingLabel()}</#if></span>
                <span class="fw-bold"><#if event.price() == 0>Бесплатно<#else>${event.price()} ₽</#if></span>
            </div>
            <h2 class="h5">${event.title()}</h2>
            <p class="small fw-semibold mb-2">${event.timeLabel()}</p>
            <p class="muted">${event.description()}</p>
            <p class="small mb-2">
                ${event.venueName()} · ${event.districtName()}
                <#if event.distanceKm()??> · ${event.distanceKm()} км от вас</#if>
            </p>
            <div class="d-flex flex-wrap gap-1">
                <#list event.categories() as category>
                    <span class="badge text-bg-light">${category}</span>
                </#list>
            </div>
        </div>
    </div>
</article>
</#macro>
