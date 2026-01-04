package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.Timeline;
import com.github.josegc789.microblogging.spi.TimelinesSpi;
import com.github.josegc789.microblogging.spi.entities.FollowerDocument;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import com.github.josegc789.microblogging.spi.entities.PublicationEvent;
import com.github.josegc789.microblogging.spi.entities.TimelineDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoFollowersRepository;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoPublicationsRepository;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoTimelinesRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleNosqlAsyncDistributionTimelineService implements TimelinesSpi {
  @Value("${app.fan-out-write-threshold}")
  private final String fanoutWriteThreshold;

  private final SpringDataMongoTimelinesRepository timelineRepository;
  private final SpringDataMongoFollowersRepository followersRepository;
  private final SpringDataMongoPublicationsRepository publicationsRepository;

  @Async("virtualThreadExecutor")
  @Override
  public void materialize(PublicationEvent publication) {
    String authorId = publication.authorId();
    distributeToSelf(publication);
    if (followersRepository.countByFollower(authorId) <= Long.parseLong(fanoutWriteThreshold)) {
      fanoutWrite(publication, authorId);
    }
  }

  private void fanoutWrite(PublicationEvent publication, String authorId) {
    List<FollowerDocument> followers = followersRepository.findByFollowee(authorId);

    timelineRepository.saveAll(
        followers.stream().map(f -> buildEntry(publication, f.getFollower())).toList());
  }

  @Async("virtualThreadExecutor")
  @Override
  public void materialize(Follow follow) {

    List<PublicationDocument> publications =
        publicationsRepository.findByAuthorIdOrderByCreatedOnDesc(
            follow.followee().id(), PageRequest.of(0, Integer.parseInt(fanoutWriteThreshold)));

    List<TimelineDocument> entries =
        publications.stream().map(p -> toTimeline(follow.follower().id(), p)).toList();

    timelineRepository.saveAll(entries);
  }

  private TimelineDocument toTimeline(String owner, PublicationDocument p) {
    return TimelineDocument.builder()
        .owner(owner)
        .publicationId(p.getId())
        .authorId(p.getAuthorId())
        .authorUsername(p.getAuthorUsername())
        .content(p.getContent())
        .createdOn(p.getCreatedOn())
        .build();
  }

  private void distributeToSelf(PublicationEvent document) {
    TimelineDocument selfEntry = buildEntry(document, document.authorId());
    timelineRepository.save(selfEntry);
  }

  private TimelineDocument buildEntry(PublicationEvent publication, String owner) {
    return TimelineDocument.builder()
        .owner(owner)
        .publicationId(publication.id())
        .authorId(publication.authorId())
        .authorUsername(publication.authorUsername())
        .createdOn(publication.createdOn())
        .content(publication.content())
        .build();
  }

  @Override
  public List<Timeline> find(String owner, int limit) {
    List<TimelineDocument> timeline =
        timelineRepository.findByOwnerOrderByCreatedOnDesc(owner, PageRequest.of(0, limit));

    List<String> followedAuthors =
        followersRepository.findByFollower(owner).stream()
            .map(FollowerDocument::getFollowee)
            .toList();

    List<TimelineDocument> indirect =
        timelineRepository.findByAuthorIdInAndPublicationIdNotIn(
            followedAuthors,
            timeline.stream().map(TimelineDocument::getPublicationId).toList(),
            PageRequest.of(0, limit));

    return mergeAndLimit(timeline, indirect, limit);
  }

  private List<Timeline> mergeAndLimit(
      List<TimelineDocument> a, List<TimelineDocument> b, int limit) {

    return Stream.concat(a.stream(), b.stream())
        .sorted(Comparator.comparing(TimelineDocument::getCreatedOn).reversed())
        .limit(limit)
        .map(this::toDomain)
        .toList();
  }

  private Timeline toDomain(TimelineDocument td) {
    return Timeline.builder()
        .publicationId(td.getPublicationId())
        .owner(td.getOwner())
        .authorId(td.getAuthorId())
        .authorUsername(td.getAuthorUsername())
        .content(td.getContent())
        .createdOn(td.getCreatedOn())
        .build();
  }

  @Override
  public List<Timeline> find(String owner, int limit, Instant cursor) {
    List<TimelineDocument> timeline =
        timelineRepository.findByOwnerAndCreatedOnLessThanOrderByCreatedOnDesc(
            owner, cursor, PageRequest.of(0, limit));

    List<String> followedAuthors =
        followersRepository.findByFollower(owner).stream()
            .map(FollowerDocument::getFollowee)
            .toList();

    List<TimelineDocument> indirect =
        timelineRepository
            .findByAuthorIdInAndPublicationIdNotInAndCreatedOnLessThanOrderByCreatedOnDesc(
                followedAuthors,
                timeline.stream().map(TimelineDocument::getPublicationId).toList(),
                cursor,
                PageRequest.of(0, limit));
    return mergeAndLimit(timeline, indirect, limit);
  }
}
