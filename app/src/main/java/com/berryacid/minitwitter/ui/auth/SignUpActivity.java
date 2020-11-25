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
import com.berryacid.minitwitter.retrofit.request.RequestSignUp;
import com.berryacid.minitwitter.retrofit.response.ResponseAuth;
import com.berryacid.minitwitter.ui.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUser, etMail, etPass;
    Button btnSignUp;
    TextView tvGoLogin;
    MiniTwitterClient miniTwitterClient;
    MiniTwitterService miniTwitterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        retrofitIni();
        findViewById();
        events();

    }

    private void retrofitIni() {
        miniTwitterClient = MiniTwitterClient.getInstance();
        miniTwitterService = miniTwitterClient.getMiniTwitterService();
    }

    private void findViewById() {
        etUser = findViewById(R.id.editTextUserName);
        etMail = findViewById(R.id.editTextTextEmail);
        etPass = findViewById(R.id.editTextPassword);
        btnSignUp = findViewById(R.id.buttonSignUp);
        tvGoLogin = findViewById(R.id.textViewGoSignUp);
    }

    private void events() {
        btnSignUp.setOnClickListener(this);
        tvGoLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.buttonSignUp:
                goToSignUp();
                break;
            case R.id.textViewGoSignUp:
                goToLogin();
        }
    }

    private void goToSignUp() {
        String username = etUser.getText().toString();
        String email = etMail.getText().toString();
        String password = etPass.getText().toString();

        if(username.isEmpty()){
            etUser.setError("Se requiere un nombre de usuario.");
        } else if(email.isEmpty()){
            etMail.setError("Se requiere un e-mail.");
        } else if(password.isEmpty() || password.length() < 6){
            etPass.setError("Se requiere un password de minimo 6 caracteres.");
        } else {
            String code = "UDEMYANDROID";
            RequestSignUp requestSignUp = new RequestSignUp(username, email, password, code);
            Call<ResponseAuth> call = miniTwitterService.doSignUp(requestSignUp);
            
            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "", Toast.LENGTH_SHORT).show();

                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_TOKEN, response.body().getToken());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_USERNAME, response.body().getUsername());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_EMAIL, response.body().getEmail());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_PHOTOURL, response.body().getPhotoUrl());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_DATE_CREATED, response.body().getCreated());
                        SharedPreferencesManager.setSomeBooleanValue(Constantes.PREF_ACTIVE, response.body().getActive());

                        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(SignUpActivity.this, "Algo ha ido mal, revise sus datos o intente con otro usuario.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "Algo ha ido mal, revise su conexión.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goToLogin() {
        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}