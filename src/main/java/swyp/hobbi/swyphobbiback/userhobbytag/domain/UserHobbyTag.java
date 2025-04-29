package swyp.hobbi.swyphobbiback.userhobbytag.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp.hobbi.swyphobbiback.hobbytag.domain.HobbyTag;
import swyp.hobbi.swyphobbiback.user.domain.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHobbyTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userHobbyTagId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "hobby_tag_id")
    private HobbyTag hobbyTag;

    public UserHobbyTag(User user, HobbyTag hobbyTag) {
        this.user = user;
        this.hobbyTag = hobbyTag;
    }

}
