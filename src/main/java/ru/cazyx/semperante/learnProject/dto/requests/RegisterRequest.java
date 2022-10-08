package ru.cazyx.semperante.learnProject.dto.requests;

import ru.cazyx.semperante.learnProject.entities.LearnUser;

import javax.validation.constraints.*;

/**
 * Тело запроса регистрации
 *
 * @param login                 Логин (не пустой, от 3 до 16 символов, только латиница, цифры и знак _)
 * @param email                 Почта (не пустая, с валидацией, что задан корректный почтовый ящик)
 * @param password              Пароль (Не пустой от 8 до 16 символов)
 * @param password_confirmation Повторение пароля (Не пустой. Соответствие паролю проверяем уже в сервисе)
 * @param role                  Желаемая роль (не пустое)ы. Очевидно, тут этого поля быть не должно в нормальной системе
 *                              Но так как это учебный проект, для упрощения тестирования системы ролей, в принципе, сойдет.
 */
public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 16)
        @Pattern(regexp = "^\\w+$")
        String login,
        @Email
        @NotEmpty
        String email,
        @NotBlank
        @Size(min = 8, max = 16)
        String password,
        @NotBlank
        String password_confirmation,
        @NotNull
        LearnUser.UserRole role
) {
}
