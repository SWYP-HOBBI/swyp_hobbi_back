package swyp.hobbi.swyphobbiback.token.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp.hobbi.swyphobbiback.token.dto.ReissueRequest;
import swyp.hobbi.swyphobbiback.token.dto.ReissueResponse;
import swyp.hobbi.swyphobbiback.token.service.ReissueService;

@RestController
@RequestMapping("api/v1/token")
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(HttpServletRequest request) {
        ReissueResponse response = reissueService.reissue(request);
        return ResponseEntity.ok(response);
    }
}
