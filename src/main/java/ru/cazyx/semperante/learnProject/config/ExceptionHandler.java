package ru.cazyx.semperante.learnProject.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для изменения логики обработки (отправки) определенных ошибок
 *
 * @see ControllerAdvice - для более подробной информации, что это такое
 */
@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {
   /**
    * Ловим ошибку валидации запросов
    *
    * @param ex      Ошибка
    * @param headers Заголовки, которые будут записаны в ответ
    * @param status  Статус ответа
    * @param request Текущий запрос
    * @return Объект ответа
    */
   @Override
   protected @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(
           MethodArgumentNotValidException ex,
           @NotNull HttpHeaders headers,
           @NotNull HttpStatus status,
           @NotNull WebRequest request) {
      List<String> errors = new ArrayList<>(ex.getErrorCount()); //Список ошибок
      for (FieldError error : ex.getBindingResult().getFieldErrors()) { //Считываем ошибки полей
         errors.add(error.getField() + ": " + error.getDefaultMessage());
      }
      for (ObjectError error : ex.getBindingResult().getGlobalErrors()) { //Считываем глобальные ошибки
         errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
      }
      return new ApiError(HttpStatus.BAD_REQUEST, "Validation error", errors).buildResponseEntity(); //Записываем ошибки в класс-обертку и возвращаем
   }


   /**
    * Класс-обертка для наших ошибок
    */
   public static class ApiError {

      private HttpStatus status;
      private String message;
      private List<String> errors;

      /**
       * Конструктор
       *
       * @param status  статус ошибки
       * @param message Общее сообщение об ошибке
       * @param errors  Список более подробных сообщений (если нужно)
       */
      public ApiError(HttpStatus status, String message, List<String> errors) {
         this.status = status;
         this.message = message;
         this.errors = errors;
      }

      public HttpStatus getStatus() {
         return status;
      }

      public void setStatus(HttpStatus status) {
         this.status = status;
      }

      public String getMessage() {
         return message;
      }

      public void setMessage(String message) {
         this.message = message;
      }

      public List<String> getErrors() {
         return errors;
      }

      public void setErrors(List<String> errors) {
         this.errors = errors;
      }

      @JsonIgnore
      public ResponseEntity<Object> buildResponseEntity() {
         return ResponseEntity.status(this.getStatus()).body(this);
      }
   }
}
