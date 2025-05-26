package swyp.hobbi.swyphobbiback.user_rank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import swyp.hobbi.swyphobbiback.user_rank.domain.UserRank;

@Getter
@Builder
@AllArgsConstructor
public class UserRankResponseDto {

    private Integer level;
    private Integer currentExp;
    private Integer requiredExp;
    private Integer progressPercent;
    private String rankName;

    public static UserRankResponseDto from(UserRank userRank) {
        return UserRankResponseDto.builder()
                .level(userRank.getLevel())
                .currentExp(userRank.getCurrentExpInLevel())
                .requiredExp(userRank.getRequiredExpForLevel())
                .progressPercent(userRank.getProgressPercent())
                .rankName(userRank.getRank().getDisplayName())
                .build();
    }
}