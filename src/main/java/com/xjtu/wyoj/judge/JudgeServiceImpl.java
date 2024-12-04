package com.xjtu.wyoj.judge;

import cn.hutool.json.JSONUtil;
import com.xjtu.wyoj.common.ErrorCode;
import com.xjtu.wyoj.exception.BusinessException;
import com.xjtu.wyoj.judge.codesandbox.CodeSandbox;
import com.xjtu.wyoj.judge.codesandbox.CodeSandboxFactory;
import com.xjtu.wyoj.judge.codesandbox.CodeSandboxProxy;
import com.xjtu.wyoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xjtu.wyoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xjtu.wyoj.judge.strategy.JudgeContext;
import com.xjtu.wyoj.model.dto.question.JudgeCase;
import com.xjtu.wyoj.judge.codesandbox.model.JudgeInfo;
import com.xjtu.wyoj.model.entity.Question;
import com.xjtu.wyoj.model.entity.QuestionSubmit;
import com.xjtu.wyoj.model.enums.QuestionSubmitStatusEnum;
import com.xjtu.wyoj.service.QuestionService;
import com.xjtu.wyoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 传入提交的id，获取对应的题目信息
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交题目信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2. 如果不为等待状态，抛出异常(两个Integer对象用==比较。比较的是地址，不是值)
        if (!Objects.equals(questionSubmit.getStatus(), QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中...");
        }
        // 3. 更改题目状态为"判题中"，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误（不能判题）");
        }
        // 4. 调用代码沙箱，获取执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox); // 代理增强
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();  //获取输入和输出用例
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class); // 转为数组对象
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList()); //获取输入用例
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);  // 代码沙箱执行的响应结果
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5. 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        // 6. 修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());  // 判题状态的成功，不是判题结果对不对
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误（已判题）");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
        return questionSubmitResult;
    }
}
