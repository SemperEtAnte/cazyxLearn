package ru.cazyx.semperante.learnProject.dto.requests;

import javax.validation.constraints.NotBlank;

/**
 * Запрос авторизации
 *
 * @param login    Логин (или почта). @NotBlank - не должно быть null или пустой строкой (в т.ч. заполненной только пробелами)
 * @param password Пароль. @NotBlank - не должно быть null или пустой строкой (в т.ч. заполненной только пробелами)
 */
public record AuthRequest(@NotBlank String login, @NotBlank String password) {
}
