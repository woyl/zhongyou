package com.zhongyou.meet.mobile.entities;

/**Retrieve specific user rating info
 * Created by wufan on 2017/8/15.
 */

public class RankInfo {

    /**
     * star : 3.0
     * serviceFrequency : 20
     * ratingFrequency : 18
     */

    private String star;
    private int serviceFrequency;
    private int ratingFrequency;

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public int getServiceFrequency() {
        return serviceFrequency;
    }

    public void setServiceFrequency(int serviceFrequency) {
        this.serviceFrequency = serviceFrequency;
    }

    public int getRatingFrequency() {
        return ratingFrequency;
    }

    public void setRatingFrequency(int ratingFrequency) {
        this.ratingFrequency = ratingFrequency;
    }
}
