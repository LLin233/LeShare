package androidpath.ll.leshare.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;

import java.util.Locale;

import androidpath.ll.leshare.R;
import androidpath.ll.leshare.View.Fragments.FriendsFragment;
import androidpath.ll.leshare.View.Fragments.InboxFragment;

/**
 * Created by Le on 2015/5/8.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    final int PAGE_COUNT = 2;

    protected Context mContext;
    private int tabIcons[] = {R.mipmap.ic_tab_inbox, R.mipmap.ic_tab_friends};


    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new InboxFragment();
            case 1:
                return new FriendsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.tab_title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.tab_title_section2).toUpperCase(l);
        }
        return null;
    }

    @Override
    public int getPageIconResId(int position) {
        return tabIcons[position];
    }
}
