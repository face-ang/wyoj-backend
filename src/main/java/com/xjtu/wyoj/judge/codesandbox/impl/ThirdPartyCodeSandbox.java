package com.xjtu.wyoj.judge.codesandbox.impl;

import com.xjtu.wyoj.judge.codesandbox.CodeSandbox;
import com.xjtu.wyoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xjtu.wyoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上已有的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
