package com.jefmelo.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.jefmelo.myapplication.R;
import com.jefmelo.myapplication.databinding.ActivityLoginBinding;
import com.jefmelo.myapplication.databinding.ActivityMainBinding;
import com.jefmelo.myapplication.helper.Permissoes;
import com.jefmelo.myapplication.helper.Preferencias;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.jefmelo.myapplication.utils.MaskFormatUtil;

public class LoginActivity extends AppCompatActivity {

    private final String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    final Handler handler = new Handler();
    protected String telFormatado;
    private ActivityLoginBinding binding;
    protected PhoneAuthProvider.ForceResendingToken forceResendingToken;
    protected PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    protected String mVerificationId;
    private static final String TAG = "MAIN_TAG";
    protected FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    ValidadorActivity validadorActivity = new ValidadorActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Permissoes.validarPermissoes(1, this, permissoesNecessarias);

        //Definição das Máscaras
        binding.editTexTelefone.addTextChangedListener(MaskFormatUtil.mask(binding.editTexTelefone, MaskFormatUtil.FORMAT_FONE));
        binding.editTextCodPais.addTextChangedListener(MaskFormatUtil.mask(binding.editTextCodPais, MaskFormatUtil.FORMAT_COD_PAIS));
        binding.editTextDDD.addTextChangedListener(MaskFormatUtil.mask(binding.editTextDDD, MaskFormatUtil.FORMAT_COD_AREA));

        //Autentucação
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Aguarde...");
        progressDialog.setCanceledOnTouchOutside(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // Este retorno de chamada será invocado em duas situações:
                // 1 - Verificação instantânea. Em alguns casos, o número de telefone pode ser instantâneo
                // verificado sem a necessidade de enviar ou inserir um código de verificação.
                // 2 - Recuperação automática. Em alguns dispositivos, o Google Play Services pode automaticamente
                // detecta o SMS de verificação de entrada e executa a verificação sem
                // ação do usuário.
                validadorActivity.signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Este retorno de chamada é invocado em uma solicitação inválida para verificação feita,
                // por exemplo, se o formato do número de telefone não for válido.
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                // O código de verificação de SMS foi enviado para o número de telefone fornecido, nós
                // agora precisa pedir ao usuário para inserir o código e, em seguida, construir uma credencial
                // combinando o código com um ID de verificação.
                Log.d(TAG, "onCodeSent" + verificationId);
                mVerificationId = verificationId;
                forceResendingToken = token;
                progressDialog.dismiss();
            }
        };

        binding.btnCadastrar.setOnClickListener(v -> {
            String nomeUsuario = binding.editTextNome.getText().toString();
            String telSemFormatar = binding.editTextCodPais.getText().toString()
                    + binding.editTextDDD.getText().toString()
                    + binding.editTexTelefone.getText().toString();

            telFormatado = "+" + MaskFormatUtil.unmask(telSemFormatar);

            if (TextUtils.isEmpty(telFormatado)) {
                Toast.makeText(this, "Por Favor, Digite o Telefone...", Toast.LENGTH_LONG).show();
            } else {
                startPhoneNumberVerification(telFormatado);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class);
                        startActivity(intent);
                    }
                }, 3000);
            }
        });
    }

    private void startPhoneNumberVerification(String telFormatado) {
        progressDialog.setMessage("Verificando Número do Telefone...");
        progressDialog.show();

        PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(telFormatado)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(authOptions);
        //startActivity(new Intent(LoginActivity.this, ValidadorActivity.class));
    }

    //Requerimento de Permissões
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int resultado : grantResults) {
            if (resultado == PackageManager.PERMISSION_DENIED) {
                alertaPermissao();
            }
        }
    }

    private void alertaPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Permissões Negadas");
        builder.setMessage("Para Prosseguir a Permissão Deve ser Aceita");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
/*
handler.postDelayed(new Runnable() {
@Override
public void run() {
    Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class);
    startActivity(intent);
    finish();
}
}, 6000);
*/