package com.github.robsonrjunior.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.repository.CompanyRepository;
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
 * Spring Data Elasticsearch repository for the {@link Company} entity.
 */
public interface CompanySearchRepository extends ElasticsearchRepository<Company, Long>, CompanySearchRepositoryInternal {}

interface CompanySearchRepositoryInternal {
    Page<Company> search(String query, Pageable pageable);

    Page<Company> search(Query query);

    @Async
    void index(Company entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CompanySearchRepositoryInternalImpl implements CompanySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CompanyRepository repository;

    CompanySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CompanyRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Company> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Company> search(Query query) {
        SearchHits<Company> searchHits = elasticsearchTemplate.search(query, Company.class);
        List<Company> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Company entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Company.class);
    }
}
