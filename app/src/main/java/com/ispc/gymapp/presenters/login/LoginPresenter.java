package com.ispc.gymapp.presenters.login;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ispc.gymapp.R;
import com.ispc.gymapp.helper.Callback;
import com.ispc.gymapp.model.User;
import com.ispc.gymapp.views.activities.MainActivity;
import com.ispc.gymapp.views.activities.OnboardingActivityView;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executor;

public class LoginPresenter {


    private final String TAG = "LoginPresenter";
    private Context ctx;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public LoginPresenter(Context ctx,FirebaseAuth mAuth,FirebaseFirestore db) {
        this.ctx = ctx;
        this.mAuth = mAuth;
        this.db = db;


    }
    public void obtenerRolUsuario(RolUsuarioCallback callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            String roleName = user.getRole().getName();
                            callback.onRolUsuarioObtenido(roleName);
                        }
                    } else {
                        callback.onFalloObtenerRolUsuario(new Exception("No such document"));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFalloObtenerRolUsuario(e);
                }
            });
        } else {
            callback.onFalloObtenerRolUsuario(new Exception("No user is signed in."));
        }
    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());
                                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            User user = documentSnapshot.toObject(User.class);
                                            if (user != null) {
                                                String roleName = user.getRole().getName();
                                                if (roleName.equals("ADMIN")) {
                                                    // Ingresa como administrador
                                                    Log.d(TAG, "signInWithEmail: success as ADMIN");
                                                    Toast.makeText(ctx, "Ingresaste como ADMIN.", Toast.LENGTH_SHORT).show();
                                                    ctx.startActivity(new Intent(ctx, MainActivity.class));
                                                } else if (roleName.equals("USER")) {
                                                    // Ingresa como usuario normal
                                                    Log.d(TAG, "signInWithEmail: success as USER");
                                                    Toast.makeText(ctx, "Bienvenido USER.", Toast.LENGTH_SHORT).show();
                                                    ctx.startActivity(new Intent(ctx, MainActivity.class));
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "get failed with ", e);
                                    }
                                });
                            } else {
                                Log.d(TAG, "No user is signed in.");
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(ctx, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getCurrentUser(Callback<User> callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            DocumentReference usernameRef = db.collection("users").document(firebaseUser.getUid());
            usernameRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User loggedUser = documentSnapshot.toObject(User.class);
                        if (loggedUser != null) {
                            callback.onSuccess(loggedUser);
                        }
                    } else {
                        callback.onFailure(); // El usuario no existe en Firestore
                    }
                }
            });
        } else {
            callback.onFailure(); // No hay usuario autenticado
        }
    }

    public void updateUser(User user, HashMap<String,Object> inputData) {
        FirebaseUser firebaseUser= mAuth.getCurrentUser();
        if(user != null) {
            Double imc = user.calculateIMC((Integer) inputData.getOrDefault("height", 0), (Double) inputData.getOrDefault("weight", 0d));
            DocumentReference usernameRef = db.collection("users").document(firebaseUser.getUid());
            usernameRef.update("genre",inputData.getOrDefault("gender",""),
                    "weight",inputData.getOrDefault("weight",0d),
                    "height",inputData.getOrDefault("height",0),
                    "weightGoal",inputData.getOrDefault("goalWeight",0d),
                    "imc",imc
                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {


                    Toast.makeText(ctx,"Perfil actualizado",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ctx,"Hubo un error",Toast.LENGTH_SHORT).show();
                }
            });


        } else {
           Toast.makeText(ctx,"Hubo un error",Toast.LENGTH_SHORT).show();
        }
    }



    public interface RolUsuarioCallback {
        void onRolUsuarioObtenido(String roleName);
        void onFalloObtenerRolUsuario(Exception e);
    }


    public void signOut(){
        mAuth.signOut();
    }


}
