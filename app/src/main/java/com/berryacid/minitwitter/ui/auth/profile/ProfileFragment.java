package com.berryacid.minitwitter.ui.auth.profile;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.berryacid.minitwitter.R;
import com.berryacid.minitwitter.common.Constantes;
import com.berryacid.minitwitter.data.ProfileViewModel;
import com.berryacid.minitwitter.retrofit.request.RequestUserProfile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    ImageView ivAvatarProfile;
    EditText etMail, etUser, etPass, etWebsite, etDescription;
    Button btnSave, btnChangePass;
    boolean loadingData = true;
    PermissionListener allPermissionListener;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = new ViewModelProvider(getActivity()).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        ivAvatarProfile = v.findViewById(R.id.imageViewAvatarProfile);
        etMail = v.findViewById(R.id.editTextTextEmailProfile);
        etUser = v.findViewById(R.id.editTextUserNameProfile);
        etPass = v.findViewById(R.id.editTextPasswordProfile);
        etWebsite = v.findViewById(R.id.editTextWebsiteProfile);
        etDescription = v.findViewById(R.id.editTextDescriptionProfile);
        btnSave = v.findViewById(R.id.button_save_changes);
        btnChangePass = v.findViewById(R.id.button_change_pass);

        //Eventos
        btnSave.setOnClickListener(view ->{
            String username = etUser.getText().toString();
            String email = etMail.getText().toString();
            String descripcion = etDescription.getText().toString();
            String website = etWebsite.getText().toString();
            String password = etPass.getText().toString();

            if (username.isEmpty()){
                etUser.setError("Se requiere un nombre de usuario.");
            } else if (email.isEmpty()){
                etMail.setError("Ingresa tu e-mail.");
            } else if (password.isEmpty()){
                etPass.setError("Ingresa tu contraseña.");
            } else {
                RequestUserProfile requestUserProfile = new RequestUserProfile(username, email, descripcion, website, password);
                profileViewModel.updateProfile(requestUserProfile);
                Toast.makeText(getActivity(), "Enviando información al servidor.", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(false);
            }
        });

        btnChangePass.setOnClickListener(view ->{
            Toast.makeText(getActivity(), "Click on change pass", Toast.LENGTH_SHORT).show();
        });

        ivAvatarProfile.setOnClickListener(view ->{
            //Invocar a la selección de la fotografia.
            //Invocar al método de comprobación de permisos
            checkPermission();
        });

        //ViewModel
        profileViewModel.userProfile.observe(getActivity(), responseUserProfile -> {
            loadingData = false;
            etUser.setText(responseUserProfile.getUsername());
            etMail.setText(responseUserProfile.getEmail());
            etWebsite.setText(responseUserProfile.getWebsite());
            etDescription.setText(responseUserProfile.getDescripcion());

            if (!loadingData){
                btnSave.setEnabled(true);
                Toast.makeText(getActivity(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        profileViewModel.photoProfile.observe(getActivity(), photo -> {

                Glide.with(getActivity())
                        .load(Constantes.API_MINITWITTER_FILES_URL + photo)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .into(ivAvatarProfile);

        });

        return  v;
    }

    private void checkPermission() {
        PermissionListener dialogOnDeniedPermissionListener =
                DialogOnDeniedPermissionListener.Builder.withContext(getActivity())
                .withTitle("Los permisos solicitados son necesarios para poder seleccionar una foto de perfil")
                .withButtonText("Aceptar")
                .withIcon(R.mipmap.ic_launcher)
                .build();

        allPermissionListener = new CompositePermissionListener(
                (PermissionListener) getActivity(),
                dialogOnDeniedPermissionListener
        );
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(allPermissionListener)
                .check();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}