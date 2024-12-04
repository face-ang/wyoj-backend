package com.xjtu.wyoj.judge;

import com.xjtu.wyoj.judge.strategy.DefaultJudgeStrategy;
import com.xjtu.wyoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.xjtu.wyoj.judge.strategy.JudgeContext;
import com.xjtu.wyoj.judge.strategy.JudgeStrategy;
import com.xjtu.wyoj.judge.codesandbox.model.JudgeInfo;
import com.xjtu.wyoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
