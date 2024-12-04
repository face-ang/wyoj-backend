package com.xjtu.wyoj.judge.strategy;

import com.xjtu.wyoj.model.dto.question.JudgeCase;
import com.xjtu.wyoj.judge.codesandbox.model.JudgeInfo;
import com.xjtu.wyoj.model.entity.Question;
import com.xjtu.wyoj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;
/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<String> inputList;
    private List<String> outputList;
    private List<JudgeCase>judgeCaseList;
    private Question question;
    private QuestionSubmit questionSubmit;
}
