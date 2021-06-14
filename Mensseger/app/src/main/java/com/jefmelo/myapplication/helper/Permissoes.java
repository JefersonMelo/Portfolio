package com.jefmelo.myapplication.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissoes {

    public Permissoes() {
    }

    public static boolean validarPermissoes(int requestCode, Activity activity, String[] permissoes) {

        if (Build.VERSION.SDK_INT >= 23) {
            List<String> listPermissoes = new ArrayList<String>();
            //Percorrer Permissões Para Liberação Pelo Usuário
            for (String permissao : permissoes) {
                Boolean validarPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!validarPermissao) {
                    listPermissoes.add(permissao);
                }
            }
            //Se Permissão já ceita, Não Solicitar
            if (listPermissoes.isEmpty()) {
                return true;
            }
            //Solicitar Permissão
            String[] arrayPermissoes = new String[listPermissoes.size()];
            ActivityCompat.requestPermissions(activity, listPermissoes.toArray(arrayPermissoes), requestCode);

        }
        return true;
    }
}
