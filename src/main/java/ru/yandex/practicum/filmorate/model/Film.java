package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Set<Integer> likeUsers; // список пользователей, кто поставил лайк
    private Set<Genre> genres;
    private Rating mpa;

    public void addLike(Integer userId) {
        this.likeUsers.add(userId);
    }

    public void deleteLike(Integer userId) {
        this.likeUsers.remove(userId);
    }
}
