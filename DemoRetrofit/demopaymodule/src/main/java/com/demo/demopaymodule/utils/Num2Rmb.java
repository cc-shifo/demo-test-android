package com.demo.demopaymodule.utils;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.demo.demopaymodule.R;

public class Num2Rmb {
    private static final String TAG = "Num2Rmb";
    private static final String[] mDigits = new String[]{"零", "一", "二", "三", "四", "五", "六",
            "七", "八", "九"};
    private static final String[] mUnits = new String[]{"千", "百", "十"};
    private static final ArrayMap<String, Integer> mSoundTable;
    static {
        mSoundTable = new ArrayMap<>();
        mSoundTable.put("零", R.raw.zero);
        mSoundTable.put("一", R.raw.one);
        mSoundTable.put("二", R.raw.two);
        mSoundTable.put("三", R.raw.three);
        mSoundTable.put("四", R.raw.four);
        mSoundTable.put("五", R.raw.five);
        mSoundTable.put("六", R.raw.six);
        mSoundTable.put("七", R.raw.seven);
        mSoundTable.put("八", R.raw.eight);
        mSoundTable.put("九", R.raw.nine);

        mSoundTable.put("万", R.raw.tenthousand);
        mSoundTable.put("千", R.raw.thousand);
        mSoundTable.put("百", R.raw.hundred);
        mSoundTable.put("十", R.raw.ten);
        mSoundTable.put("点", R.raw.point);
        mSoundTable.put("元", R.raw.yuan);
    }

    /**
     * 将一个小于15位的整数(整数部分含2位小数位)变成人民币中文读法
     */
    @NonNull
    public static String num2rmb(@IntRange(from = 1) long amount) {
        long intPart = amount / 100;
        long floatPart = amount % 100;
        if (floatPart == 0) {
            return intPart("" + intPart) + "元";
        }

        return intPart("" + intPart) + "点" + floatPart("" + floatPart) + "元";
    }

    /**
     * 将一个小于15位的整数(整数部分含2位小数位)变成人民币中文读法
     */
    public static int[] num2RmbSound(long amount) {
        /*for (int i = 0; i < mSoundTable.size(); i++) {
            Log.d(TAG, "Num2Rmb key " + mSoundTable.keyAt(i) + ", value "
                            + String.format("%x", mSoundTable.valueAt(i)));
        }*/

        String value = num2rmb(amount);
        Log.d(TAG, "Num2Rmb to announce " + amount + ": " +  value);
        int len = value.length();
        int[] soundResIds = new int[len];
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            /*Log.d(TAG, "Num2Rmb to announce : " + ch);*/
            soundResIds[i] = mSoundTable.get(String.valueOf(ch));
        }

        return soundResIds;
    }

    public static void testAnnounce(String rst, String except) {
        if (rst.equals(except))
            Log.d(TAG, "test passed\n");
        else
            Log.d(TAG, "test failed\n");
    }

    /**
     * 将12位整数部分数字转为带单位字符串
     */
    private static String intPart(String inputStr) {
        int len = inputStr.length(); //获取字符串总长度
        int partNum = (len % 4 == 0) ? (len / 4) : (len / 4) + 1;//将12位内数字分为几部分
        // 分别定义亿，万，元字符串
        String yiStr = "";
        String wanStr = "";
        String yuanStr = "";
        // 12位内至多分为三部分
        String firstPart;
        String secondPart;
        String thirdPart;
        int tmpLen; //用于计算临时有效字符串长度 （去除0）
        boolean fourValidBitsInPart3 = false; //判断第三部分是否为4位
        switch (partNum) {
            case 1: /*只有1部分，就是xxx元*/ {
                yuanStr += intPartCore(inputStr);
                break;
            }
            case 2: /*有2部分，就是 xxx万xxx元*/ {
                //注意 String.substring(beginIndex,endIndex)中endIndex不包含
                int endIndex = len - 4;
                firstPart = inputStr.substring(0, endIndex);
                wanStr += intPartCore(firstPart) + "万";
                secondPart = inputStr.substring(endIndex, len);
                yuanStr += intPartCore(secondPart);
                tmpLen = getValidStrLen(secondPart);
                if (0 < tmpLen && tmpLen < 4) {
                    yuanStr = "零" + yuanStr;
                }
                break;
            }
            case 3:/*有3部分，就是 xxx亿xxx万xxx元*/ {
                int endIndex2 = len - 4;
                int endIndex1 = endIndex2 - 4;
                firstPart = inputStr.substring(0, endIndex1);
                yiStr += intPartCore(firstPart) + "亿";
                secondPart = inputStr.substring(endIndex1, endIndex2);
                wanStr += intPartCore(secondPart) + "万";
                tmpLen = getValidStrLen(secondPart);
                if (0 < tmpLen && tmpLen < 4) //中间过程进行补零
                {
                    wanStr = "零" + wanStr;
                }
                thirdPart = inputStr.substring(endIndex2, len);
                yuanStr += intPartCore(thirdPart);
                tmpLen = getValidStrLen(thirdPart);
                if (0 < tmpLen && tmpLen < 4) {
                    yuanStr = "零" + yuanStr;
                } else {
                    if (tmpLen == 4) {
                        fourValidBitsInPart3 = true;
                    }
                }
                break;
            }
            default:
                break;
        }
        //对结果进行判断，如果万位和元位都为0 ，
        if (wanStr.equals("零万")) {
            //java中时不能直接比较 wanStr =="零"
            if (fourValidBitsInPart3)//Str_wan为"零万"且元部分为4位。如12300001234中间需读出"零"
            {
                wanStr = "零";
            } else //否则就不添加
            {
                wanStr = "";
            }
        }
        if (yuanStr.equals("零")) {
            yuanStr = "";
        }

        String currencyStr = yiStr + wanStr + yuanStr/* + "元"*/;
        return !currencyStr.isEmpty() ? currencyStr : "零";
    }

    /**
     * 四位数的字符串转汉字读法
     * 如果输入NumStr 不够4位就前补零，变为4位
     */
    private static String intPartCore(String inputStr) {
        StringBuilder builder = new StringBuilder();
        int len = inputStr.length(); //获取NumStr的长度

        //补零操作
        if (len < 4) {
            //位数小于4位就前补位
            String[] addZeroStr = {"0", "00", "000"};
            inputStr = addZeroStr[4 - len - 1] + inputStr;
        }

        len = inputStr.length(); //更新长度 其实 len==4

        //扫描至第一个不为0的字符
        int i = 0;
        while (i < len && inputStr.charAt(i) - '0' == 0) {
            ++i;
        }

        if (i == len) {
            //四个数都为零 ，就直接输出一个零 并返回
            builder.append("零");
            return builder.toString();
        }

        //四个数不都为0的情况
        for (; i < len; ++i) {
            int num = inputStr.charAt(i) - '0';
            if (i != len - 1 && num != 0) {
                //不是末尾且不为0
                builder.append(mDigits[num]).append(mUnits[i]);//直接映射汉字和单位
            } else {
                // 若当前结果字符串中最后一位不是“零”，且数字串最后一位不是数字0
                // 这样对中间2个零的只添加一个“零”如1003
                if (builder.toString().length() > 0 && builder.toString().
                        charAt(builder.toString().length() - 1) != '零'
                        && inputStr.charAt(len - 1) - '0' != 0) {
                    //四位中第二，三位至少有一个为0且最后一位数字不为0 如1003 则为一千零三
                    if ((i == 1 || i == 2) && num == 0) {
                        builder.append("零");
                    }
                }

                if (i == len - 1 && num != 0) {
                    //是最后一位，且数字不为0 不加单位
                    builder.append(mDigits[num]);
                }
            }
        }
        return builder.toString(); //返回结果字符串
    }

    /* 获取数字字符串的有效位（不是0的位数）
     */
    private static int getValidStrLen(String str) {
        int start = 0;
        int len = str.length();
        while (start < len && str.charAt(start) == '0') {
            ++start;
        }
        return 4 - start;
    }

    //处理小数字符串部分 最多两位
    private static String floatPart(String floatStr) {
        //补零操作
        int len = floatStr.length();
        if (len < 2) {
            //位数小于2位就前补位
            String[] addZeroStr = {"0", "00"};
            floatStr = addZeroStr[2 - len - 1] + floatStr;
        }

        StringBuilder jiaoStr = new StringBuilder();
        StringBuilder fenStr = new StringBuilder();
        for (int i = 0; i < 2; ++i) {
            //2 表示就是两位 因为只有角分而已
            int num = floatStr.charAt(i) - '0';
            if (i == 0) {
                jiaoStr.append(mDigits[num]);
                /*if (num != 0) {
                    jiaoStr += "角";
                }*/
            } else {
                if (num != 0) {
                    fenStr.append(mDigits[num]);
                    /*fenStr += mDigits[num] + "分";*/
                }
            }
        }
        if (fenStr.toString().isEmpty() && (jiaoStr.toString().equals("零")/* || jiaoStr.equals
        ("零角")*/)) {
            /*jiaoStr = "";*/
            jiaoStr.setLength(0);
        }
        return jiaoStr.append(fenStr.toString()).toString();
    }
}
