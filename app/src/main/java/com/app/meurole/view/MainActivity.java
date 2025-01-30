package com.app.meurole.view;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.app.meurole.R;
import com.app.meurole.view.fragment.EventCreateFragment;
import com.app.meurole.view.fragment.EventDetailFragment;
import com.app.meurole.view.fragment.EventListFragment;
import com.app.meurole.view.fragment.UserProfileFragment;
import com.app.meurole.view.fragment.UserRegistrationEventFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    public void onResume() {
        super.onResume();
        Menu menu = bottomNav.getMenu();
        MenuItem meusEventosItem = menu.findItem(R.id.menu_meus_eventos);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        meusEventosItem.setVisible(user != null);
    }

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
            } else if (itemId == R.id.menu_meus_eventos) {
                fragment = new UserRegistrationEventFragment();
            }
            else if (itemId == R.id.menu_user_profile) {
                // Verificar se está logado
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    // Não logado -> vai para UserLoginActivity
                    Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                    // Se quiser, informe que depois do login deve abrir o perfil
                    intent.putExtra("FRAGMENT_TO_OPEN", "USER_PROFILE");
                    startActivity(intent);
                    return false; // não carrega fragment
                } else {
                    // Logado -> carrega o UserProfileFragment
                    fragment = new UserProfileFragment();
                }
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

        // Sempre que a MainActivity iniciar, verifica o login para exibir/ocultar Meus Eventos.
        updateMenuVisibility();

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
     * Verifica o usuário logado e exibe/oculta "Meus Eventos" no BottomNav.
     */
    public void updateMenuVisibility() {
        Menu menu = bottomNav.getMenu();
        MenuItem meusEventosItem = menu.findItem(R.id.menu_meus_eventos);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Se user != null, está logado => visível. Caso contrário, invisível.
        meusEventosItem.setVisible(user != null);
    }

    /**
     * Metodo para trocar o Fragment no container (@+id/fragment_container).
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}