package com.app.meurole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private TextView titulo_login,language_login,novo_usuario;
    private EditText email_login, senha_login;
    private Button botao_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
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
                if(language_login.getText().toString().equalsIgnoreCase("en-US")) {
                    titulo_login.setText("Sign In");
                    senha_login.setHint("Password");
                    botao_login.setText("Enter");
                    language_login.setText("pt-BR");
                }else{
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
                Intent intent = new Intent(login.this, cadastro.class);
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

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            email_login.setVisibility(View.INVISIBLE);
                            senha_login.setVisibility(View.INVISIBLE);
                            titulo_login.setText("LOGADO");
                            botao_login.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }
}