package com.github.josegc789.microblogging;

import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.Publication;
import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.Timeline;
import com.github.josegc789.microblogging.core.domain.User;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class PublicationsIntTest {
  private static final RestTemplate REST = new RestTemplate();
  @Container static final MongoDBContainer MONGO = new MongoDBContainer("mongo:6.0.6");
  @Container static final KafkaContainer KAFKA = new KafkaContainer("apache/kafka-native:3.8.0");

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.mongodb.uri", MONGO::getReplicaSetUrl);
    registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    registry.add("spring.kafka.producer.bootstrap-servers", KAFKA::getBootstrapServers);
    registry.add("spring.kafka.consumer.bootstrap-servers", KAFKA::getBootstrapServers);
  }

  @LocalServerPort int port;

  private String baseUrl() {
    return "http://localhost:" + port + "/microblogging";
  }

  private User signUp(SignInUser signInUser) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    return REST.postForEntity(
            baseUrl() + "/users", new HttpEntity<>(signInUser, headers), User.class)
        .getBody();
  }

  private Publication publish(NewPublication publication) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    return REST.postForEntity(
            baseUrl() + "/publications", new HttpEntity<>(publication, headers), Publication.class)
        .getBody();
  }

  private Follow follow(NewFollow follow) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    return REST.exchange(
            baseUrl() + "/users/follow",
            HttpMethod.PUT,
            new HttpEntity<>(follow, headers),
            Follow.class)
        .getBody();
  }

  private List<Timeline> timeline(String owner) {
    return timeline(owner, null);
  }

  private List<Timeline> timeline(String owner, ZonedDateTime cursor) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    URI uri =
        UriComponentsBuilder.fromUriString(baseUrl() + "/timelines")
            .queryParam("owner", owner)
            .queryParam("cursor", cursor)
            .build()
            .encode()
            .toUri();
    ResponseEntity<List<Timeline>> exchange =
        REST.exchange(
            uri, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {});
    return exchange.getBody();
  }

  @Test
  void testShouldPublishToSelfTimeline() throws InterruptedException {
    SignInUser signIn = SignInUser.builder().username(UUID.randomUUID().toString()).build();
    User user = requireNonNull(signUp(signIn));
    NewPublication publishPublication =
        NewPublication.builder().authorId(user.id()).content(UUID.randomUUID().toString()).build();
    Publication publication = requireNonNull(publish(publishPublication));
    waitPublication();
    Timeline timeline =
        requireNonNull(timeline(user.id(), ZonedDateTime.now())).stream()
            .filter(t -> publication.id().equals(t.publicationId()))
            .findFirst()
            .orElseThrow(AssertionError::new);
    assertAll(
        () -> assertEquals(signIn.username(), user.username()),
        () -> assertEquals(publishPublication.content(), publication.content()),
        () -> assertEquals(publishPublication.authorId(), publication.author().id()),
        () -> assertEquals(user, publication.author()),
        () ->
            assertEquals(
                Timeline.builder()
                    .publicationId(publication.id())
                    .owner(user.id())
                    .authorId(user.id())
                    .authorUsername(user.username())
                    .content(publication.content())
                    .build(),
                timeline.withCreatedOn(null)));
  }

  @Test
  void testShouldPublishToFollowers() throws InterruptedException {
    List<User> users =
        List.of(
            requireNonNull(
                signUp(SignInUser.builder().username(UUID.randomUUID().toString()).build())),
            requireNonNull(
                signUp(SignInUser.builder().username(UUID.randomUUID().toString()).build())),
            requireNonNull(
                signUp(SignInUser.builder().username(UUID.randomUUID().toString()).build())));
    List<Follow> follows =
        List.of(
            requireNonNull(
                follow(
                    NewFollow.builder()
                        .follower(users.get(1).id())
                        .followee(users.get(0).id())
                        .build())),
            requireNonNull(
                follow(
                    NewFollow.builder()
                        .follower(users.get(2).id())
                        .followee(users.get(0).id())
                        .build())));
    List<Publication> publications =
        List.of(
            requireNonNull(
                publish(
                    NewPublication.builder()
                        .authorId(users.get(0).id())
                        .content(UUID.randomUUID().toString())
                        .build())),
            requireNonNull(
                publish(
                    NewPublication.builder()
                        .authorId(users.get(0).id())
                        .content(UUID.randomUUID().toString())
                        .build())));
    waitPublication();

    List<Timeline> timelines =
        Stream.of(
                requireNonNull(timeline(users.get(1).id())),
                requireNonNull(timeline(users.get(2).id())))
            .flatMap(List::stream)
            .toList();
    Timeline toBeExpected =
        Timeline.builder()
            .publicationId(publications.get(1).id())
            .owner(users.get(1).id())
            .authorId(users.getFirst().id())
            .authorUsername(users.getFirst().username())
            .content(publications.get(1).content())
            .build();
    List<Timeline> expected =
        List.of(
            toBeExpected,
            toBeExpected
                .withPublicationId(publications.getFirst().id())
                .withContent(publications.getFirst().content()),
            toBeExpected
                .withPublicationId(publications.get(1).id())
                .withContent(publications.get(1).content())
                .withOwner(users.get(2).id()),
            toBeExpected
                .withPublicationId(publications.getFirst().id())
                .withContent(publications.getFirst().content())
                .withOwner(users.get(2).id()));
    assertEquals(expected, timelines.stream().map(t -> t.withCreatedOn(null)).toList());
  }

  @Test
  void testNewFollowShouldMaterializeTimeline() throws InterruptedException {
    List<User> users =
        List.of(
            requireNonNull(
                signUp(SignInUser.builder().username(UUID.randomUUID().toString()).build())),
            requireNonNull(
                signUp(SignInUser.builder().username(UUID.randomUUID().toString()).build())));
    List<Publication> publications =
        List.of(
            requireNonNull(
                publish(
                    NewPublication.builder()
                        .authorId(users.get(0).id())
                        .content(UUID.randomUUID().toString())
                        .build())),
            requireNonNull(
                publish(
                    NewPublication.builder()
                        .authorId(users.get(0).id())
                        .content(UUID.randomUUID().toString())
                        .build())));
    waitPublication();
    List<Follow> follows =
        List.of(
            requireNonNull(
                follow(
                    NewFollow.builder()
                        .follower(users.get(1).id())
                        .followee(users.get(0).id())
                        .build())));
    waitPublication();
    List<Timeline> timelines =
        Stream.of(requireNonNull(timeline(users.get(1).id()))).flatMap(List::stream).toList();
    Timeline toBeExpected =
        Timeline.builder()
            .publicationId(publications.get(1).id())
            .owner(users.get(1).id())
            .authorId(users.getFirst().id())
            .authorUsername(users.getFirst().username())
            .content(publications.get(1).content())
            .build();
    List<Timeline> expected =
        List.of(
            toBeExpected,
            toBeExpected
                .withPublicationId(publications.getFirst().id())
                .withContent(publications.getFirst().content()));
    assertEquals(expected, timelines.stream().map(t -> t.withCreatedOn(null)).toList());
  }

  private static void waitPublication() throws InterruptedException {
    Thread.sleep(1000);
  }
}
