package ru.cazyx.semperante.learnProject.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Роут сообщений пользователя
 * <p>
 * Нового тут сказать нечего, смотрите описания внутри других сущностей.
 */
@Entity
@Table(name = "learn_user_messages")
public class UserMessage {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "learn_user_messages_seq")
   @SequenceGenerator(name = "learn_user_messages_seq", sequenceName = "learn_user_messages_id_seq", allocationSize = 1)
   private Long id;

   @ManyToOne
   @Fetch(FetchMode.JOIN)
   @JoinColumn(name = "author_id")
   private LearnUser learnUser;

   private String message;
   @CreationTimestamp
   @Column(name = "sent_at")
   private Timestamp sentAt;

   public UserMessage() {
   }

   public UserMessage(LearnUser learnUser, String message) {
      this.learnUser = learnUser;
      this.message = message;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public LearnUser getUser() {
      return learnUser;
   }

   public void setUser(LearnUser learnUser) {
      this.learnUser = learnUser;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Timestamp getSentAt() {
      return sentAt;
   }

   public void setSentAt(Timestamp sentAt) {
      this.sentAt = sentAt;
   }
}
