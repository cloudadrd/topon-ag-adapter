package com.business.support.attract;

import java.util.ArrayList;
import java.util.List;

public class PolicyData {

    public static class RV {
        /**
         * 开始的范围值
         */
        public int startRange;

        /**
         * 结束的范围值
         */
        public int endRange;

        /**
         * 显示概率
         */
        public int chance;
    }

    public static class Banner {

        /**
         * 样式类型
         */
        public BannerStyleType styleType;

        /**
         * 展示概率
         */
        public int chance;
    }

    public enum BannerStyleType {

        /**
         * 头发丝
         */
        HAIR(1),

        /**
         * 手指
         */
        FINGER(2);


        private int value;

        BannerStyleType(int value) {
            this.value = value;
        }

        public static BannerStyleType get(int value) {
            if (value == 1) {
                return HAIR;
            } else if (value == 2) {
                return FINGER;
            }
            return null;
        }

    }


    public List<RV> rvs = new ArrayList<>(6);

    public List<Banner> banners = new ArrayList<>(2);

    /**
     * native广告显示概率
     */
    public int nativeChance;


}
