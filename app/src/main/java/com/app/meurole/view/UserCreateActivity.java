package com.app.meurole.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.meurole.R;
import com.app.meurole.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class UserCreateActivity extends AppCompatActivity {

    private EditText nome_cadastro,email_cadastro,senha_cadastro,dob_cadastro,cpf_cadastro,confirmar_senha_cadastro;
    private Button botao_cadastro;

    DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance().getReference();

        nome_cadastro = findViewById(R.id.nome_cadastro);
        email_cadastro = findViewById(R.id.email_cadastro);
        senha_cadastro = findViewById(R.id.senha_cadastro);
        botao_cadastro = findViewById(R.id.botao_cadastro);
        dob_cadastro = findViewById(R.id.dob_cadastro);
        cpf_cadastro = findViewById(R.id.cpf_cadastro);
        confirmar_senha_cadastro = findViewById(R.id.confirmar_senha_cadastro);

        dob_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obter a data atual
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Criar o DatePickerDialog
                DatePickerDialog datePicker = new DatePickerDialog(
                        UserCreateActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Formatar a data no estilo "dd/MM/yyyy"
                            String formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                            dob_cadastro.setText(formattedDate);
                        },
                        year, month, day
                );

                // Exibir o DatePickerDialog
                datePicker.show();
            }
        });

        botao_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
                if(imm.isActive())
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                String nome,cpf,email, senha,confirma_senha;
                Date dob;
                nome = nome_cadastro.getText().toString();
                cpf = cpf_cadastro.getText().toString();
                email = email_cadastro.getText().toString();
                senha = senha_cadastro.getText().toString();
                confirma_senha = confirmar_senha_cadastro.getText().toString();
                Calendar calendar = Calendar.getInstance();
                calendar.set(2003,Calendar.JUNE,23);
                dob = calendar.getTime();

                User user = new User(nome,cpf,dob,email);

            if(email.isEmpty() || senha.isEmpty()){
                    Snackbar msg = Snackbar.make(v,"Preencha os campos corretamente",Snackbar.LENGTH_SHORT);
                    msg.setBackgroundTint(Color.RED);
                    msg.setTextColor(Color.WHITE);
                    msg.show();
                } else if (!senha.equals(confirma_senha)) {
                    Snackbar msg = Snackbar.make(v,"Senhas não conferem",Snackbar.LENGTH_SHORT);
                    msg.setBackgroundTint(Color.RED);
                    msg.setTextColor(Color.WHITE);
                    msg.show();
                } else{
                    CadastrarUsuarioAuth(v, user,senha);
                    Intent intent = new Intent(UserCreateActivity.this, UserLoginActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }

    public void CadastrarUsuarioAuth(View v, User user, String senha){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    CadastrarUsuario(v,uid, user);
                    //Snackbar msg = Snackbar.make(v,"Usuário Cadastrado",Snackbar.LENGTH_SHORT);
                    //msg.setBackgroundTint(Color.GREEN);
                    //msg.setTextColor(Color.WHITE);
                    //msg.show();
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

    public void CadastrarUsuario(View v, String uid, User user){
        database.child("usuarios").child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(v.getContext(),"Dados salvos",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Erro ao salvar cliente.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}