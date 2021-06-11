package com.jefmelo.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jefmelo.myapplication.R;
import com.jefmelo.myapplication.helper.Preferencias;

import java.util.HashMap;

public class ValidadorActivity extends AppCompatActivity {

    private EditText codValidacao;
    private Button btnValidar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codValidacao = findViewById(R.id.editText_sms_validador);
        btnValidar = findViewById(R.id.btn_validador);

        btnValidar.setOnClickListener(v -> {
            //Recuoerar Dados Preferncias do Usuário
            Preferencias preferencias = new Preferencias(ValidadorActivity.this);
            HashMap<String, String> usuario = preferencias.getDadosUsuario();

            String tokenGerado = usuario.get("token");
            String tokenDigitado = codValidacao.getText().toString();

            if (tokenDigitado.equals(tokenGerado)){
                Toast.makeText(ValidadorActivity.this, "Token Validado!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ValidadorActivity.this, "Token Errado!", Toast.LENGTH_LONG).show();
            }

        });
    }
}