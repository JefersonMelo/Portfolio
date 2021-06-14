package com.jefmelo.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.jefmelo.myapplication.R;
import com.jefmelo.myapplication.databinding.ActivityValidadorBinding;
import com.jefmelo.myapplication.helper.Preferencias;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ValidadorActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ActivityValidadorBinding binding;
    LoginActivity loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityValidadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_validador);

        //se não receber o código, solicitar reenvio
        binding.textViewReenviarCod.setOnClickListener(v -> {
            String tel = loginActivity.telFormatado.toString();
            if (TextUtils.isEmpty(tel)) {
                Toast.makeText(this, "Por Favor, Digite Um Número de Telefone Válido.", Toast.LENGTH_LONG).show();
            } else {
                resendPhoneNumberVerification(tel, loginActivity.forceResendingToken);
            }
        });

        binding.btnValidador.setOnClickListener(v -> {
            String sms = binding.editTextSmsValidador.toString();
            if (TextUtils.isEmpty(sms)) {
                Toast.makeText(this, "Por Favor, Digite o Código...", Toast.LENGTH_LONG).show();
            } else {
                verifyNumberPhoneWithCode(loginActivity.mVerificationId, sms);
            }
        });
    }

    //resend
    private void resendPhoneNumberVerification(String tel, PhoneAuthProvider.ForceResendingToken token) {
        progressDialog.setMessage("Reenviando...");
        progressDialog.show();

        PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(loginActivity.firebaseAuth)
                .setPhoneNumber(loginActivity.telFormatado)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(loginActivity.mCallbacks)
                .setForceResendingToken(token)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(authOptions);
    }

    private void verifyNumberPhoneWithCode(String verificationId, String sms) {
        progressDialog.setMessage("Verificando...");
        progressDialog.show();

        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationId, sms);
        signInWithPhoneAuthCredential(authCredential);
    }

    protected void signInWithPhoneAuthCredential(PhoneAuthCredential authCredential) {
        progressDialog.setMessage("Conectando...");
        loginActivity.firebaseAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        String tel = loginActivity.firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(ValidadorActivity.this, "Conectado com: " + tel, Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ValidadorActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

/*
//Recuperar Dados Preferncias do Usuário
Preferencias preferencias = new Preferencias(ValidadorActivity.this);
HashMap<String, String> usuario = preferencias.getDadosUsuario();

String tokenGerado = usuario.get("token");
String tokenDigitado = codValidacao.getText().toString();

if (tokenDigitado.equals(tokenGerado)){
    Toast.makeText(ValidadorActivity.this, "Token Validado!", Toast.LENGTH_LONG).show();
}else{
    Toast.makeText(ValidadorActivity.this, "Token Errado!", Toast.LENGTH_LONG).show();
}
*/