package com.jefmelo.mensageiro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.jefmelo.mensageiro.databinding.ActivityValidarCodigoMainBinding;

import java.util.concurrent.TimeUnit;

public class ValidarCodigoMainActivity extends AppCompatActivity {

    protected ActivityValidarCodigoMainBinding validarBinding;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private String tel;
    private static final String TAG = "MAIN_TAG";

    private ProgressDialog progressDialog;

    CadastrarTelefoneMainActivity cadastrarTelefoneActivity = new CadastrarTelefoneMainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validarBinding = ActivityValidarCodigoMainBinding.inflate(getLayoutInflater());
        setContentView(validarBinding.getRoot());

        String numTelefone = getIntent().getStringExtra("numeroTelefone");
        validarBinding.textViewChecandoNumero.setText("Verificando Número: \n" + numTelefone.toString());

        tel = cadastrarTelefoneActivity.telFormatado.toString();

        //Tornar Visivel após completar carregamento da ActivityCadastrar
        //validarBinding.IdActiviyValidarCodigo.setVisibility(View.VISIBLE);

        //Bloqueio de touch durante progresso
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Aguarde...");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // Este retorno de chamada será invocado em duas situações:
                // 1 - Verificação instantânea. Em alguns casos, o número de telefone pode ser instantâneo
                // verificado sem a necessidade de enviar ou inserir um código de verificação.
                // 2 - Recuperação automática. Em alguns dispositivos, o Google Play Services pode automaticamente
                // detecta o SMS de verificação de entrada e executa a verificação sem
                // ação do usuário.
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Este retorno de chamada é invocado em uma solicitação inválida para verificação feita,
                // por exemplo, se o formato do número de telefone não for válido.
                progressDialog.dismiss();
                Toast.makeText(ValidarCodigoMainActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
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

                Intent intent = new Intent(ValidarCodigoMainActivity.this, MainActivity.class);
                //intent.putExtra("numeroTelefone", numTelefone);
                startActivity(intent);
                //finish();
            }
        };

        validarBinding.btnValidador.setOnClickListener(v -> {
            String codigo = validarBinding.editTextSmsValidador.toString();
            if (TextUtils.isEmpty(codigo)) {
                Toast.makeText(this, "Por Favor, Digite o Código...", Toast.LENGTH_LONG).show();
            } else {
                verifyNumberPhoneWithCode(mVerificationId, codigo);
                //startActivity(new Intent(ValidarCodigoMainActivity.this, MainActivity.class));
            }
        });

        //se não receber o código, solicitar reenvio
        validarBinding.textViewReenviarCod.setOnClickListener(v -> {
            if (TextUtils.isEmpty(tel)) {
                Toast.makeText(this, "Por Favor, Digite O Código Recebido", Toast.LENGTH_LONG).show();
            } else {
                resendPhoneNumberVerification(tel, forceResendingToken);
            }
        });
    }

    protected void startPhoneNumberVerification(String telFormatado) {
        progressDialog.setMessage("Verificando Número do Telefone...");
        progressDialog.show();

        PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(telFormatado)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(authOptions);
    }

    private void resendPhoneNumberVerification(String tel, PhoneAuthProvider.ForceResendingToken token) {
        progressDialog.setMessage("Reenviando...");
        progressDialog.show();

        PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(tel)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(authOptions);
    }

    protected void verifyNumberPhoneWithCode(String verificationId, String codigo) {
        progressDialog.setMessage("Verificando...");
        progressDialog.show();

        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationId, codigo);
        signInWithPhoneAuthCredential(authCredential);
    }

    protected void signInWithPhoneAuthCredential(PhoneAuthCredential authCredential) {
        progressDialog.setMessage("Conectando...");
        firebaseAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(authResult -> {
                    progressDialog.dismiss();
                    String telefone = firebaseAuth.getCurrentUser().getPhoneNumber();
                    Toast.makeText(ValidarCodigoMainActivity.this, "Conectado com: " + telefone, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ValidarCodigoMainActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


    }

}
