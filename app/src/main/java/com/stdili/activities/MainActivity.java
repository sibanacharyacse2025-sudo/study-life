package com.stdili.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.stdili.R;
import com.stdili.fragments.HomeFragment;
import com.stdili.fragments.StudyFragment;
import com.stdili.fragments.AIFragment;
import com.stdili.fragments.NetworkFragment;
import com.stdili.fragments.ProfileFragment;
import com.stdili.utils.FirebaseHelper;
import com.stdili.utils.SecureSessionManager;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ExtendedFloatingActionButton fab;
    private boolean isGuest = false;
    private SecureSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SecureSessionManager(this);

        isGuest = getIntent().getBooleanExtra("IS_GUEST", false);
        boolean hasBackendSession = sessionManager.getAccessToken() != null && sessionManager.getUserId() != null;

        // Check if user is logged in (unless guest)
        if (!isGuest && FirebaseAuth.getInstance().getCurrentUser() == null && !hasBackendSession) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        if (!isGuest) FirebaseHelper.syncFcmTokenIfLoggedIn();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fab = findViewById(R.id.fab);
        fab.shrink();
        if (isGuest) {
            fab.hide();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            
            if (isGuest && (id == R.id.nav_network || id == R.id.nav_profile)) {
                Toast.makeText(this, "Please login to access this feature", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_study) {
                selectedFragment = new StudyFragment();
            } else if (id == R.id.nav_ai) {
                selectedFragment = new AIFragment();
            } else if (id == R.id.nav_network) {
                selectedFragment = new NetworkFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
            }
            return true;
        });

        // Load default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

        fab.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AIFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_ai);
        });
    }
}