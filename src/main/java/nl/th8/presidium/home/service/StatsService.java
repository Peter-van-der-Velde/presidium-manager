package nl.th8.presidium.home.service;

import nl.th8.presidium.home.controller.dto.StatDTO;
import nl.th8.presidium.home.data.KamerstukRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    @Autowired
    KamerstukRepository repository;

    public StatDTO getStats() {
        long kamerstukken = repository.count();
        long queue = repository.countAllByPostDateIsNullAndDeniedIsFalse() + repository.countAllByPostDateIsNotNullAndPostedIsFalse();
        long queueVote = repository.countAllByPostedIsTrueAndVotePostedIsFalse();
        long denied = repository.countAllByDeniedIsTrue();

        return new StatDTO(kamerstukken, queue, queueVote, denied);
    }
}