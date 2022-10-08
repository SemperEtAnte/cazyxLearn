package ru.cazyx.semperante.learnProject.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.cazyx.semperante.learnProject.config.filters.JwtFilter;

//Настройки для Сваггера (юрлы куда будут кидаться запросы и тэги внутри доки)
@OpenAPIDefinition(servers = {
        @Server(url = "http://127.0.0.1:8080", description = "local"),
},
        tags = {
                @Tag(name = "Сообщения", description = "Роуты связанные с сообщениями"),
                @Tag(name = "Пользователь", description = "Роуты учетки пользователей"),
                @Tag(name = "Модератор", description = "Роуты для модераторов"),
                @Tag(name = "Администратор", description = "Роуты для администраторов"),
        }
)
@Configuration
//Включаем фичу WebSecurity
@EnableWebSecurity
//Инициализируем схему защиту (говорим сваггеру, что существует такая схемка для передачи в заголовке)
@SecurityScheme(name = "auth", paramName = "Authorization", scheme = "bearer", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)

public class SecurityConfig {
   private final JwtFilter filter;

   /**
    * Конструктор для спринга
    *
    * @param filter Класс-Фильтр для JWT инициализирующийся инъекцией зависимостей
    */
   public SecurityConfig(JwtFilter filter) {
      this.filter = filter;
   }

   /**
    * Создаем кодировщик паролей
    * <p>
    * Аннотация @Bean - говорит о том, что объект, возвращаемый этим метод необходимо добавить в список классов, подходящих для инъекций
    *
    * @return Класс, который будет хэшировать пароли. В данном случае алгоритмом будет BCrypt
    */
   @Bean
   public PasswordEncoder getEncoder() {
      return new BCryptPasswordEncoder();
   }

   /**
    * Редактируем цепочку фильтров
    *
    * @param http Текущее состояние настроек защиты
    * @return Измененная цепочка
    * @throws Exception Любые ошибки?..
    */
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.cors() //Включаем CORS
              .and().httpBasic().disable() //Выключаем стандартное требование HTTP-basic авторизации
              .csrf().disable()//Выключаем CSRF
              .authorizeRequests() //Начинаем настройку авторизации запросов
              .antMatchers( //Любой запрос подходящий регуляркам из списка
                      "/v1/user/refresh-token", //Запрос перевыпуска токена
                      "/v1/user/login", //Запрос авторизации
                      "/v1/user/logout", //Запрос выхода из учетки
                      "/v1/user/register", //Запрос Регистрации
                      "/api-docs-op/**", //Документация
                      "/api-docs/**", // Документация
                      "/swagger-ui/**" //Документация
              ).permitAll() //permitAll() - Не требуют авторизации

              .antMatchers("/v1/**/moderator/**")//Запросы подходящие регулярке /v1/.*/moderator/.*
              .hasAnyRole("MODERATOR", "ADMIN")//Требуют роли MODERATOR или ADMIN

              .antMatchers("/v1/**/admin/**")//Запросы вида /v1/.*/admin/.*
              .hasRole("ADMIN") //Требуют роли админа
              .anyRequest().authenticated() //Остальные запросы, не подходящие под описанные в блоках antMatches, подходят любой АВТОРИЗОВАННОЙ роли
              .and()
              .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);//Добавляем наш JWT фильтр перед стандартным фильтром проверки авторизации запросов
      return http.build();
   }
}
