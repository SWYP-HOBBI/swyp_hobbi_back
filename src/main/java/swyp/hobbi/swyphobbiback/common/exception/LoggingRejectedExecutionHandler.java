package swyp.hobbi.swyphobbiback.common.exception;

import lombok.extern.slf4j.Slf4j;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.error("작업이 거부되었습니다. 현재 작업 수 : {}", executor.getActiveCount());
        log.error("큐의 크기 : {}", executor.getQueue().size());

        throw new CustomException(ErrorCode.REJECTED_BY_THREAD_POOL);
    }
}
