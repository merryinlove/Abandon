package csu.utils;

/**
 * Created by ubuntu on 15-5-15.
 */
public class WarnUtil {

    public static String getPublicWarn(int total, int used) {
        if (used < total / 3) {
            return "流量充足,请放心使用";
        } else if (used < 2 * total / 3) {
            return "流量暂且宽裕";
        } else if (used < total) {
            return "流量已经使了一大半了,注意您的使用";
        } else {
            return "流量超了哦,小心地用哦,";
        }
    }

    public static String getCampusWarn() {
        return "开发者还从来没有用完过校园网流量";

    }


}
