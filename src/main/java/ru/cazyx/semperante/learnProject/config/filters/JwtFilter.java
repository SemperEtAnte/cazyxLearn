package ru.cazyx.semperante.learnProject.config.filters;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.server.ResponseStatusException;
import ru.cazyx.semperante.learnProject.entities.LearnUser;
import ru.cazyx.semperante.learnProject.entities.repositories.LearnUserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр проверяющий наличие заголовка Authorization
 * Если заголовок есть - валидируем его как внутренний JWT и
 * -- если всё ок - передаем дальше в цепочку фильтров
 * -- если какая-то ошибка при чтении токена - возвращаем 401
 * Если заголовка нет - передаем дальше в цепочку фильтров
 * <p>
 * Component - означает, что класс является компонентом для Спринга.
 * В такие классы загружаются автоматически при запуске проекта, в них производится dependency-injection
 * Также спринг сможет и их инъектить в другие классы.
 */
@Component
public class JwtFilter extends GenericFilterBean {
   /**
    * Класс содержащий утилити для работы с JWT
    */
   private final JwtUtils utils;
   /**
    * Репозитория с пользователями
    */
   private final LearnUserRepository userRepository;

   /**
    * Конструктор в который спринг будет производить инъекцию зависимостей
    *
    * @param utils          Класс содержащий утилити для работы с JWT
    * @param userRepository Репозитория с пользователями
    */
   public JwtFilter(JwtUtils utils, LearnUserRepository userRepository) {
      this.utils = utils;
      this.userRepository = userRepository;
   }


   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      try {
         if (request instanceof HttpServletRequest req) { //Проверяем что запрос является HTTP и сразу кастим в переменную req
            String authorization = req.getHeader("Authorization"); //Берем заголовок Authorization из запроса
            if (authorization != null && !authorization.isBlank()) { //Если Не null и не пустой (пробельный)
               Long id = utils.decode(authorization); //Декодируем JWT

               //Берем пользователя из репозитория по ID из токена. Если его там нет - кидаем 401 (т.к. токен-то не валидный)
               LearnUser user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
               //Указываем контексту защиты, что авторизация прошла - кладем пользователя как principal модель авторизовавшегося
               SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            }
         }
         chain.doFilter(request, response); //Передаем запрос дальше по цепочке фильтров
      }
      /*
         В случае возникновения любой ошибки при чтении JWT прерываем цепочку.
         Т.к. данный фильтр находится в цепочке раньше фильтров, которые возвращают ResponseStatusException как JSON
         Нам необходимо руками записать JSON ответ-ошибку.
         По скольку для фронта, в основном, важно поле message - его и записываем.
         А так никто не запрещает записать ещё что-то.
      */
      catch (Throwable e) { //В случае возникновения любой ошибки при чтении JWT
         int status = 500; //Статус по-умолчанию
         String message = e.getMessage(); //Сообщение по-умолчанию
         if (e instanceof ResponseStatusException r) { //Если ошибка является "Предусмотренной" нами
            status = r.getRawStatusCode(); //Берем статус ошибки
            message = r.getReason(); //Берем текст ошибки
         }
         if (status == 500) { //Если ошибка - Internal Server Error
            e.printStackTrace(); //Выводим стак-трейс в консоль
         }
         if (response instanceof HttpServletResponse resp) { //Кастим ответ в HTTP
            resp.setStatus(status); //Записываем статус
            resp.setContentType("application/json"); //Типа ответа - JSON
            resp.getWriter().append("{ \"message\": \"") // Записываем ошибку.
                    .append(message)
                    .append("\"}").flush(); //flush() - отправляем
         }
      }
   }


}
