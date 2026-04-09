package com.github.robsonrjunior.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.github.robsonrjunior.domain.RawMaterial;
import com.github.robsonrjunior.repository.RawMaterialRepository;
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
 * Spring Data Elasticsearch repository for the {@link RawMaterial} entity.
 */
public interface RawMaterialSearchRepository extends ElasticsearchRepository<RawMaterial, Long>, RawMaterialSearchRepositoryInternal {}

interface RawMaterialSearchRepositoryInternal {
    Page<RawMaterial> search(String query, Pageable pageable);

    Page<RawMaterial> search(Query query);

    @Async
    void index(RawMaterial entity);

    @Async
    void deleteFromIndexById(Long id);
}

class RawMaterialSearchRepositoryInternalImpl implements RawMaterialSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final RawMaterialRepository repository;

    RawMaterialSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, RawMaterialRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<RawMaterial> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<RawMaterial> search(Query query) {
        SearchHits<RawMaterial> searchHits = elasticsearchTemplate.search(query, RawMaterial.class);
        List<RawMaterial> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(RawMaterial entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), RawMaterial.class);
    }
}
