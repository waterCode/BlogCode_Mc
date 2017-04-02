package com.example.myapplication;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab);
        TabAdapter tabAdapter=new TabAdapter(getSupportFragmentManager());
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(tabAdapter);

        tabLayout.setupWithViewPager(viewPager);

    }

    public static class TabFragment extends Fragment{

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v= inflater.inflate(R.layout.fragment_layout,container,false);
            return v;
        }
    }


    private class TabAdapter extends FragmentPagerAdapter{

        String[] title={"TAB1","TAB2","TAB3","TAB4"};
        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {
            return new TabFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return 4;
        }
    }
}

