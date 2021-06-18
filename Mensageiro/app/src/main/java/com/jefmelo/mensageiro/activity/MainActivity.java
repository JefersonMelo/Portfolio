package com.jefmelo.mensageiro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jefmelo.mensageiro.R;
import com.jefmelo.mensageiro.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        //setContentView(R.layout.activity_main);

        firebaseAuth = firebaseAuth.getInstance();
        checarStatusUsuario();

        String numTelefone = getIntent().getStringExtra("numeroTelefone");
        mainBinding.textViewNumTelefoneLogado.setText("Verificando Número: \n" + numTelefone);

        mainBinding.buttonSair.setOnClickListener(v -> {
            firebaseAuth.signOut();
            checarStatusUsuario();
        });
    }

    private void checarStatusUsuario() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            String telefone = firebaseUser.getPhoneNumber();
            mainBinding.textViewNumTelefoneLogado.setText(telefone);
        }else{
            finish();
        }
    }
}