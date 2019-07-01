package com.ibs.limiter;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ResponseEnum result;
        result = preFilter(request);
        if(ResponseEnum.OK==result)
            return true;
        handResponse(result,response);
        return false;
    }

    protected abstract ResponseEnum preFilter(HttpServletRequest request);

    private void handResponse(ResponseEnum result, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(result.getMsg());
        } catch (IOException i) {
            i.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
