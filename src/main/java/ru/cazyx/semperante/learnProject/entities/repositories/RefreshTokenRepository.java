package ru.cazyx.semperante.learnProject.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.cazyx.semperante.learnProject.entities.RefreshToken;

/**
 * Репозитория Refresh-Токенов
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

   /**
    * Запрос для удаления истекших токенов
    * Все запросы, описываемые в репозитории, которые вносят правки в БД, должны помечаться аннотацией {@link Modifying}
    */
   @Modifying
   @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < CURRENT_TIMESTAMP")
   void deleteExpired();
}
