package org.tfoweaver.utils;

/**
 * @Description:
 * @title: StringUtils
 * @Author Star_Chen
 * @Date: 2022/7/12 11:15
 * @Version 1.0
 */
public class StringUtils {


    /**
     * @return boolean
     * @Author Star_Chen
     * @Description
     * @Date 8:57 2022/8/5
     * @Param [css]
     **/
    public static boolean isNoneBlank(final CharSequence... css) {
        return !isAnyBlank(css);
    }

    /**
     * @Author Star_Chen
     * @Description 
     * @Date 8:57 2022/8/5
     * @Param [css]
     * @return boolean
     **/
    private static boolean isAnyBlank(final CharSequence... css) {
        if (isEmpty(css)) {
            return true;
        }
        for (CharSequence cs : css) {
            if (isBlank(cs)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Author Star_Chen
     * @Description 
     * @Date 9:20 2022/8/5
     * @Param [array]
     * @return boolean
     **/
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Is blank boolean.
     *
     * @param cs the cs
     * @return the boolean
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
