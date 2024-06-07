package com.ispc.gymapp.views.activities;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Role;
import com.ispc.gymapp.model.User;
import com.ispc.gymapp.presenters.login.LoginPresenter;
import com.ispc.gymapp.presenters.register.RegisterPresenter;
import com.ispc.gymapp.views.adapter.OnboardingStateAdapter;
import com.ispc.gymapp.views.viewmodel.RegisterViewModel;

import java.time.LocalDate;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private RegisterPresenter registerPresenter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private Button btnRegister;
    private TextView btnToLogin;
    private AlertDialog.Builder dialog;
    private EditText registerMail;
    private EditText registerPassword;
    private EditText confirmPassword;
    private Switch passwordVisibilitySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(R.layout.layout_loading_dialog);
        AlertDialog alertDialog = dialog.create();

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btnToLogin = findViewById(R.id.btnToLogin);
        btnToLogin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        registerMail = findViewById(R.id.registerUsername);
        registerPassword = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerPresenter = new RegisterPresenter(this, db, mAuth);

        passwordVisibilitySwitch = findViewById(R.id.passwordVisibilitySwitch);


        // TextWatcher para validar el correo en tiempo real
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable == registerMail.getEditableText()) {
                    // Validar la dirección de correo electrónico en tiempo real
                    String email = editable.toString().trim();
                    if (!isValidEmail(email)) {
                        registerMail.setError("Correo electrónico no válido");
                    } else {
                        // La dirección de correo electrónico es válida
                        registerMail.setError(null);
                    }
                } else if (editable == confirmPassword.getEditableText()) {
                    String password = editable.toString().trim();
                    String password2 = registerPassword.getText().toString().trim();
                    if (!password2.equals(password)) {
                        confirmPassword.setError("Las contraseñas no coinciden");
                    } else {
                        confirmPassword.setError(null);
                    }
                }
            }
        };

        registerMail.addTextChangedListener(textWatcher);
        confirmPassword.addTextChangedListener(textWatcher);
        switchPassword();

    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnRegister){
            String mail = registerMail.getText().toString().trim();
            String password = registerPassword.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPass)) {
                confirmPassword.setError("Las contraseñas no coinciden");
            } else if (password.length() < 6) { // Comprobación de longitud mínima de la contraseña
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            } else {
                // Las contraseñas coinciden, los campos no están vacíos y la contraseña es lo suficientemente larga, continuar con el registro
                Role role = new Role("USER");
                User user = new User(mail, password, role);
                registerPresenter.registerUser(user);
                dialog.show();
            }
        }

        if(view.getId() == R.id.btnToLogin){
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        }



    }

    public void switchPassword() {


        this.passwordVisibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final EditText registerPassword = findViewById(R.id.registerPassword);
                final EditText confirmPassword = findViewById(R.id.confirmPassword);
                // Alternar la visibilidad de la contraseña en ambos campos
                int inputType = isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                registerPassword.setInputType(inputType);
                confirmPassword.setInputType(inputType);
                }

        });
    }
}