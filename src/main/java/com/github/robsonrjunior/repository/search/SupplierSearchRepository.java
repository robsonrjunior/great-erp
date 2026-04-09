package com.github.robsonrjunior.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.github.robsonrjunior.domain.Supplier;
import com.github.robsonrjunior.repository.SupplierRepository;
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
 * Spring Data Elasticsearch repository for the {@link Supplier} entity.
 */
public interface SupplierSearchRepository extends ElasticsearchRepository<Supplier, Long>, SupplierSearchRepositoryInternal {}

interface SupplierSearchRepositoryInternal {
    Page<Supplier> search(String query, Pageable pageable);

    Page<Supplier> search(Query query);

    @Async
    void index(Supplier entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SupplierSearchRepositoryInternalImpl implements SupplierSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SupplierRepository repository;

    SupplierSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SupplierRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Supplier> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Supplier> search(Query query) {
        SearchHits<Supplier> searchHits = elasticsearchTemplate.search(query, Supplier.class);
        List<Supplier> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Supplier entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Supplier.class);
    }
}
