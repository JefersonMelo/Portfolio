package com.jefmelo.myapplication.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferencias {

    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "app_preferecias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;
    private String CHAVE_NOME = "nome";
    private String CHAVE_TELEFONE = "telefone";
    private String TOKEN = "token";

    public Preferencias() {
    }

    public Preferencias(Context parametroContext) {

        context = parametroContext;
        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
    }

    public void salvarPreferenciasUsuario(String nomeUsuario, String telefone, String token) {

        editor.putString(CHAVE_NOME, nomeUsuario);
        editor.putString(CHAVE_TELEFONE, telefone);
        editor.putString(TOKEN, token);

        editor.commit();
    }

    public HashMap<String, String> getDadosUsuario(){

        HashMap<String, String> dadosUsuario = new HashMap<>();
        dadosUsuario.put(CHAVE_NOME, preferences.getString(CHAVE_NOME, null));
        dadosUsuario.put(CHAVE_TELEFONE, preferences.getString(CHAVE_TELEFONE, null));
        dadosUsuario.put(TOKEN, preferences.getString(TOKEN, null));

        return dadosUsuario;
    }
}
