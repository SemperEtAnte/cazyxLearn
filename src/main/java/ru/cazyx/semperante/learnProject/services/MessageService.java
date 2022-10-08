package ru.cazyx.semperante.learnProject.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.cazyx.semperante.learnProject.dto.responses.MessageResponse;
import ru.cazyx.semperante.learnProject.entities.LearnUser;
import ru.cazyx.semperante.learnProject.entities.UserMessage;
import ru.cazyx.semperante.learnProject.entities.repositories.UserMessagesRepository;

/**
 * Сервис реализующий логику роутов сообщений
 * <p>
 * Хочу обратить внимание, что при написании сервера не нужно возвращать ResponseEntity.
 * Или допустим если нам нужно будет вернуть на фронт Long, не нужно прям тут заворачивать его в класс-обертку.
 * Сервис должен возвращать String, long, boolean,... что-то, что уже контроллер сможет завернуть в ответ.
 * При соблюдении этого правила, обычно, проще рефакторить код для смены ответа.
 * <p>
 * Аннотация @Service говорит о том, что данный класс является сервисом.
 * Работает аналогично @Component
 */
@Service
public class MessageService extends AParentService {

   /**
    * Репозитория сообщений
    */
   private final UserMessagesRepository userMessagesRepository;

   /**
    * Конструктор для спринга
    *
    * @param userMessagesRepository Dependency Injection репозитории
    */
   public MessageService(UserMessagesRepository userMessagesRepository) {
      this.userMessagesRepository = userMessagesRepository;
   }

   /**
    * Метод создания сообщения
    * <p>
    * Помечен аннотацией @Transactional.
    * Это аннотация говорит спрингу о том, что всё, что происходит в этом методе, должно происходить внутри транзакции.
    * Т.е. если мы, допустим, сохраним что-то или удалим из БД, а далее при продолжении выполнении метода возникнет какая-то ошибка, то все изменения будут отменены.
    *
    * @param message Текст сообщения
    * @return Ответ с сообщением
    */
   @Transactional
   public MessageResponse create(String message) {
      LearnUser user = getAuthorizedUser(); //Берем авторизовавшего пользователя
      return messageToResponse(userMessagesRepository.save(new UserMessage(user, message))); //Сохраняем в репозиторию новое сообщение и возвращаем результат как ответ.
   }

   /**
    * Получить страницу сообщений
    * <p>
    * Если наш метод не будет вносить никакие правки в БД, вполне разумно указать это в аннотации @Transactional.
    * Если верить документации, то readOnly=true не запрещает вносить правки, но лучше оптимизирует транзакцию именно для чтения.
    *
    * @param limit Количество элементов на страницу
    * @param page  Индекс страницы (с нуля)
    * @return Страницу с сообщениями. Под страницей, имеется в виду не HTML страница, а список записей + сколько всего записей в БД и сколько страниц всего.
    */
   @Transactional(readOnly = true)
   public Page<MessageResponse> getMessages(Integer limit, Integer page) {
      /*
      Взятие страницы из репозитории, вызывая метод findAll.
      Обратите внимание, на то, что наш запрос оборачивается в PageRequest (Pageable если быть точнее).
      Мы указываем параметры страницы (страница, лимит, сортировка), в нашем случае мы сортируем от новых к старым.
       */
      Page<UserMessage> msg = userMessagesRepository.findAll(PageRequest.of(page, limit, Sort.by("sentAt").descending()));
      return msg.map(MessageService::messageToResponse); //Указываем, что для возвращения, элементы страницы нужно преобразовать при помощи методы messageToResponse
   }

   /**
    * Удалить сообщение из БД
    *
    * @param id ИД сообщения
    */
   @Transactional
   public void deleteMessage(Long id) {
      userMessagesRepository.deleteById(id);
   }

   /**
    * Редактировать сообщения
    *
    * @param id      ИД сообщения
    * @param message Новый текст сообщения
    * @return Модель ответа
    */
   @Transactional
   public MessageResponse edit(Long id, String message) {
      //Берем сообщения по ИД из БД. Если его нет - бросаем 404.
      UserMessage msg = userMessagesRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
      msg.setMessage(message); //Меняем текст сообщения
      return messageToResponse(userMessagesRepository.save(msg)); //Сохраняем, преобразуем, возвращаем.
   }

   /**
    * Статический метод преобразовывающий сущность сообщения в ответ для фронта
    *
    * @param message Объект сущности сообщения
    * @return Модель для фронта
    */
   private static MessageResponse messageToResponse(UserMessage message) {
      return new MessageResponse(message.getId(), message.getMessage(), message.getSentAt(), message.getUser().getUsername(), message.getUser().getRole());
   }


}
