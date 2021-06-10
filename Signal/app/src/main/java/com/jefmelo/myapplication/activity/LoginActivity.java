package com.jefmelo.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.jefmelo.myapplication.R;
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

        telefone.addTextChangedListener(MaskFormatUtil.mask(telefone, MaskFormatUtil.FORMAT_FONE));
        cod_pais.addTextChangedListener(MaskFormatUtil.mask(cod_pais, MaskFormatUtil.FORMAT_COD_PAIS));
        cod_area.addTextChangedListener(MaskFormatUtil.mask(cod_area, MaskFormatUtil.FORMAT_COD_AREA));
    }
}