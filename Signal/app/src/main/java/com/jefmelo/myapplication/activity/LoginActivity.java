package com.jefmelo.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.jefmelo.myapplication.R;
import com.jefmelo.myapplication.helper.Preferencias;

import java.util.HashMap;
import java.util.Random;

import Utils.MaskFormatUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText nome;
    private EditText telefone;
    private EditText cod_pais;
    private EditText cod_area;
    private Button botao_cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nome = (EditText) findViewById(R.id.editText_nome);
        telefone = (EditText) findViewById(R.id.editTex_telefone);
        cod_pais = (EditText) findViewById(R.id.editText_Cod_Pais);
        cod_area = (EditText) findViewById(R.id.editText_DDD);
        botao_cadastrar = (Button) findViewById(R.id.btn_cadastrar);

        //Definição das Máscaras
        telefone.addTextChangedListener(MaskFormatUtil.mask(telefone, MaskFormatUtil.FORMAT_FONE));
        cod_pais.addTextChangedListener(MaskFormatUtil.mask(cod_pais, MaskFormatUtil.FORMAT_COD_PAIS));
        cod_area.addTextChangedListener(MaskFormatUtil.mask(cod_area, MaskFormatUtil.FORMAT_COD_AREA));

        //Botão Gerador do Token
        botao_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeUsuario = nome.getText().toString();
                String telSemFormatar = cod_pais.getText().toString()
                                        + cod_area.getText().toString()
                                        + telefone.getText().toString();

                String telFormatado = MaskFormatUtil.unmask(telSemFormatar);

                //Gerar Token, Verificar Geração de Token no Firebase
                /*https://firebase.google.com/docs/auth/admin/create-custom-tokens?hl=pt-br#android
                * https://firebase.google.com/docs/database/rest/auth?hl=pt-br#java
                * https://firebase.google.com/docs/auth/android/phone-auth?hl=pt-br
                * https://www.youtube.com/watch?v=QVecPoSG_ec
                * */
                Random random = new Random();
                int numRandom = random.nextInt(9999 - 1000) + 1000;
                String token = String.valueOf(numRandom);

                //Salvar dados para Validação
                Preferencias preferencias = new Preferencias(LoginActivity.this);
                preferencias.salvarPreferenciasUsuario(nomeUsuario, telSemFormatar, token);

                HashMap<String, String> usuario = preferencias.getDadosUsuario();

                Log.i("TOKEN", "T " + usuario.get("token"));
            }
        });
    }
}