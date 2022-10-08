package ru.cazyx.semperante.learnProject.services;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.cazyx.semperante.learnProject.entities.LearnUser;

/**
 * Сервис-родитель всех сервисов
 * <p>
 * Удобно для хранения общих методов, как например взятие текущего пользователя из контекста и подобное
 * <p>
 * Учтите, что кэширование тут использовать бессмысленно, так же как и транзакции, асинхронность и т.д. Э
 * Это связано с прокси-версиями класса.
 * Такое в двух словах не расскажешь, просто запомним, что если нужно кэшировать какие-то общие штуки, лучше сделать отдельый сервис под это.
 */
public abstract class AParentService {

   protected static LearnUser getAuthorizedUser() {
      return (LearnUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
   }

}
