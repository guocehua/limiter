package com.ibs.limiter;


import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
@Component
public class LimitInterceptor extends AbstractInterceptor  {

    @Autowired
    private  RedisRateLimiter rateLimiter;
    private  int flag=0;
    @Override
    protected ResponseEnum preFilter(HttpServletRequest request) {
        if(flag==0){
            rateLimiter.create();
            flag+=1;
        }
        if(!rateLimiter.tryAcquire()){
            System.out.println("限流中");
            return ResponseEnum.LIMIT;
        }
        return ResponseEnum.OK;
    }
}
