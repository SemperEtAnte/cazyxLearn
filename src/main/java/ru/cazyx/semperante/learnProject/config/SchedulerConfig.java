package ru.cazyx.semperante.learnProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import ru.cazyx.semperante.learnProject.entities.repositories.RefreshTokenRepository;

import java.util.concurrent.TimeUnit;

/**
 * Класс-настройка логики, которая должна запускаться "Время-от-времени"
 * <p>
 * Аннотация @Configuration говорит о том, что данные класс содержит в себе настройки
 * Аннотация @EnableScheduling указываем спрингу, что надо включить систему-планировщик
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
   /**
    * Репозитория с refresh токенами пользователей
    */
   private final RefreshTokenRepository refreshTokenRepository;

   /**
    * Конструктор для спринга
    *
    * @param refreshTokenRepository Репозитория которая будет подкидываться инъекцией
    */
   public SchedulerConfig(RefreshTokenRepository refreshTokenRepository) {
      this.refreshTokenRepository = refreshTokenRepository;
   }

   /**
    * Раз в 5 минут запускаем транзакцию
    * Внутри которой удаляем все истекшие refresh-токены из БД
    */
   @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
   @Transactional
   public void doDeleteExpiredTokens() {
      refreshTokenRepository.deleteExpired(); //Вызов метода для удаления
   }
}
