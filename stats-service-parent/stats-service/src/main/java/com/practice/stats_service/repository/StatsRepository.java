package com.practice.stats_service.repository;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_dto.ViewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = """
            select app, uri, count(id) as hits
            from endpoint_hits
            where app = ?1
              and timestamp between ?2 and ?3
              and uri in (:uris)
            group by app, uri
            """,
            nativeQuery = true)
    List<ViewStats> getSingleUriStats(String app, String start, String end, String[] uris);

    @Query(value = """
            select app, uri, count(distinct ip) as hits
            from endpoint_hits
            where app = ?1
              and timestamp between ?2 and ?3
              and uri in (:uris)
            group by app, uri
            """,
            nativeQuery = true)
    List<ViewStats> getUniqueIpSingleUriStats(String app, String start, String end, String[] uris);

    @Query(value = """
            select app, uri, count(id) as hits
            from endpoint_hits
            where app = ?1
              and timestamp between ?2 and ?3
            group by app, uri
            """,
            nativeQuery = true)
    List<ViewStats> getAllUriStats(String app, String start, String end);

    @Query(value = """
            select app, uri, count(distinct ip) as hits
            from endpoint_hits
            where app = ?1
              and timestamp between ?2 and ?3
            group by app, uri
            """,
            nativeQuery = true)
    List<ViewStats> getUniqueIpAllUriStats(String app, String start, String end);

    @Query(value = """
            select exists(select *
                          from endpoint_hits
                          where uri = ?1
                            and ip = ?2)
            """,
            nativeQuery = true)
    boolean checkIpExistsByUri(String uri, String ip);
}
