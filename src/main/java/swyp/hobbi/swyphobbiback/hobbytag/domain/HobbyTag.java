package swyp.hobbi.swyphobbiback.hobbytag.domain;

import jakarta.persistence.*;
import lombok.*;
import swyp.hobbi.swyphobbiback.user.domain.UserHobbyTag;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hobby_tag")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class HobbyTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hobbyTagId;

    @Column(nullable = false)
    private String hobbyTagName;

    @Column(nullable = false)
    private String hobbyType;

//    @OneToMany(mappedBy = "hobbyTag", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PostHobbyTag> postHobbyTags = new ArrayList<>();

    @OneToMany(mappedBy = "hobbyTag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserHobbyTag> userHobbyTags = new ArrayList<>();
}
