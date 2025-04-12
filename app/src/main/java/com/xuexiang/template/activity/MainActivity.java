package com.xuexiang.template.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xuexiang.template.R;
import com.xuexiang.template.User.User;
import com.xuexiang.template.core.BaseActivity;
import com.xuexiang.template.core.BaseFragment;
import com.xuexiang.template.databinding.ActivityMainBinding;
import com.xuexiang.template.fragment.combat.CombatFragment;
import com.xuexiang.template.fragment.community.CommunityFragment;
import com.xuexiang.template.fragment.other.AboutFragment;
import com.xuexiang.template.fragment.other.SettingsFragment;
import com.xuexiang.template.fragment.profile.ProfileFragment;
import com.xuexiang.template.fragment.learning.LearningFragment;
import com.xuexiang.template.utils.Utils;
import com.xuexiang.template.utils.sdkinit.XUpdateInit;
import com.xuexiang.template.widget.GuideTipsDialog;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.utils.XToastUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.common.CollectionUtils;
import com.xuexiang.xutil.display.Colors;
import com.xuexiang.template.fragment.community.PublishDialogFragment;

/**
 * 程序主页面,只是一个简单的Tab例子
 *
 * @author xuexiang
 * @since 2019-07-07 23:53
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, ClickUtils.OnClick2ExitListener, Toolbar.OnMenuItemClickListener {
    private int communityClickCount = 0;
    private String[] mTitles;
    com.xuexiang.template.User.User user;
    @Override
    protected ActivityMainBinding viewBindingInflate(LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (com.xuexiang.template.User.User) getIntent().getSerializableExtra("user");
        if (user == null) {
            // 可以提示用户重新登录或进行其他处理
            Log.e("MainActivity", "User 对象为空，请检查数据传递");
            // 例如 finish() 或跳转到登录页面
        }


        initViews();

        initData();

        initListeners();
    }

    public User getUser() {
        return user;
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    private void initViews() {
        WidgetUtils.clearActivityBackground(this);

        mTitles = ResUtils.getStringArray(R.array.home_titles);
        binding.includeMain.toolbar.setTitle(mTitles[0]);
        binding.includeMain.toolbar.inflateMenu(R.menu.menu_main);
        binding.includeMain.toolbar.setOnMenuItemClickListener(this);

        initHeader();

        // 主页内容填充
        BaseFragment[] fragments = new BaseFragment[]{
                new CombatFragment(),
                new LearningFragment(),
                new ProfileFragment(),
                new CommunityFragment() // 添加 CommunityFragment
        };
        FragmentAdapter<BaseFragment> adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
        binding.includeMain.viewPager.setOffscreenPageLimit(mTitles.length - 1);
        binding.includeMain.viewPager.setAdapter(adapter);
    }

    private void initData() {
//        GuideTipsDialog.showTips(xthis);
        XUpdateInit.checkUpdate(this, false);
    }

    private void initHeader() {
        binding.navView.setItemIconTintList(null);
        View headerView = binding.navView.getHeaderView(0);
        LinearLayout navHeader = headerView.findViewById(R.id.nav_header);
        RadiusImageView ivAvatar = headerView.findViewById(R.id.iv_avatar);
        TextView tvAvatar = headerView.findViewById(R.id.tv_avatar);
        TextView tvSign = headerView.findViewById(R.id.tv_sign);

        if (Utils.isColorDark(ThemeUtils.resolveColor(this, R.attr.colorAccent))) {
            tvAvatar.setTextColor(Colors.WHITE);
            tvSign.setTextColor(Colors.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivAvatar.setImageTintList(ResUtils.getColors(R.color.xui_config_color_white));
            }
        } else {
            tvAvatar.setTextColor(ThemeUtils.resolveColor(this, R.attr.xui_config_color_title_text));
            tvSign.setTextColor(ThemeUtils.resolveColor(this, R.attr.xui_config_color_explain_text));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivAvatar.setImageTintList(ResUtils.getColors(R.color.xui_config_color_gray_3));
            }
        }

        // TODO: 2019-10-09 初始化数据
        ivAvatar.setImageResource(R.drawable.ic_default_head);
        tvAvatar.setText(R.string.app_name);
        tvSign.setText("这个家伙很懒，什么也没有留下～～");
        navHeader.setOnClickListener(this);
    }

    protected void initListeners() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.includeMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //侧边栏点击事件
        binding.navView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.isCheckable()) {
                binding.drawerLayout.closeDrawers();
                return handleNavigationItemSelected(menuItem);
            } else {
                int id = menuItem.getItemId();
                if (id == R.id.nav_settings) {
                    openNewPage(SettingsFragment.class);
                } else if (id == R.id.nav_about) {
                    openNewPage(AboutFragment.class);
                }
                else {
                    XToastUtils.toast("点击了:" + menuItem.getTitle());
                }
            }
            return true;
        });
        //主页事件监听
        binding.includeMain.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MenuItem item = binding.includeMain.bottomNavigation.getMenu().getItem(position);
                binding.includeMain.toolbar.setTitle(item.getTitle());
                item.setChecked(true);
                updateSideNavStatus(item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.includeMain.bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    /**
     * 处理侧边栏点击事件
     *
     * @param menuItem
     * @return
     */
    private boolean handleNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            binding.includeMain.toolbar.setTitle(menuItem.getTitle());
            binding.includeMain.viewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_privacy) {
            GuideTipsDialog.showTipsForce(this);
        } else if (id == R.id.action_about) {
            openNewPage(AboutFragment.class);
        }
        return false;
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.nav_header) {
            XToastUtils.toast("点击头部！");
        }
    }

    //================Navigation================//

    /**
     * 底部导航栏点击事件
     *
     * @param menuItem
     * @return
     */

    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            binding.includeMain.toolbar.setTitle(menuItem.getTitle());
            binding.includeMain.viewPager.setCurrentItem(index, false);
            updateSideNavStatus(menuItem);

            // 恢复所有底部导航项的原始图标
            for (int i = 0; i < binding.includeMain.bottomNavigation.getMenu().size(); i++) {
                MenuItem item = binding.includeMain.bottomNavigation.getMenu().getItem(i);
                item.setIcon(getOriginalIcon(item.getItemId()));
            }

            // 判断是否是社区中心的菜单项
            if (menuItem.getItemId() == R.id.nav_comuit) {
                // 将图标切换为“+”
                menuItem.setIcon(R.drawable.ic_add); // 替换为你的“+”符号图标

                communityClickCount++; // 增加点击计数
                if (communityClickCount >= 2) {
                    openPublishDialog(); // 打开发布界面
                    communityClickCount = 0; // 重置计数
                }
                return true;
            }

            return true;
        }
        return false;
    }

    // 打开发布界面的逻辑
    private void openPublishDialog() {
        PublishDialogFragment dialogFragment = new PublishDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "PublishDialog");
    }



    /**
     * 获取菜单项的原始图标
     */
    private int getOriginalIcon(int itemId) {
        switch (itemId) {
            case R.id.nav_news:
                return R.drawable.ic_menu_news;
            case R.id.nav_trending:
                return R.drawable.ic_menu_trending;
            case R.id.nav_profile:
                return R.drawable.ic_menu_person;
            case R.id.nav_comuit:
                return R.drawable.ic_menu_comuit;  // 社区中心的原图标
            default:
                return R.drawable.ic_menu_comuit;
        }
    }


    /**
     * 更新侧边栏菜单选中状态
     *
     * @param menuItem
     */
    private void updateSideNavStatus(MenuItem menuItem) {
        MenuItem side = binding.navView.getMenu().findItem(menuItem.getItemId());
        if (side != null) {
            side.setChecked(true);
        }
    }

    /**
     * 菜单、返回键响应
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }

    @Override
    public void onRetry() {
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.includeMain.bottomNavigation.setOnNavigationItemSelectedListener(this);
        // 重新设置底部导航栏的状态
        MenuItem item = binding.includeMain.bottomNavigation.getMenu().getItem(binding.includeMain.viewPager.getCurrentItem());
        item.setChecked(true);
    }

}
