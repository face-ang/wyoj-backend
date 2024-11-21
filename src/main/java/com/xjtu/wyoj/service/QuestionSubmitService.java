package com.xjtu.wyoj.service;

import com.xjtu.wyoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xjtu.wyoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjtu.wyoj.model.entity.User;

/**
* @author admin
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-11-20 22:33:34
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);
}
