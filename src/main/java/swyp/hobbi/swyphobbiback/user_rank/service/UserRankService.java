package swyp.hobbi.swyphobbiback.user_rank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user_rank.domain.UserRank;
import swyp.hobbi.swyphobbiback.user_rank.dto.UserRankResponseDto;
import swyp.hobbi.swyphobbiback.user_rank.repository.UserRankRepository;

@Service
@RequiredArgsConstructor
public class UserRankService {

    private final UserRankRepository userRankRepository;

    public UserRankResponseDto getUserRank(User user) {
        UserRank userRank = userRankRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserRankResponseDto.from(userRank);
    }
}
