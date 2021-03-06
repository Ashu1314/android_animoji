package com.wen.hugo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.avos.avoscloud.AVUser;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.wen.hugo.R;
import com.wen.hugo.chatPage.ChatActivity;
import com.wen.hugo.data.DataRepository;
import com.wen.hugo.followPage.FollowPageActivity;
import com.wen.hugo.followPage.FollowPageFragment;
import com.wen.hugo.login.LoginActivity;
import com.wen.hugo.mySubject.MySubjectActivity;
import com.wen.hugo.publishStatus.PublishStatusActivity;
import com.wen.hugo.publishSubject.PublishSubjectActivity;
import com.wen.hugo.settingPage.SetttingPageActivity;
import com.wen.hugo.subjectPage.SubjectPageFragment;
import com.wen.hugo.subjectPage.SubjectPagePresenter;
import com.wen.hugo.timeLine.TimeLineFragment;
import com.wen.hugo.timeLine.TimeLinePresenter;
import com.wen.hugo.userPage.UserPageActivity;
import com.wen.hugo.util.schedulers.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * TODOx
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    private final static int LOGOUT = 13;

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    Adapter adapter;

    TextBadgeItem numberBadgeItem;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    int currentMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(savedInstanceState);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TimeLineFragment)adapter.getItem(0)).send();
            }
        });

        numberBadgeItem = new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(android.R.color.holo_blue_bright);

        bottomNavigationBar.clearAll();
        bottomNavigationBar.setFab(fab);
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Home").setActiveColorResource(R.color.icon_select_color))
                .addItem(new BottomNavigationItem(R.drawable.ic_book_white_24dp, "Books").setActiveColorResource(R.color.icon_select_color))
                .addItem(new BottomNavigationItem(R.drawable.ic_music_note_white_24dp, "Music").setActiveColorResource(R.color.icon_select_color).setBadgeItem(numberBadgeItem))
                .setFirstSelectedPosition(0)
                .initialise();

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                switch (currentMenuItem){
                    case R.id.nav_home:
                        UserPageActivity.go(MainActivity.this, AVUser.getCurrentUser());
                        break;
                    case R.id.nav_messages:
                        FollowPageActivity.goFollow(MainActivity.this, FollowPageFragment.TYPE_FOLLOWER);
                        break;
                    case R.id.nav_friends:
                        FollowPageActivity.goFollow(MainActivity.this, FollowPageFragment.TYPE_FOLLOWING);
                        break;
                    case R.id.nav_discussion:
                        Intent intent = new Intent(MainActivity.this, PublishStatusActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.add_subject:
                        Intent subject = new Intent(MainActivity.this, PublishSubjectActivity.class);
                        startActivity(subject);
                        break;
                    case R.id.my_subject:
                        MySubjectActivity.go(MainActivity.this);
                        break;
                    case R.id.my_setting:
                        Intent setting = new Intent(MainActivity.this, SetttingPageActivity.class);
                        startActivityForResult(setting, LOGOUT);
                        break;
                }
                currentMenuItem = 0;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }


    private void setupViewPager(Bundle savedInstanceState) {
        adapter = new Adapter(getSupportFragmentManager());

        if(savedInstanceState==null) {

            TimeLineFragment timeLineFragment = TimeLineFragment.newInstance(false);
            new TimeLinePresenter(DataRepository.getInstance(), timeLineFragment, SchedulerProvider.getInstance());
            adapter.addFragment(timeLineFragment);


            SubjectPageFragment subjectPageFragment = SubjectPageFragment.newInstance();
            new SubjectPagePresenter(DataRepository.getInstance(), subjectPageFragment, SchedulerProvider.getInstance());
            adapter.addFragment(subjectPageFragment);

//            UserPageFragment userPageFragment = UserPageFragment.newInstance();
//            new UserPagePresenter(DataRepository.getInstance(), userPageFragment, SchedulerProvider.getInstance());
//            adapter.addFragment(userPageFragment);

            EaseConversationListFragment conversationListFragment = new EaseConversationListFragment();
            adapter.addFragment(conversationListFragment);
            conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

                @Override
                public void onListItemClicked(EMConversation conversation) {
                    startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
                }
            });
        }else{
            adapter.addFragment(getSupportFragmentManager().findFragmentByTag(Adapter.makeFragmentName(viewPager.getId(),0)));
            adapter.addFragment(getSupportFragmentManager().findFragmentByTag(Adapter.makeFragmentName(viewPager.getId(),1)));
            adapter.addFragment(getSupportFragmentManager().findFragmentByTag(Adapter.makeFragmentName(viewPager.getId(),2)));
        }

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(2);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(false);
                    mDrawerLayout.closeDrawers();
                    currentMenuItem = menuItem.getItemId();
                    return true;
                }
            });
        ImageView avatarView = navigationView.getHeaderView(0).findViewById(R.id.avatarView);
        ImageView avatarView2 = navigationView.getHeaderView(0).findViewById(R.id.avatarView2);
        TextView tv = navigationView.getHeaderView(0).findViewById(R.id.tvs);
        tv.setText(AVUser.getCurrentUser().getUsername());
        EaseImageUtils.displayAvatar(AVUser.getCurrentUser().getUsername(), avatarView,avatarView2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadLabel();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        bottomNavigationBar.selectTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public static String makeFragmentName(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            refreshUIWithMessage();
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {}
    };

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                updateUnreadLabel();
 //               if (viewPager.getCurrentItem() == 2) {
                    // refresh conversation list
                    if ((EaseConversationListFragment)adapter.getItem(2) != null) {
                        ((EaseConversationListFragment)adapter.getItem(2)).refresh();
 //                   }
                }
            }
        });
    }

    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            if (numberBadgeItem != null) {
                numberBadgeItem.setText(String.valueOf(count));
                numberBadgeItem.show(true);
            }
        }else {
            numberBadgeItem.hide(true);
        }
    }

    public int getUnreadMsgCountTotal() {
        return EMClient.getInstance().chatManager().getUnreadMsgsCount();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOGOUT) {
                AVUser.logOut();
                SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                sp.edit().putString("loginToken", "");
                EMClient.getInstance().logout(true);
                startActivity(new Intent(this, LoginActivity.class));
                this.finish();
            }
        }
    }


}

