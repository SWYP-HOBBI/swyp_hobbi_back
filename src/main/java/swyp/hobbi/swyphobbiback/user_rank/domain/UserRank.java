package swyp.hobbi.swyphobbiback.user_rank.domain;

import jakarta.persistence.*;
import lombok.*;
import swyp.hobbi.swyphobbiback.user.domain.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Integer level;

    @Column(name = "rank_name", nullable = false)
    private RankType rank;

    @Column(nullable = false)
    private Integer exp; //현재 레벨 내 경험치

    public enum RankType {
        RED_HOBBI("레드 호비"),
        ORANGE_HOBBI("오렌지 호비"),
        YELLOW_HOBBI("옐로우 호비"),
        GREEN_HOBBI("그린 호비"),
        BLUE_HOBBI("블루 호비"),
        NAVY_HOBBI("네이비 호비"),
        PURPLE_HOBBI("퍼플 호비"),
        WHITE_HOBBI("화이트 호비"),
        BLACK_HOBBI("블랙 호비"),
        RAINBOW_HOBBI("레인보우 호비");

        private final String displayName;

        RankType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public void updateExp(int amount) {
        if (level >= 100) return;

        this.exp += amount;

        while (level < 100) {
            int required = getRequiredExpForLevel();

            if (this.exp < required) break;

            this.exp -= required;
            this.level++;

            // 레벨이 10의 배수일 때 랭크도 같이 올리기
            if (this.level % 10 == 0 && this.rank.ordinal() < RankType.values().length - 1) {
                this.rank = RankType.values()[this.rank.ordinal() + 1];
            }
        }
    }

    public int getRequiredExpForLevel() {
        double base = 50;
        for (int i = 0; i < 10; i++) {
            int minLevel = i * 10 + 1;
            int maxLevel = i * 10 + 9;
            int requiredExp = (int) base;

            if (this.level >= minLevel && this.level <= maxLevel) {
                return requiredExp;
            }
            base *= 1.5;
        }
        return 0;
    }

    public int getCurrentExpInLevel() {
        return this.exp;
    }

    public int getProgressPercent() { //경험치 비율
        int required = getRequiredExpForLevel();
        return (required == 0) ? 100 : (int) (((double) this.exp / required) * 100);
    }
}