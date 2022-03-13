package com.yan1.answer.service.impl;

import com.yan1.answer.entity.QuestionAndAnswer;
import com.yan1.answer.service.QaService;
import com.yan1.answer.util.EsUtil;
import com.yan1.answer.vo.QueryVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QaServiceImpl implements QaService {

    @Resource
    private EsUtil esUtil;

    @Override
    public List<QuestionAndAnswer> queryQa(QueryVO queryVO) {
        return searchQa(queryVO);
    }

    /**
     * 按关键词搜索
     *
     * @param queryVO
     * @return
     */
    private List<QuestionAndAnswer> searchQa(QueryVO queryVO) {
        Integer pageNo = queryVO.getPageNo();
        Integer pageSize = queryVO.getPageSize();
        if (pageNo < 0) {
            pageNo = 0;
        }
        if (pageSize < 0) {
            pageSize = 10;
        }
        List<QuestionAndAnswer> search = esUtil.search(queryVO.getSearchKey(), pageNo, pageSize);

        // 缺一个total

        return search;
    }

}
