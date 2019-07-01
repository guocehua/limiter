package com.ibs.limiter;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TImeTest {
    @Test
    public void day(){
        System.out.println(TimeUtil.getDayOfMonth(System.currentTimeMillis()));
        System.out.print(TimeUnit.MILLISECONDS.toDays(new Date().getTime()));
    }
}
