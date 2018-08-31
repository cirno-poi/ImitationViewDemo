package com.example.dell.customviewdemo.utils;

public class NumberUtils {

    /**
     * 判断是否为临界值（9、99、999等）
     *
     * @param number 需要判断的数字
     * @return 是否为临界值
     */
    public static boolean isCritical(int number) {
        while (number > 0) {
            if (number % 10 != 9) {
                return false;
            }
            number = number / 10;
        }
        return true;
    }

    /**
     * 得到末尾9的个数（比如199为2，999为3）
     *
     * @param number 目标数字
     * @return 9的个数
     */
    public static int getNineCount(int number) {
        int count = 0;
        while (number > 0) {
            if (number % 10 != 9) {
                return count;
            }
            number = number / 10;
            count++;
        }
        return count;

    }
}
