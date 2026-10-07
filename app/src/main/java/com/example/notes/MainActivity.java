package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPrefManager sharedPrefManager;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefManager = SharedPrefManager.getInstance(this);
        currentUserEmail = sharedPrefManager.readString("CURRENT_USER_EMAIL", "");

        if (currentUserEmail.isEmpty()) {
            openLoginAndClearTask();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        setupBackPressedHandler();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        refreshNavigationHeader();

        if (savedInstanceState == null) {
            openFragment(new AllNotesFragment(), R.string.all_notes);
            navigationView.setCheckedItem(R.id.nav_all_notes);
        }
    }

    public void refreshNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = headerView.findViewById(R.id.textView_header_name);
        TextView emailTextView = headerView.findViewById(R.id.textView_header_email);

        emailTextView.setText(currentUserEmail);

        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(this, "USER_DATABASE", null, 1);

        User user = userDatabaseHelper.getUserByEmail(currentUserEmail);

        if (user != null)
            nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        else
            nameTextView.setText(R.string.notes_user);
    }

    private void openFragment(Fragment fragment, int titleResourceId) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(titleResourceId);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_all_notes)
            openFragment(new AllNotesFragment(), R.string.all_notes);
        else if (itemId == R.id.nav_favorites)
            openFragment(new FavoritesFragment(), R.string.favorites);
        else if (itemId == R.id.nav_sorted)
            openFragment(new SortedNotesFragment(), R.string.sorted);
        else if (itemId == R.id.nav_profile)
            openFragment(new ProfileFragment(), R.string.profile);
        else if (itemId == R.id.nav_logout) {
            logout();
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        sharedPrefManager.remove("CURRENT_USER_EMAIL");
        openLoginAndClearTask();
    }

    private void openLoginAndClearTask() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    finish();
            }
        });
    }
}