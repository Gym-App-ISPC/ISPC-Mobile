package com.ispc.gymapp.views.activities;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Plan;
import com.ispc.gymapp.views.adapter.CarritoAdapter;


import java.util.ArrayList;
import java.util.List;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCarrito;
    private CarritoAdapter carritoAdapter;
    private Button btnCheckout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        recyclerViewCarrito = findViewById(R.id.recyclerViewCarrito);

        btnBack = findViewById(R.id.btnBack);
        btnCheckout = findViewById(R.id.btnCheckout);

        recyclerViewCarrito.setLayoutManager(new LinearLayoutManager(this));
        List<Plan> planes = Carrito.getInstance().getPlanes();
        if (planes.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío.", Toast.LENGTH_SHORT).show();
            btnCheckout.setEnabled(false); // Desactiva el botón de checkout si el carrito está vacío
        } else {
            carritoAdapter = new CarritoAdapter(planes);
            recyclerViewCarrito.setAdapter(carritoAdapter);
            btnCheckout.setEnabled(true);
        }
        Carrito.getInstance().configurarBotonVaciarCarrito(findViewById(android.R.id.content));
        Carrito.getInstance().cargarCarritoDesdeFirestore(this::updateRecyclerView);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CarritoActivity.this, Ecommerce.class);
            startActivity(intent);
            finish();
        });


        btnCheckout.setOnClickListener(v -> {
            if (!planes.isEmpty()) {
                openPaymentDialog();
            } else {
                Toast.makeText(this, "No hay planes en el carrito para comprar.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateRecyclerView() {
        List<Plan> plans = Carrito.getInstance().getPlanes();
        if (carritoAdapter == null) {
            carritoAdapter = new CarritoAdapter(plans);
            recyclerViewCarrito.setAdapter(carritoAdapter);
        } else {
            carritoAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Carrito.getInstance().guardarCarritoEnFirestore();
    }

    private void openPaymentDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_checkout, null);

        TextInputEditText editTextCardName = dialogView.findViewById(R.id.editTextCardName);
        TextInputEditText editTextCardNumber = dialogView.findViewById(R.id.editTextCardNumber);

        TextInputEditText editTextExpiryMonth = dialogView.findViewById(R.id.editTextExpiryMonth);
        TextInputLayout expiryMonthLayout = dialogView.findViewById(R.id.expiryMonthLayout);
        editTextExpiryMonth.addTextChangedListener(new CustomTextWatcher(editTextExpiryMonth, expiryMonthLayout, CustomTextWatcher.ValidationType.EXPIRY_MONTH));

        TextInputEditText editTextExpiryYear = dialogView.findViewById(R.id.editTextExpiryYear);
        TextInputLayout expiryYearLayout = dialogView.findViewById(R.id.expiryYearLayout);
        editTextExpiryYear.addTextChangedListener(new CustomTextWatcher(editTextExpiryYear, expiryYearLayout, CustomTextWatcher.ValidationType.EXPIRY_YEAR));

        TextInputEditText editTextCVV = dialogView.findViewById(R.id.editTextCVV);
        TextInputLayout cvvLayout = dialogView.findViewById(R.id.cvvLayout);
        editTextCVV.addTextChangedListener(new CustomTextWatcher(editTextCVV, cvvLayout, CustomTextWatcher.ValidationType.CVV));

        Spinner spinnerInstallments = dialogView.findViewById(R.id.spinnerInstallments);
        editTextCardNumber.addTextChangedListener(new CreditCardTextWatcher(editTextCardNumber));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.installment_options, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstallments.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Datos de pago")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cardName = editTextCardName.getText().toString();
                        String cardNumber = editTextCardNumber.getText().toString();
                        String expiryMonth = editTextExpiryMonth.getText().toString();
                        String expiryYear = editTextExpiryYear.getText().toString();
                        String cvv = editTextCVV.getText().toString();
                        String installments = spinnerInstallments.getSelectedItem().toString();

                        if (validateInput(cardName, cardNumber, expiryMonth, expiryYear, cvv)) {
                            processPayment(cardName, cardNumber, expiryMonth, expiryYear, cvv, installments);
                        } else {
                            Toast.makeText(CarritoActivity.this, "Por favor, complete todos los campos correctamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    private boolean validateInput(String cardName, String cardNumber, String expiryMonth, String expiryYear, String cvv) {
        return !cardName.isEmpty() && !cardNumber.isEmpty() && !expiryMonth.isEmpty() && !expiryYear.isEmpty() && !cvv.isEmpty();
    }


    private void processPayment(String cardName, String cardNumber, String expiryMonth, String expiryYear, String cvv, String installments) {

        boolean paymentSuccess = true;

        if (paymentSuccess) {
            actualizarBaseDeDatosYConfirmar();
        } else {
            Toast.makeText(this, "El pago ha fallado. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show();
        }
    }
    private void actualizarBaseDeDatosYConfirmar() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DocumentReference userDocRef = db.collection("users").document(user.getUid());

            // Verificar si el usuario ya tiene un plan activo
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> historialCompras = (List<String>) documentSnapshot.get("historialCompras");
                    if (historialCompras != null && !historialCompras.isEmpty()) {
                        Toast.makeText(this, "Ya tienes un plan activo. No puedes comprar otro.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Continuar con la compra del plan
                        List<Plan> planes = Carrito.getInstance().getPlanes();
                        if (!planes.isEmpty()) {
                            Plan plan = planes.get(0);
                            String planDescription = plan.getDescripcion();

                            // Actualizar el historial de compras del usuario
                            userDocRef.update("historialCompras", FieldValue.arrayUnion(plan.getNombre()))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Compra registrada exitosamente.", Toast.LENGTH_SHORT).show();
                                        Carrito.getInstance().vaciarCarrito();
                                        showConfirmationPopup(plan.getNombre(), plan.getDescripcion());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error al actualizar la base de datos", e);
                                        Toast.makeText(this, "Error al registrar la compra. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(this, "No hay planes en el carrito", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // El documento del usuario no existe
                    Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error al verificar el plan del usuario", e);
                Toast.makeText(this, "Error al verificar el plan del usuario", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showConfirmationPopup(String planName, String planDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Has contratado!")
                .setMessage("¡Has contratado el plan: " + planName + "!")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CarritoActivity.this, PlanDetailsActivity.class);
                        intent.putExtra("planName", planName);
                        intent.putExtra("planDescription", planDescription);
                        startActivity(intent);
                        finish();


                    }
                })
                .setCancelable(false)
                .show();
    }







}


