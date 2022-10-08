package ru.cazyx.semperante.learnProject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Класс-Сущность нашего пользователя
 * <p>
 * Такие классы помечаются аннотацией @Entity и говорят о том, что данные этого класса хранятся в таблице БД.
 * Название таблицы задается в аннотации @Table
 * <p>
 * Мы наследуем интерфейс UserDetails чтобы данная модель распознавались спрингом как пользователь, который может быть авторизован
 */
@Entity
@Table(name = "learn_users")
public class LearnUser implements UserDetails {
   /**
    * Поле ID. Должно быть у всех сущностей. Помечается аннотацией @Id
    * <p>
    * Аннотацией @GeneratedValue описывает метод генерации ID в нашем случае мы используем стратегию SEQUENCE.
    * При использовании этой стратегии спринг будет запрашивать и БД значение соответствующего счетчика в БД и присваивать его модели до внесения в Базу
    * Если выделить достаточный allocationSize (например 20-30) можно хорошо оптимизировать большие сохранения данных.
    * Например, если за 1 запрос к бэку, нам нужно внести в БД 20 записей, то при allocationSize = 20 спринг 1 раз запросит следующее значение счетчика
    * и сам присвоит 20 моделям ИД, после чего внесет их в БД. Не будет затрачено время на взятие ИД из БД для присвоения ИД в сохраняемый объект.
    * <p>
    * Также эта стратегия может использоваться с batch запросами, в отличие от IDENTITY.
    * <p>
    * В аннотации @SequenceGenerator мы описываем счетчик, который имеется в БД. У PostgreSQL его название: имяТаблицы_id_seq
    * <p>
    * Обратите внимание, что @GeneratedValue.generator == @SequenceGenerator.name
    * В первом мы задаем имя генератора, во втором, собственно, создаем генератор с этим именем
    */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "learn_users_seq")
   @SequenceGenerator(name = "learn_users_seq", sequenceName = "learn_users_id_seq", allocationSize = 1)
   private Long id;
   /**
    * Логин нашего пользователя.
    */
   private String login;
   /**
    * Почта пользователя (и доп проверка на валидность email)
    */
   @Email
   private String email;
   /**
    * Захэшированный пароль нашего пользователя.
    * <p>
    * Аннотаций @JsonIgnore говорит о том, что при переводе модели в JSON (перед отправкой на фронт) это поле будет проигнорировано.
    */
   @JsonIgnore
   private String passwordDigest;

   /**
    * Роль.
    * <p>
    * Аннотация @Enumerated задает логику чтения и записи значений enum'а из/в БД. STRING - означает что будет использоваться имя поля.
    * <p>
    * Подробнее можно почитить в документации к этой аннотации  {@link Enumerated}
    */
   @Enumerated(value = EnumType.STRING)
   private UserRole role;

   /**
    * Время регистрации пользователя
    * <p>
    * Аннотация @CreationTimestamp говорит о том, что это поле будет означать дату добавления записи в БД
    * <p>
    * Обратите так же внимание, что в БД поле называется registered_at (snake-case), а здесь используется camelCase версия
    * Спринг сам прекрасно справляется с такими преобразованиями, но для надежности можно накинуть @Column
    */
   @CreationTimestamp
   private Timestamp registeredAt;

   /**
    * Сообщения пользователя.
    * <p>
    * Отношение "один ко многим" задается аннотацией @OneToMany.
    * <p>
    * mappedBy - переменная в модели {@link UserMessage} отвечающая за обратную связь
    * <p>
    * cascade - тип каскадных зависимостей (их много разных, дока в помощь)
    * <p>
    * Также игнорируем при выдаче на фронт, т.к. зависимость является LAZY и при записи в JSON будет ошибка "ленивой" переменной
    */

   @OneToMany(mappedBy = "learnUser", cascade = CascadeType.ALL)
   @JsonIgnore
   private List<UserMessage> messages = new ArrayList<>();

   /**
    * Аналогично сообщениям
    */

   @OneToMany(mappedBy = "learnUser", cascade = CascadeType.ALL)
   @JsonIgnore
   private List<RefreshToken> refreshTokens = new ArrayList<>();


   /**
    * Публичный конструктор по-умолчанию.
    * <b>Обязательно должен присутствовать в сущностях</b> иначе спринг просто не сможет инициализировать класс для чтения
    */
   public LearnUser() {
   }

   /**
    * Конструктор используемый нами для создания сущности
    *
    * @param login          Логин
    * @param email          Почта
    * @param passwordDigest Захэшированный пароль
    * @param role           Роль
    */
   public LearnUser(String login, String email, String passwordDigest, UserRole role) {
      this.login = login;
      this.email = email;
      this.passwordDigest = passwordDigest;
      this.role = role;
   }

   //Ниже идут геттеры и сеттеры полей

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getLogin() {
      return login;
   }

   public void setLogin(String login) {
      this.login = login;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   @JsonIgnore
   public String getPasswordDigest() {
      return passwordDigest;
   }

   public void setPasswordDigest(String passwordDigest) {
      this.passwordDigest = passwordDigest;
   }

   public UserRole getRole() {
      return role;
   }

   public void setRole(UserRole role) {
      this.role = role;
   }

   public Timestamp getRegisteredAt() {
      return registeredAt;
   }

   public void setRegisteredAt(Timestamp registeredAt) {
      this.registeredAt = registeredAt;
   }

   @JsonIgnore
   public List<UserMessage> getMessages() {
      return messages;
   }

   public void setMessages(List<UserMessage> messages) {
      this.messages = messages;
   }

   @JsonIgnore
   public List<RefreshToken> getResetTokens() {
      return refreshTokens;
   }

   public void setResetTokens(List<RefreshToken> refreshTokens) {
      this.refreshTokens = refreshTokens;
   }

   /**
    * Данный метод используется спрингом для получения прав нашего пользователя.
    * Обратите внимание, что все роли, т.е. то, что в настройках Security описывается в hasRole или hasAnyRole должно начинаться с префикса ROLE_
    *
    * @return Коллекция ролей (в нашем случае лист на 1 элемент)
    */
   @Override
   @JsonIgnore
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
   }

   //Ниже идут поля наследуемые от UserDetails. Используются для внутренних нужд SpringSecurity, потому особо важного сказать нечего.
   @Override
   @JsonIgnore
   public String getPassword() {
      return passwordDigest;
   }

   @Override
   @JsonIgnore
   public String getUsername() {
      return login;
   }


   @Override
   @JsonIgnore
   public boolean isAccountNonExpired() {
      return true;
   }

   @Override
   @JsonIgnore
   public boolean isAccountNonLocked() {
      return true;
   }

   @Override
   @JsonIgnore
   public boolean isCredentialsNonExpired() {
      return true;
   }

   @Override
   @JsonIgnore
   public boolean isEnabled() {
      return true;
   }

   /**
    * Роли пользователей.
    */


   public enum UserRole {
      /**
       * Обычный пользователь. Имеет доступ к роутам /v1/user, а также созданию и просмотру сообщений.
       */
      USER,
      /**
       * Модератор. Имеет те же права, что и пользователь + возможность редактировать сообщения.
       */
      MODERATOR,
      /**
       * Администратор. Имеет те же права, что и модератор + возможность удалять сообщения.
       */
      ADMIN
   }

}
