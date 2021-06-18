package com.jefmelo.mensageiro.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Toast;

import com.jefmelo.mensageiro.databinding.ActivityCadastrarTelefoneMainBinding;
import com.jefmelo.mensageiro.helper.Permissoes;

import com.jefmelo.mensageiro.util.MaskFormatUtil;


public class CadastrarTelefoneMainActivity extends AppCompatActivity {

    private final String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    private ActivityCadastrarTelefoneMainBinding telefoneBinding;

    protected String telFormatado;

    //Progressive Dialog
    private ProgressDialog progressDialog;

    ValidarCodigoMainActivity validarCodigoMainActivity = new ValidarCodigoMainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        telefoneBinding = ActivityCadastrarTelefoneMainBinding.inflate(getLayoutInflater());
        setContentView(telefoneBinding.getRoot());

        Permissoes.validarPermissoes(1, this, permissoesNecessarias);

        //Definição das Máscaras
        telefoneBinding.editTexTelefone.addTextChangedListener(MaskFormatUtil.mask(telefoneBinding.editTexTelefone, MaskFormatUtil.FORMAT_FONE));
        telefoneBinding.editTextCodPais.addTextChangedListener(MaskFormatUtil.mask(telefoneBinding.editTextCodPais, MaskFormatUtil.FORMAT_COD_PAIS));
        telefoneBinding.editTextCodArea.addTextChangedListener(MaskFormatUtil.mask(telefoneBinding.editTextCodArea, MaskFormatUtil.FORMAT_COD_AREA));

        //Tornar visível a activity
        telefoneBinding.idActivityCadastrarTelefone.setVisibility(View.VISIBLE);

        //Bloqueio de touch durante progresso
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Aguarde...");
        progressDialog.setCanceledOnTouchOutside(false);


        telefoneBinding.btnCadastrar.setOnClickListener(v -> {
            String nomeUsuario = telefoneBinding.editTextNome.getText().toString();
            String telSemFormatar = telefoneBinding.editTextCodPais.getText().toString()
                    + telefoneBinding.editTextCodArea.getText().toString()
                    + telefoneBinding.editTexTelefone.getText().toString();

            telFormatado = "+" + MaskFormatUtil.unmask(telSemFormatar);

            progressDialog.setMessage("Verificando Número do Telefone...");
            progressDialog.show();

            if (TextUtils.isEmpty(telFormatado)) {
                Toast.makeText(this, nomeUsuario + " Por Favor, Digite o Telefone...", Toast.LENGTH_LONG).show();
            } else {
                validarCodigoMainActivity.startPhoneNumberVerification(telFormatado);
                Intent intent = new Intent(CadastrarTelefoneMainActivity.this, ValidarCodigoMainActivity.class);
                intent.putExtra("numeroTelefone", telFormatado);
                startActivity(intent);
                //finish();
            }
        });
    }

    //Inicio: Requerimento de Permissões
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
    }//Final: Requerimento de Permissões

}

//45min
//https://www.youtube.com/watch?v=F_UemS493IM&t=645s
//https://firebase.google.com/docs/auth/android/phone-auth?authuser=0

//Versão 3h
//https://youtu.be/F_UemS493IM?t=1548