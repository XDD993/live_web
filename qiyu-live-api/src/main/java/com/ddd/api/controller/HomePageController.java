package com.ddd.api.controller;


import com.ddd.common.vo.WebResponseVO;
import com.qiyu.live.web.starter.context.QiyuRequestContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@CrossOrigin
public class HomePageController {
    
    @PostMapping("/initPage")
    public WebResponseVO initPage() {
        Long userId = QiyuRequestContext.getUserId();
        System.out.println(userId);
        //前端调用initPage --> success状态则代表已经登录过，token有效，前端可隐藏登录按钮
        return WebResponseVO.success();
    }
}