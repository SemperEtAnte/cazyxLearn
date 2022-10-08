package ru.cazyx.semperante.learnProject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cazyx.semperante.learnProject.dto.requests.SingleValueRequest;
import ru.cazyx.semperante.learnProject.dto.responses.MessageResponse;
import ru.cazyx.semperante.learnProject.services.MessageService;

import javax.validation.Valid;

//Контроллер REST API
@RestController
/*
   Префикс всех запросов обслуживаемых этим контроллером
   т.е. роуты будут иметь вид http://127.0.0.1:8080/v1/messages...
*/
@RequestMapping(value = "/v1/messages", produces = MediaType.APPLICATION_JSON_VALUE)
//Указываем сваггеру, что всем роутам в этом контроллере нужна авторизация описанная в аннотации над SecurityManager
@SecurityRequirement(name = "auth")

/**
 * Контроллер с запросами дла сообщений.
 * Обычно контроллеры используются для описания запросов и ответов
 * В них самих никакой особой логики нет. Прием запроса - передача его в сервис.
 * Получение ответа от сервиса и, если нужно, запаковка его в вид нужный для ответа
 */
public class MessageController {

   private final MessageService messageService;

   /**
    * Конструктор для спринга
    *
    * @param messageService Сервис, который будет реализовывать логику контроллера
    */
   public MessageController(MessageService messageService) {
      this.messageService = messageService;
   }


   /**
    * Метод создания нового сообщения от имени авторизованного пользователя
    *
    * @param message Тело запроса с сообщением
    *                Обратим внимание на аннотации перед параметром:
    *                Аннотация @RequestBody - говорит о том, что данный параметр передается в теле запроса
    *                Аннотация @Valid - говорит о том, что тело запроса должно автоматически проверяться на валидность
    * @return {@link ResponseEntity} - отвечает за возвращение ответа в формате JSON.
    */
   @PostMapping("") //POST запрос в корень контроллера (т.е. POST http://127.0.0.1:8080/v1/messages/)
   @Operation(summary = "Отправить сообщение", tags = "Сообщения") //Описание для сваггера
   public ResponseEntity<MessageResponse> doCreate(@RequestBody @Valid SingleValueRequest<String> message) {
      return ResponseEntity.ok(messageService.create(message.value())); //Вызываем метод в сервисе
   }

   /**
    * Запрос возвращающий список сообщений с пагинацией и сортировкой по времени отправки DESC (сначала новые)
    *
    * @param limit QUERY параметр задающий количество сообщений на странице
    * @param page  QUERY параметр задающий индекс страницы (начиная с 0)
    *              <p>
    *              Query параметры описываются аннотацией @RequestParam внутри которого указывается обязателен ли параметр.
    *              В нашем случае параметры необязательны, но заданы значения по-умолчанию 20 и 0 соотв.
    * @return "Страницу" сообщений - по сути список сообщений с полями необходимыми для пагинации
    */
   @GetMapping("") //GET запрос в корень контроллера (GET http://127.0.0.1/v1/messages/)
   @Operation(summary = "Получение списка сообщений с пагинацией", tags = "Сообщения") //Описание для сваггера
   public ResponseEntity<Page<MessageResponse>> doList(
           @RequestParam(required = false, defaultValue = "20") Integer limit,
           @RequestParam(required = false, defaultValue = "0") Integer page
   ) {
      return ResponseEntity.ok(messageService.getMessages(limit, page));
   }

   /**
    * Админский роут удаления сообщений. Как видно из маппинга тут уже будет путь кроме префикса,
    * Например: http://127.0.0.1:8080/v1/messages/admin/delete/10
    *
    * @param id Переменная задаваемая в пути (в роутинге) указывает на ID сообщения которое необходимо удалить
    * @return Пустое тело (Void)
    */
   @DeleteMapping("/admin/delete/{id}")
   @Operation(summary = "Полное удаление сообщения", tags = "Администратор")
   public ResponseEntity<Void> doDelete(@PathVariable Long id) {
      messageService.deleteMessage(id);
      return ResponseEntity.ok().build();
   }

   /**
    * Роут модератора для редактирования сообщений
    *
    * @param id      ИД сообщения
    * @param message Новый текст сообщения
    * @return Измененное сообщение
    */

   @PutMapping("/moderator/edit/{id}")
   @Operation(summary = "Редактирование текста сообщения", tags = "Модератор")
   public ResponseEntity<MessageResponse> doEdit(@PathVariable Long id, @RequestBody @Valid SingleValueRequest<String> message) {
      return ResponseEntity.ok(messageService.edit(id, message.value()));
   }
}
