package ru.cazyx.semperante.learnProject.dto.requests;

import javax.validation.constraints.NotNull;

/**
 * Параметризованный класс для запросов, содержащих одно поле
 *
 * @param value Значение поля в json: <code>
 *              {
 *              "value: значение
 *              }
 *              </code>
 * @param <T>   то, к какому классу должно относиться value (в примере всегда String, но может быть любой тип)
 */
public record SingleValueRequest<T>(@NotNull T value) {
}
