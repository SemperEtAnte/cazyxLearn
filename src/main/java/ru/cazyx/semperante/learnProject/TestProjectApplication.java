package ru.cazyx.semperante.learnProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения. По сути всегда имеет такой вид как здесь, т.к. все остальное разумно описывать уже в других классах.
 */
@SpringBootApplication
public class TestProjectApplication {

   public static void main(String[] args) {
      SpringApplication.run(TestProjectApplication.class, args);
   }

}
