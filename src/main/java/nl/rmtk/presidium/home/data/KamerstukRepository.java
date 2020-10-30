package nl.rmtk.presidium.home.data;

import nl.rmtk.presidium.home.controller.dto.Kamerstuk;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public interface KamerstukRepository extends MongoRepository<Kamerstuk, String> {

    Optional<Kamerstuk> findByCallsignEqualsAndPostedIsTrue(String callsign);

    List<Kamerstuk> findAllByPostDateIsAfterAndDeniedIsFalse(Date date);

    PriorityQueue<Kamerstuk> findAllByPostDateIsBeforeAndPostedIsFalseAndDeniedIsFalse(Date date);

    List<Kamerstuk> findAllByPostedIsTrueAndVotePostedIsFalseAndDeniedIsFalseAndVoteDateIsNotNull();

    List<Kamerstuk> findAllByPostedIsTrueAndVotePostedIsFalseAndDeniedIsFalseAndVoteDateIsNull();

    List<Kamerstuk> findAllByPostedIsTrueAndCallsignContains(String contains);

    List<Kamerstuk> findAllByPostDateIsNullAndDeniedIsFalse();

    List<Kamerstuk> findAllByPostDateIsBetweenAndCallsignIsNotNull(Date date1, Date date2);

    List<Kamerstuk> findAllByPostDateIsBeforeAndCallsignIsNotNull(Date date1);

    long countAllByPostDateIsNullAndDeniedIsFalse();

    long countAllByPostDateIsNotNullAndPostedIsFalse();

    long countAllByPostedIsTrueAndVotePostedIsFalseAndDeniedIsFalse();

    long countAllByPostedIsFalseAndDeniedIsTrue();

    long countAllByPostedIsTrueAndDeniedIsTrue();

    long countAllByPostedIsTrueAndVotePostedIsTrue();

    boolean existsByCallsignAndIdIsNot(String callsign, String id);

    boolean existsByCallsign(String callsign);
}
