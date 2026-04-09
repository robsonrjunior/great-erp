package com.github.robsonrjunior.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.github.robsonrjunior.domain.State;
import com.github.robsonrjunior.repository.StateRepository;
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
 * Spring Data Elasticsearch repository for the {@link State} entity.
 */
public interface StateSearchRepository extends ElasticsearchRepository<State, Long>, StateSearchRepositoryInternal {}

interface StateSearchRepositoryInternal {
    Page<State> search(String query, Pageable pageable);

    Page<State> search(Query query);

    @Async
    void index(State entity);

    @Async
    void deleteFromIndexById(Long id);
}

class StateSearchRepositoryInternalImpl implements StateSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final StateRepository repository;

    StateSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, StateRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<State> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<State> search(Query query) {
        SearchHits<State> searchHits = elasticsearchTemplate.search(query, State.class);
        List<State> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(State entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), State.class);
    }
}
