package com.github.alenfive.datajellyfish.utils;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Company: 成都国盛天丰技术有限责任公司
 * @Author: 米华军
 * @CreateDate: 2020/9/10 21:14
 * @UpdateDate: 2020/9/10 21:14
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
public class ThreadUtils {
    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
