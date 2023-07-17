package com.test.vetcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registerpaciente extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText institucion,medicotrat,telmedico,direccion;
    private Button termregistro;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerpaciente);

        institucion=findViewById(R.id.instsalud);
        medicotrat=findViewById(R.id.medico);
        telmedico=findViewById(R.id.telmed);
        direccion=findViewById(R.id.direccion);
        termregistro=findViewById(R.id.btnRegister2);
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        termregistro.setOnClickListener(view -> {
            updateuser();
        });
    }

    private void updateuser() {

        String inst = institucion.getText().toString();
        String med = medicotrat.getText().toString();
        String telmed = telmedico.getText().toString();
        String direcc = direccion.getText().toString();

        if (TextUtils.isEmpty(inst)){
            institucion.setError("Ingrese la institucion");
            institucion.requestFocus();
        }else if (TextUtils.isEmpty(med)){
            medicotrat.setError("Ingrese el medico tratante");
            medicotrat.requestFocus();
        }else if (TextUtils.isEmpty(telmed)){
            telmedico.setError("Ingrese el telefono del medico");
            telmedico.requestFocus();
        }else if (TextUtils.isEmpty(direcc)){
            direccion.setError("Ingrese su direccion");
            direccion.requestFocus();
        }else {
            String userID = mAuth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("users").document(userID);

            Map<String,Object> user=new HashMap<>();
            user.put("INSTITUCION", inst);
            user.put("MEDICO TRATANTE", med);
            user.put("TELEFONO MEDICO", telmed);
            user.put("DIRECCION", direcc);



                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "onSuccess: Datos registrados"+userID);
                            }
                        });
                        Toast.makeText(registerpaciente.this, "Usuario paciente Registrado", Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(registerpaciente.this, LoginActivity.class));


                }


        }
    }

