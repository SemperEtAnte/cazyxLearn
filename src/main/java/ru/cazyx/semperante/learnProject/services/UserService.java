package ru.cazyx.semperante.learnProject.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.cazyx.semperante.learnProject.config.filters.JwtUtils;
import ru.cazyx.semperante.learnProject.dto.requests.AuthRequest;
import ru.cazyx.semperante.learnProject.dto.requests.RegisterRequest;
import ru.cazyx.semperante.learnProject.dto.responses.AuthorizationResponse;
import ru.cazyx.semperante.learnProject.entities.LearnUser;
import ru.cazyx.semperante.learnProject.entities.RefreshToken;
import ru.cazyx.semperante.learnProject.entities.repositories.LearnUserRepository;
import ru.cazyx.semperante.learnProject.entities.repositories.RefreshTokenRepository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для роутов пользователя
 */
@Service
public class UserService extends AParentService {
   private final PasswordEncoder encoder;
   private final LearnUserRepository userRepository;
   private final JwtUtils jwtUtils;
   private final RefreshTokenRepository refreshTokenRepository;

   /**
    * Конструктор для спринга
    *
    * @param encoder                Кодировщик пролей, который мы настроили как @Bean в {@link ru.cazyx.semperante.learnProject.config.SecurityConfig}
    * @param userRepository         Репозитория пользователей
    * @param jwtUtils               Утилити для работы с JWT (нужно для кодировки)
    * @param refreshTokenRepository Репозитория с токенами для перевыпуска.
    */
   public UserService(PasswordEncoder encoder, LearnUserRepository userRepository, JwtUtils jwtUtils, RefreshTokenRepository refreshTokenRepository) {
      this.encoder = encoder;
      this.userRepository = userRepository;
      this.jwtUtils = jwtUtils;
      this.refreshTokenRepository = refreshTokenRepository;
   }

   /**
    * Получить текущего пользователя.
    *
    * @return Пользователь
    */
   public LearnUser getMe() {
      return getAuthorizedUser(); //Просто берем пользователя из контекста
   }

   /**
    * Регистрация нового пользователя. Внутри транзакции, что логично
    *
    * @param request Запрос на регистрациею
    * @return Пользователь
    */
   @Transactional
   public LearnUser register(RegisterRequest request) {
      //Проверяем что пароль и его повторение совпадают, иначе - 400
      if (!request.password().equals(request.password_confirmation())) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords not matches");
      }
      //Запрашиваем в БД пользователя с таким же логигом или почтой
      Optional<LearnUser> userOpt = userRepository.findByCredentials(request.login(), request.email());

      //Если ответ не пустой, значит данные заняты - 400.
      if (userOpt.isPresent()) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login or email are used");
      }
      //Сохраняем нового пользователя и возвращаем на фронт.
      return userRepository.save(new LearnUser(request.login(), request.email(), encoder.encode(request.password()), request.role()));
   }

   /**
    * Авторизация пользователя
    *
    * @param request Тело запроса
    * @return Ответ авторизации
    */
   @Transactional
   public AuthorizationResponse auth(AuthRequest request) {
      //Ищем пользователя по данным авторизации (логин или почта), если такого нет - 404.
      LearnUser user = userRepository.findByCredentials(request.login()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
      //Проверяем введенный пароль. Если он неверный - 401
      if (!encoder.matches(request.password(), user.getPasswordDigest())) {
         throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password is invalid");
      }
      //Создаем строку которая будет токеном для перевыпуска
      String refresh = generateRefreshToken();
      //Сохраняем новый токен для перевыпуска в БД
      refreshTokenRepository.save(new RefreshToken(user, refresh));

      //Кодируем ИД пользователя в JWT и возвращаем ответ авторизации
      return new AuthorizationResponse(jwtUtils.encode(user.getId()), refresh);
   }

   /**
    * Перевыпуск JWT
    *
    * @param refreshToken Токен для перевыпуска
    * @return Новый ответ авторизации (новые токены)
    */
   @Transactional
   public AuthorizationResponse doReset(String refreshToken) {
      //Ищем заданный токен в БД. Если его нет - 404
      RefreshToken token = refreshTokenRepository.findById(refreshToken).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad refresh token"));
      //Сразу удаляем использованный токен из БД.
      refreshTokenRepository.delete(token);
      //На всякий проверяем, что он не истек (на случай, если 5 минут назад он ещё был валидный)
      if (token.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))) {
         throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is expired");
      }
      //Берем пользователя из токены (по зависимости многие к 1)
      LearnUser user = token.getUser();
      //Генерируем строку для нового токена
      String newToken = generateRefreshToken();
      //Сохраняем новый токен
      refreshTokenRepository.save(new RefreshToken(user, newToken));
      //Записываем ИД пользователя в JWT и возвращем новые токены.
      return new AuthorizationResponse(jwtUtils.encode(user.getId()), newToken);
   }

   /**
    * Выход из учетки. По сути просто делаем не валидным токен для сброса.
    * Передаваемый JWT нет смысла делать не валидным, т.к. он все равно умрет через 5 минут.
    *
    * @param refreshToken Токен для перевыпуска JWT.
    */
   public void doLogout(String refreshToken) {
      refreshTokenRepository.deleteById(refreshToken);
   }

   /**
    * Метод генерации токена для перевыпуска JWT
    *
    * @return Строка с токеном. По сути просто 2 UUID склеенных через "-", в теории должно дать достаточную уникальность, чтобы не было коллизий.
    * Можно применить другую логику - воля автора.
    */
   private static String generateRefreshToken() {
      return UUID.randomUUID() + "-" + UUID.randomUUID();
   }
}
