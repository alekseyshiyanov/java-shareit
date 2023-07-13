package ru.practicum.shareit.eventhandlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.searchservices.IndexingService;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartupEvent implements ApplicationListener<ApplicationReadyEvent> {

    private final IndexingService indexingService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            indexingService.initiateIndexing();
        } catch (InterruptedException e) {
            log.error("Переиндексация записей при запуске приложения завершилась с ошибкой. Причина: {}", e.getMessage());
            log.error("StackTrace: ");
            e.printStackTrace();
        }
    }
}
