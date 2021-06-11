package com.jefmelo.myapplication.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.jefmelo.myapplication.R;
import com.jefmelo.myapplication.helper.Permissoes;
import com.jefmelo.myapplication.helper.Preferencias;

import java.util.HashMap;
import java.util.Random;

import com.jefmelo.myapplication.utils.MaskFormatUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText nome;
    private EditText telefone;
    private EditText cod_pais;
    private EditText cod_area;
    private Button botao_cadastrar;

    private final String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissoes.validarPermissoes(1, this, permissoesNecessarias);

        nome = (EditText) findViewById(R.id.editText_nome);
        telefone = (EditText) findViewById(R.id.editTex_telefone);
        cod_pais = (EditText) findViewById(R.id.editText_Cod_Pais);
        cod_area = (EditText) findViewById(R.id.editText_DDD);
        botao_cadastrar = (Button) findViewById(R.id.btn_cadastrar);

        //Definição das Máscaras
        telefone.addTextChangedListener(MaskFormatUtil.mask(telefone, MaskFormatUtil.FORMAT_FONE));
        cod_pais.addTextChangedListener(MaskFormatUtil.mask(cod_pais, MaskFormatUtil.FORMAT_COD_PAIS));
        cod_area.addTextChangedListener(MaskFormatUtil.mask(cod_area, MaskFormatUtil.FORMAT_COD_AREA));

        //Botão Gerador do Token: new View.OnClickListener()
        botao_cadastrar.setOnClickListener(v -> {
            String nomeUsuario = nome.getText().toString();
            String telSemFormatar = cod_pais.getText().toString()
                    + cod_area.getText().toString()
                    + telefone.getText().toString();

            String telFormatado = MaskFormatUtil.unmask(telSemFormatar);

            //Gerar Token, Verificar Geração de Token no Firebase

            Random random = new Random();
            int numRandom = random.nextInt(9999 - 1000) + 1000;
            String token = String.valueOf(numRandom);
            String msgEnvio = "Código de Confirmação: " + token;

            //Salvar dados para Validação
            Preferencias preferencias = new Preferencias(LoginActivity.this);
            preferencias.salvarPreferenciasUsuario(nomeUsuario, telSemFormatar, token);

            //Envio SMS
            envioSms("+" + telFormatado, msgEnvio);

            HashMap<String, String> usuario = preferencias.getDadosUsuario();

            Log.i("TOKEN", "T " + usuario.get("token"));


        });
    }

    //Envio do SMS
    private boolean envioSms(String tel, String msg) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(tel, null, msg, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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

/*https://firebase.google.com/docs/auth/admin/create-custom-tokens?hl=pt-br#android
 * https://firebase.google.com/docs/database/rest/auth?hl=pt-br#java
 * https://firebase.google.com/docs/auth/android/phone-auth?hl=pt-br
 * https://www.youtube.com/watch?v=QVecPoSG_ec
 * https://developer.android.com/reference/android/telephony/SmsManager?hl=pt-br
 * */