package swyp.hobbi.swyphobbiback.common.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.common.repository.PostConstructRepository;

@Service
@RequiredArgsConstructor
public class PostConstructService {
    private final PostConstructRepository postConstructRepository;

    @PostConstruct
    public void init() {
        postConstructRepository.insertHobbyTag();
    }
}
