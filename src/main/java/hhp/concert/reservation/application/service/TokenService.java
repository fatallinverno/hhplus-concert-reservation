package hhp.concert.reservation.application.service;


import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

@Service
public class TokenService {

    private final Queue<TokenEntity> waitingQueue = new LinkedList<>();
    private final Queue<TokenEntity> readyQueue = new LinkedList<>(); // 입장 가능한 사용자 큐
    private static final int MAX_READY_QUEUE_SIZE = 50;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public TokenEntity generateToken(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        int queuePosition = waitingQueue.size() + 1;
        TokenEntity token = new TokenEntity();
        token.setUserEntity(user);  // UserEntity 설정
        token.setToken(jwtUtil.generateToken(userId, queuePosition));
        token.setQueuePosition(queuePosition);
        token.setIssuedAt(LocalDateTime.now());
        token.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        token.setValid(true);

        // 대기열에 추가
        waitingQueue.add(token);
        System.out.println("Added to waitingQueue: User ID " + userId + ", Current waiting size: " + waitingQueue.size());

        // 입장 가능한 큐로 이동
        if (readyQueue.size() < MAX_READY_QUEUE_SIZE) {
            moveToReadyQueue();
            System.out.println("Moved to readyQueue. Current ready size: " + readyQueue.size());
        }

        return token;
    }

    private void moveToReadyQueue() {
        if (waitingQueue.isEmpty()) {
            return;
        }
        TokenEntity nextToken = waitingQueue.poll();
        readyQueue.add(nextToken);
    }

    public TokenEntity getNextInQueue() {
        return readyQueue.peek();
    }

    public void processNextInQueue() {
        readyQueue.poll();
        moveToReadyQueue();
    }

    public int getQueuePosition(Long userId) {
        int position = 1;
        for (TokenEntity token : waitingQueue) {
            if (token.getUserEntity().getUserSeq().equals(userId)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public int getReadyQueuePosition(Long userId) {
        int position = 1;
        for (TokenEntity token : readyQueue) {
            if (token.getUserEntity().getUserSeq().equals(userId)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public int getReadyQueueSize() {
        return readyQueue.size();
    }

    public int getWaitingQueueSize() {
        return waitingQueue.size();
    }

}