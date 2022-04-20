package com.liucj.jetpack_base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.liucj.jetpack_base.view.AppBottomBar;
import com.liucj.jetpack_base.view.NavGraphBuilder;
import com.liucj.lib_common.livedata.LiveDataBus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private NavController navController;
    private AppBottomBar navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(fragment);
        NavGraphBuilder.build(this, navController, fragment.getId());

        navView.setOnNavigationItemSelectedListener(this);
        }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        navController.navigate(item.getItemId());
//        LiveDataBusKt.INSTANCE.<String>with("LiveDataBus")
//                .setValue(item.getTitle()+":"+item.getItemId())
        LiveDataBus.get().with("LiveDataBus")
                .setValue(item.getTitle()+":"+item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }
}