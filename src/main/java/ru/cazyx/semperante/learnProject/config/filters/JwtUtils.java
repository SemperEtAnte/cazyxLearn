package ru.cazyx.semperante.learnProject.config.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

/**
 * Класс-утилити для шифрования и дешифрования JWT токенов
 */
@Component
public class JwtUtils {
   /**
    * Верификатор JWT - занимается чтением токена и проверкой ключа шифрования
    */
   private final JWTVerifier decoder;
   /**
    * Алгоритм шифрования. Тут используется HMAC512
    */
   private final Algorithm algo;

   /**
    * Конструктор компонента
    *
    * @param jwtSalt Соль нашего токена.
    *                Аннотация @Value говорит о том, что данное значение нужно брать из конфига по пути <i>spring.jwt.salt</i>
    *                Значение после двоеточия - значение по-умолчанию (testSalt) в данном случае
    */
   public JwtUtils(@Value("${spring.jwt.salt:testSalt}") String jwtSalt) {
      this.algo = Algorithm.HMAC512(jwtSalt); //Инициализируем алгоритм шифрования
      this.decoder = JWT.require(algo).build(); //Инициализируем декодер токенов
   }

   /**
    * Кодирование токена
    *
    * @param userId ID пользователя для которого токен кодируется
    * @return Строка - JWT токен
    */
   public String encode(Long userId) {
      return JWT.create() //Создаем кодировщик
              .withKeyId(String.valueOf(userId)) //Записываем ID пользователя (как строку в этом случае)
              .withExpiresAt(new Date(System.currentTimeMillis() + 300_000L)) //Задаем время жизни токена 5 минут (в миллисекундах)
              .sign(algo); //В конце задаем алгоритм "подписи" (шифрования) токена, после чего библиотека вернет которые токен.
   }

   /**
    * Декодирование токена
    *
    * @param jwt Токен пришедший в заголовке авторизации
    * @return ID пользователя который зашифрован в токена
    */
   public Long decode(String jwt) {
      try {
         var decoded = decoder.verify(jwt); //Декодируем токен. Декдор сам проверяет ключ подписи
         if (new Date().after(decoded.getExpiresAt())) { //Если дата истечения в токене "перед" текущей - токен истек
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired"); //Выбрасываем ошибку
         }
         return Long.parseLong(decoded.getKeyId()); //Если всё ок - берем ИД из токена и парсим его обратно в Long
      }
      catch (Throwable ex) { //В случае возникновения любых ошибок - токен считаем не валидным
         throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid");
      }

   }
}
