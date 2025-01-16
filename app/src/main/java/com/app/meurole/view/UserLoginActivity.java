package com.app.meurole.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.meurole.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserLoginActivity extends AppCompatActivity {

    private TextView titulo_login, language_login, novo_usuario;
    private EditText email_login, senha_login;
    private Button botao_login;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titulo_login = findViewById(R.id.titulo_login);
        language_login = findViewById(R.id.language_login);
        email_login = findViewById(R.id.email_login);
        senha_login = findViewById(R.id.senha_login);
        botao_login = findViewById(R.id.botao_login);
        novo_usuario = findViewById(R.id.novo_usuario);


        language_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (language_login.getText().toString().equalsIgnoreCase("en-US")) {
                    titulo_login.setText("Sign In");
                    senha_login.setHint("Password");
                    botao_login.setText("Enter");
                    language_login.setText("pt-BR");
                } else {
                    titulo_login.setText("Acessar");
                    senha_login.setHint("Senha");
                    botao_login.setText("Entrar");
                    language_login.setText("en-US");
                }
            }
        });

        novo_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLoginActivity.this, UserCreateActivity.class);
                startActivity(intent);
                finish();
            }
        });

        botao_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, senha;
                email = email_login.getText().toString();
                senha = senha_login.getText().toString();

                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Recuperamos o eventId que recebemos
                                    String eventId = getIntent().getStringExtra("EVENT_ID");

                                    // Criamos a Intent para devolver ao Fragment
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("EVENT_ID", eventId);

                                    // Devolvemos RESULT_OK
                                    setResult(Activity.RESULT_OK, returnIntent);

                                    finish(); // fecha a UserLoginActivity e volta para o Fragment que chamou

                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Erro no login: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }


}