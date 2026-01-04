package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.Timeline;
import com.github.josegc789.microblogging.spi.entities.FollowerDocument;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import com.github.josegc789.microblogging.spi.entities.PublicationEvent;
import com.github.josegc789.microblogging.spi.entities.TimelineDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoFollowersRepository;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoPublicationsRepository;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoTimelinesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleNosqlAsyncDistributionTimelineServiceTest {

  @Mock SpringDataMongoTimelinesRepository timelineRepository;
  @Mock SpringDataMongoFollowersRepository followersRepository;
  @Mock SpringDataMongoPublicationsRepository publicationsRepository;

  private SimpleNosqlAsyncDistributionTimelineService service() {
    return new SimpleNosqlAsyncDistributionTimelineService(
        "2", timelineRepository, followersRepository, publicationsRepository);
  }

  @Test
  void testShouldMaterializePublication() {
    var svc = service();
    String authorId = UUID.randomUUID().toString();
    String authorUsername = "authorName";
    String pubId = UUID.randomUUID().toString();
    Instant now = Instant.now();
    PublicationEvent ev = new PublicationEvent(pubId, authorUsername, authorId, "content", now);
    when(followersRepository.countByFollower(authorId)).thenReturn(1L);
    FollowerDocument follower =
        FollowerDocument.builder().follower("f1").followee(authorId).build();
    when(followersRepository.findByFollowee(authorId)).thenReturn(List.of(follower));
    when(timelineRepository.save(any(TimelineDocument.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    when(timelineRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
    svc.materialize(ev);
    ArgumentCaptor<TimelineDocument> selfCaptor = ArgumentCaptor.forClass(TimelineDocument.class);
    verify(timelineRepository, times(1)).save(selfCaptor.capture());
    TimelineDocument self = selfCaptor.getValue();
    assertAll(
        () -> assertEquals(authorId, self.getAuthorId()),
        () -> assertEquals(pubId, self.getPublicationId()),
        () -> assertEquals(authorUsername, self.getAuthorUsername()),
        () -> assertEquals(now, self.getCreatedOn()),
        () -> assertEquals("content", self.getContent()),
        () -> assertEquals(authorId, self.getOwner()));
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<TimelineDocument>> listCaptor = ArgumentCaptor.forClass(List.class);
    verify(timelineRepository, times(1)).saveAll(listCaptor.capture());
    List<TimelineDocument> saved = listCaptor.getValue();
    assertEquals(1, saved.size());
    TimelineDocument fanout = saved.get(0);
    assertAll(
        () -> assertEquals("f1", fanout.getOwner()),
        () -> assertEquals(pubId, fanout.getPublicationId()),
        () -> assertEquals(authorId, fanout.getAuthorId()));
  }

  @Test
  void testShouldSkipFanoutWhenThresholdExceeded() {
    var svc = service();
    String authorId = UUID.randomUUID().toString();
    PublicationEvent ev =
        new PublicationEvent(UUID.randomUUID().toString(), "u", authorId, "c", Instant.now());
    when(followersRepository.countByFollower(authorId)).thenReturn(100L);
    when(timelineRepository.save(any(TimelineDocument.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    svc.materialize(ev);
    verify(timelineRepository, times(1)).save(any(TimelineDocument.class));
    verify(timelineRepository, never()).saveAll(anyList());
  }

  @Test
  void testShouldMaterializeFollowWritesRecentPublications() {
    var svc = service();
    String followerId = UUID.randomUUID().toString();
    String followeeId = UUID.randomUUID().toString();
    Follow follow =
        Follow.builder()
            .follower(
                com.github.josegc789.microblogging.core.domain.User.builder()
                    .id(followerId)
                    .build())
            .followee(
                com.github.josegc789.microblogging.core.domain.User.builder()
                    .id(followeeId)
                    .build())
            .id(UUID.randomUUID().toString())
            .build();
    PublicationDocument p =
        PublicationDocument.builder()
            .id(UUID.randomUUID().toString())
            .authorId(followeeId)
            .authorUsername("au")
            .content("ct")
            .createdOn(Instant.now())
            .build();
    when(publicationsRepository.findByAuthorIdOrderByCreatedOnDesc(
            eq(followeeId), any(PageRequest.class)))
        .thenReturn(List.of(p));
    when(timelineRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
    svc.materialize(follow);
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<TimelineDocument>> captor = ArgumentCaptor.forClass(List.class);
    verify(timelineRepository).saveAll(captor.capture());
    List<TimelineDocument> saved = captor.getValue();
    assertEquals(1, saved.size());
    TimelineDocument t = saved.get(0);
    assertAll(
        () -> assertEquals(followerId, t.getOwner()),
        () -> assertEquals(p.getId(), t.getPublicationId()),
        () -> assertEquals(p.getAuthorId(), t.getAuthorId()),
        () -> assertEquals(p.getContent(), t.getContent()));
  }

  @Test
  void testShouldFindMergeAndRespectLimit() {
    var svc = service();
    String owner = UUID.randomUUID().toString();
    int limit = 2;
    Instant now = Instant.now();
    Instant later = now.plusSeconds(60);
    Instant earlier = now.minusSeconds(60);
    TimelineDocument own1 =
        TimelineDocument.builder()
            .publicationId("own1")
            .owner(owner)
            .authorId("a1")
            .authorUsername("u1")
            .content("c1")
            .createdOn(now)
            .build();
    TimelineDocument own2 =
        TimelineDocument.builder()
            .publicationId("own2")
            .owner(owner)
            .authorId("a2")
            .authorUsername("u2")
            .content("c2")
            .createdOn(earlier)
            .build();
    TimelineDocument indirect =
        TimelineDocument.builder()
            .publicationId("ind1")
            .owner("x")
            .authorId("followed")
            .authorUsername("fu")
            .content("ic")
            .createdOn(later)
            .build();
    when(timelineRepository.findByOwnerOrderByCreatedOnDesc(eq(owner), any(PageRequest.class)))
        .thenReturn(List.of(own1, own2));
    FollowerDocument fd = FollowerDocument.builder().follower(owner).followee("followed").build();
    when(followersRepository.findByFollower(owner)).thenReturn(List.of(fd));
    when(timelineRepository.findByAuthorIdInAndPublicationIdNotIn(
            anyList(), anyList(), any(PageRequest.class)))
        .thenReturn(List.of(indirect));
    List<Timeline> actual = svc.find(owner, limit);
    assertEquals(limit, actual.size());
    assertEquals("ind1", actual.get(0).publicationId());
    assertEquals("own1", actual.get(1).publicationId());
  }

  @Test
  void testShouldFindWithCursorMergeAndRespectLimit() {
    var svc = service();
    String owner = UUID.randomUUID().toString();
    int limit = 3;
    Instant cursor = Instant.now();
    TimelineDocument own =
        TimelineDocument.builder()
            .publicationId("a")
            .owner(owner)
            .authorId("a1")
            .authorUsername("u1")
            .content("c")
            .createdOn(cursor.minusSeconds(10))
            .build();
    TimelineDocument indirect =
        TimelineDocument.builder()
            .publicationId("b")
            .owner("o")
            .authorId("followed")
            .authorUsername("fu")
            .content("ic")
            .createdOn(cursor.minusSeconds(5))
            .build();
    when(timelineRepository.findByOwnerAndCreatedOnLessThanOrderByCreatedOnDesc(
            eq(owner), eq(cursor), any(PageRequest.class)))
        .thenReturn(List.of(own));
    FollowerDocument fd = FollowerDocument.builder().follower(owner).followee("followed").build();
    when(followersRepository.findByFollower(owner)).thenReturn(List.of(fd));
    when(timelineRepository
            .findByAuthorIdInAndPublicationIdNotInAndCreatedOnLessThanOrderByCreatedOnDesc(
                anyList(), anyList(), eq(cursor), any(PageRequest.class)))
        .thenReturn(List.of(indirect));
    List<Timeline> actual = svc.find(owner, limit, cursor);
    assertEquals(2, actual.size());
    assertEquals("b", actual.get(0).publicationId());
    assertEquals("a", actual.get(1).publicationId());
  }
}
