package com.stdili.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.stdili.R;
import com.stdili.adapters.OnboardingAdapter;

public class OnboardingActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "stdili_prefs";
    public static final String KEY_ONBOARDING_DONE = "onboarding_done";

    private ViewPager2 viewPager;
    private Button btnContinue, btnSkip;
    private LinearLayout dotsLayout;
    private int[] layouts = {R.layout.onboarding_slide1, R.layout.onboarding_slide2, R.layout.onboarding_slide3, R.layout.onboarding_slide4};
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnContinue = findViewById(R.id.btnContinue);
        btnSkip = findViewById(R.id.btnSkip);
        dotsLayout = findViewById(R.id.dots);

        adapter = new OnboardingAdapter(layouts);
        viewPager.setAdapter(adapter);

        addDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDots(position);
                if (position == layouts.length - 1) {
                    btnContinue.setText("Get Started");
                } else {
                    btnContinue.setText("Continue");
                }
            }
        });

        btnContinue.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem() + 1;
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                markOnboardingDone();
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
            }
        });

        btnSkip.setOnClickListener(v -> {
            markOnboardingDone();
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        });
    }

    private void markOnboardingDone() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply();
    }

    private void addDots(int position) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < layouts.length; i++) {
            View dot = new View(this);
            dot.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
            dot.setBackgroundResource(i == position ? R.drawable.dot_selected : R.drawable.dot_unselected);
            dotsLayout.addView(dot);
        }
    }
}