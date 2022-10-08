package ru.cazyx.semperante.learnProject.dto.responses;

import ru.cazyx.semperante.learnProject.entities.LearnUser;

import java.sql.Timestamp;

/**
 * Ответ на запросы сообщений
 *
 * @param id         ИД сообщения
 * @param message    Текст сообщения
 * @param sentAt     Когда отправлен
 * @param authorName Логин автора
 * @param authorRole Роль автора
 */
public record MessageResponse(Long id, String message, Timestamp sentAt, String authorName, LearnUser.UserRole authorRole) {
}
