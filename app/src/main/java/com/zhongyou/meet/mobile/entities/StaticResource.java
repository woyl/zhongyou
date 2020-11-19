package com.zhongyou.meet.mobile.entities;

/**
 * Created by whatisjava on 18-1-15.
 */

public class StaticResource implements Entity {

    private StaticRes staticRes;

    public StaticRes getStaticRes() {
        return staticRes;
    }

    public void setStaticRes(StaticRes staticRes) {
        this.staticRes = staticRes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaticResource that = (StaticResource) o;

        return staticRes != null ? staticRes.equals(that.staticRes) : that.staticRes == null;
    }

    @Override
    public int hashCode() {
        return staticRes != null ? staticRes.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "StaticResource{" +
                "staticRes=" + staticRes +
                '}';
    }
}
