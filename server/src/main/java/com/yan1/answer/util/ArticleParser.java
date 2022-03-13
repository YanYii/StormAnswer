package com.yan1.answer.util;


import com.yan1.answer.entity.QuestionAndAnswer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 《我爱问帅张》公众号问答文章解析器
 */
@Slf4j
public class ArticleParser {


    public static List<QuestionAndAnswer> parseQa(String link) {

        List<QuestionAndAnswer> qaList = new ArrayList<>();
        try {
            // 检查文本有没有 【球友提问】 【张哥回答】 这个标记，有的话，才是问答文章
            // 检查多少个【球友提问】
            // 开始遍历，每个节点，找到【球友提问】，检测之前的序号是否对应上，不做限制，只做标记
            // 找到后开始解析，记录
            // 继续找到【张哥回答】,解析，记录

            Document document = Jsoup.parse(new URL(link), 10 * 1000);
            String content = document.text();

            int order = 1;
            while (true) {
                String qTag = order + " 球友提问：";
                order++;


                int qIndex = content.indexOf(qTag);
                if (qIndex == -1) {
                    break;
                }

                // 找到第一个球友提问，截取出问题内容
                // 找第一个张哥问答
                String aTag = "张哥回答：";
                int aIndex = content.indexOf(aTag);
                if (aIndex == -1) {
                    break;
                }

                int start = qIndex + qTag.length();
                int end = aIndex;
                String question = content.substring(start, end);
                if (StringUtils.isEmpty(question)) {
                    break;
                }

                log.info("问题 {} : {}", (order - 1), question);

                content = content.substring(end);

                // 找答案
                String nextQTag = order + " 球友提问：";
                int nextQIndex = content.indexOf(nextQTag);
                if (nextQIndex == -1) {
                    // 可能要截取到结尾，作为答案

                    String endTag = "预览时标签不可点";
                    nextQIndex = content.indexOf(endTag);

                    if (nextQIndex == -1) {
                        break;
                    }
                }
                start = aTag.length();
                end = nextQIndex;
                String answer = content.substring(start, end);

                log.info("答案 {} : {}", (order - 1), answer);
                log.info(answer);

                qaList.add(new QuestionAndAnswer(question, answer));

                content = content.substring(nextQIndex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return qaList;
    }

}
