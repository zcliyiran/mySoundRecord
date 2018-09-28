package com.zcliyiran.mysoundrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;
import com.zcliyiran.mysoundrecorder.activity.SettingsActivity;
import com.zcliyiran.mysoundrecorder.adapter.MyAdapter;
import com.zcliyiran.mysoundrecorder.fragment.FileViewerFragment;
import com.zcliyiran.mysoundrecorder.fragment.RecordFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 这是一个小项目
 * 主要涉及到数据库的增删改查
 * 文件的存储
 * 录音功能的调用
 * 文件的provider路径
 *
 * 存在优化
 * 8.0以后的权限回调
 *
 *
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    private static final String TAG = "MainActivity";

    private String[] titles;

    private MyAdapter adapter;

    private List<Fragment> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        initData();

    }

    private void initData() {
        list = new ArrayList<>();
        titles = new String[]{getString(R.string.tab_title_record),
                getString(R.string.tab_title_saved_recordings)};
        list.add(RecordFragment.newInstance(1));
        list.add(FileViewerFragment.newInstance());
        adapter = new MyAdapter(getSupportFragmentManager(), titles, list);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:

                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;

            default:

        }
        return super.onOptionsItemSelected(item);


    }
}
