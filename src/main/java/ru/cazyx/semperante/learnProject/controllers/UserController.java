package ru.cazyx.semperante.learnProject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.cazyx.semperante.learnProject.dto.requests.AuthRequest;
import ru.cazyx.semperante.learnProject.dto.requests.RegisterRequest;
import ru.cazyx.semperante.learnProject.dto.responses.AuthorizationResponse;
import ru.cazyx.semperante.learnProject.entities.LearnUser;
import ru.cazyx.semperante.learnProject.services.UserService;

import javax.validation.Valid;

//Контроллер REST API запросов
@RestController
//Префикс всех запросов за который отвечает этот контроллер
@RequestMapping(value = "/v1/user")
//Тэг к которому относятся все запросы контроллера
@Tag(name = "Пользователь")
//Включаем валидацию для параметров запросов
@Validated
public class UserController {
   private final UserService userService;

   /**
    * Конструктор для спринга
    *
    * @param userService Сервис реализующий логику роутов
    */
   public UserController(UserService userService) {
      this.userService = userService;
   }

   /**
    * Получение модели пользователя по токену авторизации
    *
    * @return Модель пользователя
    */
   @GetMapping("/me")
   @SecurityRequirement(name = "auth")
   @Operation(summary = "Получить модель текущего пользователя")
   public ResponseEntity<LearnUser> getMe() {
      return ResponseEntity.ok(userService.getMe());
   }

   /**
    * Регистрация пользователя
    *
    * @param req Тело запроса для регистрации с валидацией
    * @return Модель пользователя
    */
   @PostMapping("/register")
   @Operation(summary = "Регистрация нового пользователя")
   public ResponseEntity<LearnUser> doRegister(@RequestBody @Valid RegisterRequest req) {
      return ResponseEntity.ok(userService.register(req));
   }

   /**
    * Авторизация
    *
    * @param request Тело запроса для авторизации с валидациями
    * @return Ответ авторизации
    */
   @PostMapping("/login")
   @Operation(summary = "Авторизация пользователя")
   public ResponseEntity<AuthorizationResponse> doLogin(@RequestBody @Valid AuthRequest request) {
      return ResponseEntity.ok(userService.auth(request));

   }

   /**
    * Выход.
    *
    * @param refreshToken Refresh-Токен. Передается в заголовке Authorization-Refresh.
    *                     Получение параметра из заголовка осуществляется при помощи аннотации RequestHeader
    *                     Сама аннотация аналогична @RequestParam и поле может быть помечено необязательным.
    * @return Пустое тело ответа
    */
   @PostMapping("/logout")
   @Operation(summary = "Выйти из учетной записи")
   public ResponseEntity<Void> doLogout(@RequestHeader(name = "Authorization-Refresh") String refreshToken) {
      userService.doLogout(refreshToken);
      return ResponseEntity.ok().build();
   }

   /**
    * Роут перевыпуска JWT по Refresh-Токену
    *
    * @param refreshToken Refresh-токен передаваемый в заголовке Authorization-Refresh
    * @return Такое же ответ как в запросе авторизации
    */
   @PostMapping("/refresh-token")
   @Operation(summary = "Перевыпустить токен авторизации при помощи токена-сброса")
   public ResponseEntity<AuthorizationResponse> doRefresh(@RequestHeader(name = "Authorization-Refresh") String refreshToken) {
      return ResponseEntity.ok(userService.doReset(refreshToken));
   }

}
