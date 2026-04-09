package com.github.robsonrjunior.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.github.robsonrjunior.domain.Country;
import com.github.robsonrjunior.repository.CountryRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Country} entity.
 */
public interface CountrySearchRepository extends ElasticsearchRepository<Country, Long>, CountrySearchRepositoryInternal {}

interface CountrySearchRepositoryInternal {
    Page<Country> search(String query, Pageable pageable);

    Page<Country> search(Query query);

    @Async
    void index(Country entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CountrySearchRepositoryInternalImpl implements CountrySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CountryRepository repository;

    CountrySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CountryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Country> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Country> search(Query query) {
        SearchHits<Country> searchHits = elasticsearchTemplate.search(query, Country.class);
        List<Country> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Country entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Country.class);
    }
}
