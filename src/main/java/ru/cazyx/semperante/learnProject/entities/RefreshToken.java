package ru.cazyx.semperante.learnProject.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Класс-сущность токенов для перевыпуска JWT
 * <p>
 * Более подробно о сущностях смотреть в {@link LearnUser}
 */
@Entity
@Table(name = "learn_refresh_tokens")
public class RefreshToken {
   /**
    * Поле являющееся PRIMARY KEY.
    * Обратите внимание, что тут это String, причем не генерируемый автоматически.
    */
   @Id
   private String token;

   /**
    * Зависимость многие к одному с пользователями. Описывается аннотацией @ManyToOne
    * <p>
    * Внутри аннотации @Fetch мы описыавем метод чтения этой зависимости.
    * JOIN означает, что при любом запросе на получение модели RefreshToken запрос будет иметь INNER JOIN для чтения модели пользователя
    * <p>
    * Аннотация @JoinColumn указывает на колонку в БД содержащую ИД пользователя.
    */
   @ManyToOne
   @Fetch(FetchMode.JOIN)
   @JoinColumn(name = "user_id")
   private LearnUser learnUser;

   /**
    * Время истечения
    * <p>
    * Аннотация @Column позволяет указывать какая колонка соответствует переменной, является ли она обновляемой и сохраняемой.
    * Как правило, используется, если имя переменной отличается от названия колонки БД.
    */
   @Column(name = "expires_at")
   private Timestamp expiresAt;

   /**
    * Конструктор по-умолчанию
    */
   public RefreshToken() {
   }

   public RefreshToken(LearnUser learnUser, String token) {
      this.learnUser = learnUser;
      this.token = token;
      this.expiresAt = new Timestamp(System.currentTimeMillis() + 86400_000L);
   }


   public LearnUser getUser() {
      return learnUser;
   }

   public void setUser(LearnUser learnUser) {
      this.learnUser = learnUser;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public Timestamp getExpiresAt() {
      return expiresAt;
   }

   public void setExpiresAt(Timestamp expiresAt) {
      this.expiresAt = expiresAt;
   }
}
