package com.url.url_shortener.repository;

import com.url.url_shortener.models.UrlMapping;
import com.url.url_shortener.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    UrlMapping findByShortUrl(String shortUrl);

    List<UrlMapping> findByUser(User user);
}
