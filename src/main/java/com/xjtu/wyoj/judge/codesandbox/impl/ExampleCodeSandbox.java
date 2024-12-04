package com.xjtu.wyoj.judge.codesandbox.impl;

import com.xjtu.wyoj.judge.codesandbox.CodeSandbox;
import com.xjtu.wyoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xjtu.wyoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xjtu.wyoj.judge.codesandbox.model.JudgeInfo;
import com.xjtu.wyoj.model.enums.JudgeInfoMessageEnum;
import com.xjtu.wyoj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱（仅为了先跑通业务流程）
 */
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse=new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
