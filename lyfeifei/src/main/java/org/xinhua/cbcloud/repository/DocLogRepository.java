package org.xinhua.cbcloud.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.xinhua.cbcloud.pojo.DocLog;

public interface DocLogRepository extends ElasticsearchRepository<DocLog, Long> {

}
