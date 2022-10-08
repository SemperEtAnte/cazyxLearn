package ru.cazyx.semperante.learnProject.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.cazyx.semperante.learnProject.entities.LearnUser;

import java.util.Optional;

/**
 * Репозитория для работы с пользователями.
 * По сути представляет собой прослойку между нашим кодом и БД.
 * В этом интерфейсе описываются необходимые методы, спринг за нас реализует класс, который будет содержать логику
 * <p>
 * Наследуем {@link JpaRepository} которая содержит основные функции для работы.
 * <p>
 * Левый параметр - Entity для которого мы создаем репозиторию
 * <p>
 * Правый параметр - Тип представляющий ID в этой модели
 * <p>
 * В этом интерфейсе мы можем как переопределить заложенные в родителях запросы, так и написать свои
 * <p>
 * Для написания своих используется аннотация {@link Query}
 * <p>
 * Внутри неё пишутся запросы на языке JPQL - псевдоязык который потом JPA переведет в обычный SQL
 * <p>
 * Можно писать и "чистые" SQL запросы, но тогда надо указать в аннотации {@link Query#nativeQuery()} = true;
 */
public interface LearnUserRepository extends JpaRepository<LearnUser, Long> {
   /**
    * Поиск пользователя по логину (почте)
    *
    * @param login Логин введенный для авторизации
    * @return null-safe ответ с пользователем (или без)
    */
   @Query("SELECT u FROM LearnUser u WHERE lower(u.login) = lower(?1) OR lower(u.email)=lower(?1)")
   Optional<LearnUser> findByCredentials(String login);

   /**
    * Поиск по логину или почте (отдельно). Используется для проверки "занятости" полей.
    *
    * @param login Логин
    * @param email Почта
    * @return null-safe ответ с пользователем (или без)
    */
   @Query("SELECt u FROM LearnUser  u WHERE lower(u.login) = lower(?1) OR lower(u.email) = lower(?2)")
   Optional<LearnUser> findByCredentials(String login, String email);


}
