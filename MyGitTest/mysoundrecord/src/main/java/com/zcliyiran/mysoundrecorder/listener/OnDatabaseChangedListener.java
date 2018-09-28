package com.zcliyiran.mysoundrecorder.listener;

/**
 * @author 甘罗
 * @date 2018/9/10.
 */
public interface OnDatabaseChangedListener {
    /**
     *
     */
    void onNewDatabaseEntryAdded();

    /**
     *
     */
    void onDatabaseEntryRenamed();
}
