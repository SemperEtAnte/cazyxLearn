package ru.cazyx.semperante.learnProject.dto.responses;

/**
 * Тело ответа запроса
 *
 * @param token        JWT авторизованного пользователя (будет передаваться в заголовке Authorizatino)
 * @param refreshToken Refresh-Токен для перевыпуска JWT.
 */
public record AuthorizationResponse(String token, String refreshToken) {
}
