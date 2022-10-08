package ru.cazyx.semperante.learnProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Файл настройки работы CORS (Cross-Origin requests)
 * По сути всё что он делает, возвращает на CORS запросы заголовки Access-Control-:
 * Allow-Methods: *
 * Allow-Origins: *
 * Allow-Headers: *
 * Expose-Headers: Authorization, Authorization-Refresh
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
   /**
    * Сама настройка
    * @param registry Внутренний спринговский регистр CORS
    */
   @Override
   public void addCorsMappings(CorsRegistry registry) {
      registry
              .addMapping("/**") //Любой путь (роутинг)
              .allowedMethods("*") //Разрешаем все методы (GET, POST, PUT, DELETE, ...)
              .allowedOrigins("*") //Разрешаем любые исходные домены (origin)
              .allowedHeaders("*") //Разрешаем все заголовки
              .exposedHeaders("Authorization", "Authorization-Refresh"); //Хэдеры которые необходимо, чтобы браузер разрешил у себя.
   }
}
