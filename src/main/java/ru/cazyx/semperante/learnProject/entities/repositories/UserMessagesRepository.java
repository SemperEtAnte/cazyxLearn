package ru.cazyx.semperante.learnProject.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.cazyx.semperante.learnProject.entities.UserMessage;

/**
 * Репозитория отвечающая за работу сообщениями.
 * Никакой особой логики для них мы не реализуем, потому стандартного набора, заложенного у родителей, нам хватит.
 */
public interface UserMessagesRepository extends JpaRepository<UserMessage, Long> {

}
