package com.test.vetcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashBoard extends AppCompatActivity {
    private String userID;

    private Button btnSalir;
    private LocationManager locationManager;
    private String latitud="", longitud="";
    private FirebaseFirestore db;
    private TextView user1,user2,user3;
    private FirebaseAuth mAuth;
    ArrayList<Type> mArrayList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        btnSalir = findViewById(R.id.btnSalir);
        user1=findViewById(R.id.user1);
        user2=findViewById(R.id.user2);
        user3=findViewById(R.id.user3);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        btnSalir.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));

        });
        if (ContextCompat.checkSelfPermission(DashBoard.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(DashBoard.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DashBoard.this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitud=String.valueOf(location.getLatitude());
                longitud=String.valueOf(location.getLongitude());
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference documentReference = db.collection("users").document(userID);
                Map<String,Object> user=new HashMap<>();
                user.put("latitud", latitud);
                user.put("longitud", longitud);
                documentReference.update(user);

                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                CollectionReference userExercisesRef = rootRef.collection("users");
                Query query = userExercisesRef;
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            long count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                long longitudq = document.getLong("longitud");
                                long latitudq = document.getLong("latitud");
                                if (count==0){

                                    user1.setText("Longitud: "+String.valueOf(longitudq)+" Latitud:"+String.valueOf(latitudq));
                                }
                                if (count==1){
                                    user2.setText("Longitud: "+String.valueOf(longitudq)+" Latitud:"+String.valueOf(latitudq));
                                }
                                if (count==2){
                                    user3.setText("Longitud: "+String.valueOf(longitudq)+" Latitud:"+String.valueOf(latitudq));
                                }

                                count=count+1;
                            }

                           //   Log.d(TAG, String.valueOf(count));
                        }
                    }
                });

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        



    }//End onCreate

    /*private void getListItems() {
        db.collection("some collection").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<Type> types = documentSnapshots.toObjects(Type.class);


                            // Add all to your list
                            mArrayList.addAll(types);
                            Log.d(TAG, "onSuccess: " + mArrayList);
                        }                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                        }
                    });*/

}