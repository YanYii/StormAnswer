package com.yan1.answer.util;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.yan1.answer.entity.QuestionAndAnswer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ES工具类
 */
@Slf4j
@Component
public class EsUtil {

    private static final String INDEX_NAME = "question-answer";
    private static final String FIELD_QUESTION = "question";
    private static final String FIELD_ANSWER = "answer";

    @Autowired
    private ElasticsearchClient client;

    /**
     * 创建索引
     */
    private void createIndex() {
        // 创建index
        try {
            CreateIndexRequest request = new CreateIndexRequest.Builder().index(INDEX_NAME).build();
            client.indices().create(request);
            log.info("创建索引成功, 索引名：{}", INDEX_NAME);
        } catch (IOException e) {
            log.error("创建索引失败, 索引名：" + INDEX_NAME, e);
        }
    }

    /**
     * 查询
     *
     * @param searchKey 搜索关键词
     * @param pageNo    分页的页码
     * @param pageSize  分页的大小
     * @return
     */
    public List<QuestionAndAnswer> search(String searchKey, Integer pageNo, Integer pageSize) {
        SearchRequest.Builder builder = new SearchRequest.Builder().index(INDEX_NAME);
        // 设置分页参数
        builder.from((pageNo - 1) * pageSize).size(pageSize);

        // 设置检索内容
        if (StringUtils.isNotEmpty(searchKey)) {
            builder.query(_0 -> _0
                    .multiMatch(_1 -> _1
                            .query(searchKey)
                            .fields(FIELD_QUESTION, FIELD_ANSWER)
                    ));
        }
        SearchRequest searchRequest = builder.build();

        try {
            SearchResponse<QuestionAndAnswer> response = client.search(searchRequest, QuestionAndAnswer.class);
            HitsMetadata<QuestionAndAnswer> hits = response.hits();
            List<Hit<QuestionAndAnswer>> hits1 = hits.hits();
            return hits1.stream().map(hit -> hit.source()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 存储数据到ES
     *
     * @param qaList
     */
    private void addDocument(List<QuestionAndAnswer> qaList) {
        // 添加文档
        qaList.forEach(questionAndAnswer -> {
            IndexRequest<QuestionAndAnswer> request = new IndexRequest.Builder<QuestionAndAnswer>().index(INDEX_NAME).document(questionAndAnswer).build();
            try {
                client.index(request);
                log.info("数据存储成功");
            } catch (IOException e) {
                log.error("数据存储失败", e);
            }
        });
    }

}
