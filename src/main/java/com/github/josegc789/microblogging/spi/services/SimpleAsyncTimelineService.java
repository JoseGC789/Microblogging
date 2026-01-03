package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.Timeline;
import com.github.josegc789.microblogging.spi.TimelinesSpi;
import com.github.josegc789.microblogging.spi.entities.FollowerDocument;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import com.github.josegc789.microblogging.spi.entities.TimelineDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoFollowersRepository;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoPublicationsRepository;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoTimelinesRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleAsyncTimelineService implements TimelinesSpi {
  private final SpringDataMongoTimelinesRepository timelineRepository;
  private final SpringDataMongoFollowersRepository followersRepository;
  private final SpringDataMongoPublicationsRepository publicationsRepository;

  @Value("${app.popular-follower-threshold}")
  private final long popularThreshold;

  @Override
  @Async("virtualThreadExecutor")
  public void distribute(PublicationDocument publication) {

    String authorId = publication.getAuthorId();
    try {
      distributeToSelf(publication);
      long followerCount = followersRepository.countByFollowee(authorId);

      if (followerCount > popularThreshold) {
        // Popular author: skip mass push. Read path will pull recent publications of this author.
        return;
      }

      fanOut(publication, authorId);
    } catch (Exception e) {
      pushQueue(publication);
    }
  }

  private void fanOut(PublicationDocument publication, String authorId) {
    List<FollowerDocument> followers = followersRepository.findByFollowee(authorId);

    timelineRepository.saveAll(
        followers.stream().map(f -> buildEntry(publication, f.getFollower())).toList());
  }

  private void pushQueue(PublicationDocument publication) {
    log.error("unimplemented push queue fanout {}", publication);
  }

  private void distributeToSelf(PublicationDocument document) {
    TimelineDocument selfEntry = buildEntry(document, document.getAuthorId());
    timelineRepository.save(selfEntry);
  }

  private TimelineDocument buildEntry(PublicationDocument publication, String owner) {
    return TimelineDocument.builder()
        .owner(owner)
        .publicationId(publication.getId())
        .authorId(publication.getAuthorId())
        .authorUsername(publication.getAuthorUsername())
        .createdOn(publication.getCreatedOn())
        .content(publication.getContent())
        .build();
  }

  @Override
  public List<Timeline> find(String owner, int limit) {
    Pageable page = PageRequest.of(0, limit);
    return timelineRepository.findByOwnerOrderByCreatedOnDesc(owner, page).stream()
        .map(
            td ->
                Timeline.builder()
                    .publicationId(td.getPublicationId())
                    .owner(td.getOwner())
                    .authorId(td.getAuthorId())
                    .authorUsername(td.getAuthorUsername())
                    .content(td.getContent())
                    .createdOn(td.getCreatedOn())
                    .build())
        .toList();
  }

  @Override
  public List<Timeline> find(String owner, int limit, Instant cursor) {
    Pageable page = PageRequest.of(0, limit);
    return timelineRepository
        .findByOwnerAndCreatedOnLessThanOrderByCreatedOnDesc(owner, cursor, page)
        .stream()
        .map(
            td ->
                Timeline.builder()
                    .publicationId(td.getPublicationId())
                    .owner(td.getOwner())
                    .authorId(td.getAuthorId())
                    .authorUsername(td.getAuthorUsername())
                    .content(td.getContent())
                    .createdOn(td.getCreatedOn())
                    .build())
        .toList();
  }
}
