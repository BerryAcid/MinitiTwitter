package com.berryacid.minitwitter.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.berryacid.minitwitter.R;
import com.berryacid.minitwitter.common.Constantes;
import com.berryacid.minitwitter.common.SharedPreferencesManager;
import com.berryacid.minitwitter.retrofit.MiniTwitterClient;
import com.berryacid.minitwitter.retrofit.MiniTwitterService;
import com.berryacid.minitwitter.retrofit.request.RequestLogin;
import com.berryacid.minitwitter.retrofit.response.ResponseAuth;
import com.berryacid.minitwitter.ui.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    TextView tvGoSignUp;
    EditText etMail, etPass;
    MiniTwitterClient miniTwitterClient;
    MiniTwitterService miniTwitterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se elimina la barra de aciones superior.
        getSupportActionBar().hide();

        retrofitInit();
        findViewById();
        events();
    }

    private void retrofitInit() {
        miniTwitterClient = MiniTwitterClient.getInstance();
        miniTwitterService = miniTwitterClient.getMiniTwitterService();
    }

    private void findViewById() {
        btnLogin = findViewById(R.id.buttonLogin);
        tvGoSignUp = findViewById(R.id.textViewGoSignUp);
        etMail = findViewById(R.id.editTextTextEmail);
        etPass = findViewById(R.id.editTextPassword);
    }
    private void events() {
        btnLogin.setOnClickListener(this);
        tvGoSignUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        switch (id) {
            case R.id.textViewGoSignUp:
                goToSignUp();
                break;
            case R.id.buttonSignUp:
                break;
            case R.id.buttonLogin:
                goToLogin();
                break;
        }
    }

    private void goToLogin() {
        String email = etMail.getText().toString();
        String password = etPass.getText().toString();

        if(email.isEmpty()){
            etMail.setError("El email es requerido");
        } else if(password.isEmpty()){
            etPass.setError("La contraseña es requerida.");
        } else {
            RequestLogin requestLogin = new RequestLogin(email, password);

            Call<ResponseAuth> call = miniTwitterService.doLogin(requestLogin);

            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show();

                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_TOKEN, response.body().getToken());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_USERNAME, response.body().getUsername());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_EMAIL, response.body().getEmail());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_PHOTOURL, response.body().getPhotoUrl());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_DATE_CREATED, response.body().getCreated());
                        SharedPreferencesManager.setSomeBooleanValue(Constantes.PREF_ACTIVE, response.body().getActive());

                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(MainActivity.this, "Algo fue mal revise sus datos.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Problemas de conexión, intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void goToSignUp() {
        Intent i = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(i);
    }
}