package com.prakharsaxena.livesaidarshan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DashFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selected=null;
                    switch(menuItem.getItemId()){
                        case R.id.navigation_dash:
                            selected=new DashFragment();
                            break;
                        case R.id.navigation_tags:
                            selected=new TagsFragment();
                            break;
                        case R.id.navigation_items:
                            selected=new ItemsFragment();
                            break;
                        case R.id.navigation_more:
                            selected=new MoreFragment();
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selected).commit();
                    return true;
                }
            };
}
