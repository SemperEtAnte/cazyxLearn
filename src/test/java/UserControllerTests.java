import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.cazyx.semperante.learnProject.TestProjectApplication;
import ru.cazyx.semperante.learnProject.dto.requests.RegisterRequest;
import ru.cazyx.semperante.learnProject.entities.LearnUser;
import ru.cazyx.semperante.learnProject.entities.repositories.LearnUserRepository;

import javax.annotation.PostConstruct;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestProjectApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.flyway.enabled=false"
})

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTests {
    private String host;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LearnUserRepository learnUserRepository;

    @LocalServerPort
    private int port;

    @AfterEach
    public void console()
    {
        System.out.println("======================================gjkfdhghfdhgjhfdgjkdf====================");
        learnUserRepository.deleteAll(); // очистка БД перед каждым тестом
    }

    @Test
    @Order(1)
    public  void testRegisterUserSuccess () throws Exception
    {
        RegisterRequest registerRequest = new RegisterRequest("test", "test@test.test","12345678","12345678", LearnUser.UserRole.USER);
        mockMvc.perform(post(host + "/v1/user/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public  void testRegisterInvalidPassConfirmation () throws Exception
    {
        RegisterRequest registerRequest = new RegisterRequest("test1", "test1@test.test","12345678","1234567", LearnUser.UserRole.USER);
        mockMvc.perform(post(host + "/v1/user/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(3)
    public  void testGetUsers () throws Exception
    {
        assertEquals(0, learnUserRepository.findAll().size());
    }


    @PostConstruct
    public void setHost()
    {
        host = "http://localhost:" + port;
    }
}
