package com.app.meurole;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class cadastro extends AppCompatActivity {

    private EditText email_cadastro,senha_cadastro;
    private Button botao_cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email_cadastro = findViewById(R.id.email_cadastro);
        senha_cadastro = findViewById(R.id.senha_cadastro);
        botao_cadastro = findViewById(R.id.botao_cadastro);

        botao_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
                if(imm.isActive())
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                String email, senha;
                email = email_cadastro.getText().toString();
                senha = senha_cadastro.getText().toString();

                if(email.isEmpty() || senha.isEmpty()){
                    Snackbar msg = Snackbar.make(v,"Preencha os campos corretamente",Snackbar.LENGTH_SHORT);
                    msg.setBackgroundTint(Color.RED);
                    msg.setTextColor(Color.WHITE);
                    msg.show();
                }
                else{
                    CadastrarUuario(v,email,senha);
                    Intent intent = new Intent(cadastro.this, login.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }

    public void CadastrarUuario(View v, String email, String senha){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Snackbar msg = Snackbar.make(v,"Usuário Cadastrado",Snackbar.LENGTH_SHORT);
                    msg.setBackgroundTint(Color.GREEN);
                    msg.setTextColor(Color.WHITE);
                    msg.show();
                }else{
                    String erro;

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erro = "Senha fraca";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "Email inválido";
                    }catch (FirebaseAuthUserCollisionException e){
                        erro = "Usuário já cadastrado";
                    }catch (Exception e){
                        erro = "Erro desconhecido. Contante o Admin.";
                        System.out.println("Erro: \n"+e.toString());
                    }

                    Snackbar msg = Snackbar.make(v,erro,Snackbar.LENGTH_SHORT);
                    msg.setBackgroundTint(Color.RED);
                    msg.setTextColor(Color.WHITE);
                    msg.show();

                }
            }
        });
    }

}