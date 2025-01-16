package com.app.meurole.view;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.app.meurole.R;
import com.app.meurole.view.fragment.EventCreateFragment;
import com.app.meurole.view.fragment.EventDetailFragment;
import com.app.meurole.view.fragment.EventListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Layout com o FrameLayout + BottomNavigationView

        bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                fragment = new EventListFragment();
            } else if (itemId == R.id.menu_criar_eventos) {
                fragment = new EventCreateFragment();
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            }
            return false;
        });

        if (getIntent() != null) {
            String fragmentToOpen = getIntent().getStringExtra("FRAGMENT_TO_OPEN");
            if ("EVENT_DETAIL".equals(fragmentToOpen)) {
                String eventId = getIntent().getStringExtra("EVENT_ID");
                if (eventId != null) {
                    // Abre o fragment de detalhes
                    EventDetailFragment detailFragment = EventDetailFragment.newInstance(eventId);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, detailFragment)
                            .commit();
                    return;
                }
            } else if ("EVENT_LIST".equals(fragmentToOpen)) {
                bottomNav.setSelectedItemId(R.id.menu_home);
                return;
            }
        }

        // Se você quer que a tela inicial, ao abrir, seja o "Início" (listagem):
        bottomNav.setSelectedItemId(R.id.menu_home);
    }

    /**
     * Método para trocar o Fragment no container (@+id/fragment_container).
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}