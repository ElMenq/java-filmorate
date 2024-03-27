package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    @Email
    @NotBlank
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private Set<Integer> friends; // список друзей

    public void addFriend(Integer friendId) {
        this.friends.add(friendId);
    }

    public void deleteFriends(Integer friendId) {
        this.friends.remove(friendId);
    }
}
