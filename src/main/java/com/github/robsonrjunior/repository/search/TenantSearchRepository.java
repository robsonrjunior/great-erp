package com.github.robsonrjunior.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.github.robsonrjunior.domain.Tenant;
import com.github.robsonrjunior.repository.TenantRepository;
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
 * Spring Data Elasticsearch repository for the {@link Tenant} entity.
 */
public interface TenantSearchRepository extends ElasticsearchRepository<Tenant, Long>, TenantSearchRepositoryInternal {}

interface TenantSearchRepositoryInternal {
    Page<Tenant> search(String query, Pageable pageable);

    Page<Tenant> search(Query query);

    @Async
    void index(Tenant entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TenantSearchRepositoryInternalImpl implements TenantSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TenantRepository repository;

    TenantSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TenantRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Tenant> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Tenant> search(Query query) {
        SearchHits<Tenant> searchHits = elasticsearchTemplate.search(query, Tenant.class);
        List<Tenant> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Tenant entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Tenant.class);
    }
}
