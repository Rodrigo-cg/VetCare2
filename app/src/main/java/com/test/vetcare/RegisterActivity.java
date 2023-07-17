package com.test.vetcare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {


    private ImageView photo_register;
    private Button add_foto;
    private Button creaactu_img,r_imag;
    private EditText txtUser;
    private EditText txtMail;
    private EditText txtPhone;
    private TextInputLayout txtPassword;
    private Button btnRegister;
    private TextView lblLogin;
    private RadioGroup paciente_voluntario;
    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText DNI;
    StorageReference storageReference;
    private CircleImageView profileImageView;
    String storegapath="perfilimg/*";
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private StorageTask uploadtask;
    private StorageReference storageprofileref;
    private Uri image_url;
    String photo = "photo";
    String idd;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        profileImageView=findViewById(R.id.pet_photo);
        txtUser = findViewById(R.id.txtUser);
        txtMail = findViewById(R.id.txtMail);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        lblLogin = findViewById(R.id.lblLogin);
        btnRegister = findViewById(R.id.btnRegister);
        creaactu_img=findViewById(R.id.btn_photo);
        r_imag=findViewById(R.id.btn_remove_photo);
        paciente_voluntario=(RadioGroup) findViewById(R.id.pac_vol_rg);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        DNI=findViewById(R.id.txtDNI);
        btnRegister.setOnClickListener(view -> {
            createuser();
        });


        lblLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                boolean pick=true;
                    if(pick==true){
                        if(!checkCameraPermission()){
                            requestCameraPermission();
                        }else PickImage();

                    }else{
                        if(!checkStoragePermission()){
                            requestStoragePermission();
                        }else PickImage();

                    }
                       ;
            }
        });

    }//End onCreate

    private void PickImage() {
        CropImage.activity().start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    private boolean checkStoragePermission() {
        boolean res2= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return  res1 && res2;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image_url = result.getUri();
                Picasso.get().load(image_url).into(profileImageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
   /* private void uploadprofileimg(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Carga tu foto de perfil");
        progressDialog.setMessage("Cargando la imagen");
        progressDialog.show();
        if (image_url!=null){
            final StorageReference fileRef=storageprofileref.child()
        }
    }*/


    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }// End openLoginActivity
    private void subirPhoto(Uri image_url,String userID) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        String rute_storage_photo = storegapath + "" + photo + "" + mAuth.getUid() ;

        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("photo", download_uri);
                            db.collection("users").document(userID).update(map);
                            Toast.makeText(RegisterActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void createuser(){
        String pac_volunt="paciente";




        if (paciente_voluntario.getCheckedRadioButtonId() == R.id.pacienteRB) {
            pac_volunt="paciente";

        }else{
            pac_volunt="voluntario";
        }
        String name = txtUser.getText().toString();
        String dnii = DNI.getText().toString();
        String mail = txtMail.getText().toString();
        String phone = txtPhone.getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        if (TextUtils.isEmpty(name)){
            txtMail.setError("Ingrese un Nombre");
            txtMail.requestFocus();
        }else if (TextUtils.isEmpty(mail)){
            txtMail.setError("Ingrese un Correo");
            txtMail.requestFocus();
        }else if (TextUtils.isEmpty(phone)){
            txtMail.setError("Ingrese un Teléfono");
            txtMail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            txtMail.setError("Ingrese una Contraseña");
            txtMail.requestFocus();
        }else {

            String finalPac_volunt = pac_volunt;
            mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("users").document(userID);

                        Map<String,Object> user=new HashMap<>();
                        user.put("Nombre", name);
                        user.put("DNI", dnii);
                        user.put("Correo", mail);
                        user.put("Teléfono", phone);
                        user.put("Contraseña", password);
                        user.put("Condicion", finalPac_volunt);
                        user.put("latitud", "");
                        user.put("longitud", "");

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "onSuccess: Datos registrados"+userID);
                            }
                        });
                        Toast.makeText(RegisterActivity.this, "Usuario Registrado", Toast.LENGTH_SHORT).show();
                        subirPhoto(image_url,userID);
                        if(finalPac_volunt.equals("paciente")) {
                            mAuth.signInWithEmailAndPassword(mail, password);

                            startActivity(new Intent(RegisterActivity.this, registerpaciente.class));
                        }else
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this, "Usuario no registrado"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

}// End RegisterActivity