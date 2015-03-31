package com.androidy.tpt.info;

/**
 * Created by christinajackey on 3/23/15.
 */
public class Tips {
    private String mMessage;
    private String mLink;
    private String mMonth;
    private int mDay;
    private int mTipListId;
    private int mCategoryId;
    private String mDate;

    public Tips() {}

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getMonth() {
        return mMonth;
    }

    public void setMonth(String month) {
        mMonth = month;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public int getTipListId() {
        return mTipListId;
    }

    public void setTipListId(int tipListId) {
        mTipListId = tipListId;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    public String getDate() {

            return mMonth + " " + mDay;

    }
}
