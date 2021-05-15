package com.yogdroidtech.shopadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yogdroidtech.shopadmin.model.Banner;
import com.yogdroidtech.shopadmin.model.Products;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductUploadActivity extends AppCompatActivity implements uploadListener {
    private FirebaseAuth mAuth;
    private static int RC_SIGN_IN= 123;
    private TextView userName;
    private Button signOut;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private GridLayoutManager gridLayoutManager;
    private PlaceHolderAdapter placeHolderAdapter;
    private  int placePosition;
    List<Uri> filePathLists = new ArrayList<>();
    List<String> uploadFileUrlList = new ArrayList<>();

    private List<Products> productsList = new ArrayList<>();
    private List<Banner> bannerList = new ArrayList<>();
    private List<Bitmap> bitmapList = new ArrayList<>();

    @BindView(R.id.button6)
    Button choose;

    @BindView(R.id.button7)
    Button upload;

    @BindView(R.id.rvThumbnail)
    RecyclerView rvThumbnail;

    @BindView(R.id.saveProduct)
    Button saveProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_upload);

        mAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);

        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Bitmap icon = getBitmaps(this,R.drawable.add);
//        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.add);
//
        bitmapList.add(icon);
        bitmapList.add(icon);
        bitmapList.add(icon);
        bitmapList.add(icon);
        bitmapList.add(icon);
        gridLayoutManager = new GridLayoutManager(this, 4);
        placeHolderAdapter = new PlaceHolderAdapter(bitmapList, this::onclick);
        rvThumbnail.setLayoutManager(gridLayoutManager);
        rvThumbnail.setAdapter(placeHolderAdapter);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });
        if(currentUser!=null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Create a new user with a first and last name
//            db.collection("products")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d("TAG", document.getId() + " => " + document.getData());
//                                    Banner banner = document.toObject(Banner.class);
//                                    bannerList.add(banner);
//                                }
//                                Glide.with(ProductUploadActivity.this).load(bannerList.get(0).getImgUrl()).into(imageView);
//
////                                storageReference = storage.getReferenceFromUrl(productsList.get(0).getImgUrl());
////                                StorageReference sr = storageReference.child("images/"+bannerList.get(0).getImgUrl());
////
////                                sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
////                                    @Override
////                                    public void onComplete(@NonNull Task<Uri> task) {
////                                        Log.i("to", task.toString());
////                                    }
////                                });
//                            } else {
//                                Log.d("TAG", "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
        }
        else {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    private void selectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    private void uploadImage()
    {
        if (filePath!=null) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String imageName = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("products/" + imageName);

               ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       String path = task.getResult().toString();
                       Toast.makeText(ProductUploadActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();

                       ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                           @Override
                           public void onComplete(@NonNull Task<Uri> task) {
                               progressDialog.dismiss();
                               uploadFileUrlList.add(task.getResult().toString());
                               Log.i("yog", uploadFileUrlList.toString());
                               Toast.makeText(ProductUploadActivity.this, "Image URL Downloaded!!", Toast.LENGTH_SHORT).show();

                           }
                       });
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       progressDialog.dismiss();
                       Toast.makeText(ProductUploadActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                   @Override
                                   public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                       double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                   }
                               });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i("yogesh", user.getDisplayName());
                userName.setText(user.getDisplayName());
                // ...
            } else {
                Log.i("yogesh", "failed");
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
//                uploadImage();
                filePathLists.add(filePath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bitmapList.set(placePosition, bitmap);
                placeHolderAdapter.setBitmapList(bitmapList);
                placeHolderAdapter.notifyDataSetChanged();
//                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onclick(int position) {
        placePosition = position;
        selectImage();
    }

    private void saveProduct(){

        String[] arr = new String[uploadFileUrlList.size()];
        for (int i =0; i < uploadFileUrlList.size(); i++){
            arr[i] = uploadFileUrlList.get(i);
        }

        Map<String, Object> docData = new HashMap<>();
        docData.put("imgUrl",Arrays.asList(arr));
        docData.put("id", 123);
        docData.put("productName", "Car");
        docData.put("category", "Car");
        docData.put("subCategory", "Car");
        docData.put("markPrice", 44);
        docData.put("sellPrice", 33);
        docData.put("isWishList", false);
        docData.put("unit", "KG");

        Products product = new Products("","","",88,9,9,true,"kg",uploadFileUrlList);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").document().set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProductUploadActivity.this, "Image Saved in Database!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductUploadActivity.this, "Image Saved in Database!!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
    private static Bitmap getBitmaps(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
}