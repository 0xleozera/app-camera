package com.example.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Seleciona o fab e víncula o evento de click que chama o método que
        // pede permissão do uso da camera e storage
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static String mCurrentPhotoPath;

    private void getPermissions() {
        // Se não tiver permissão ele envia o request
        // Senão só abre a camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            dispatchTakePictureIntent();
        }
    }

    // Request para uso da camera
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Você precisa permitir o uso da camera para o app", Toast.LENGTH_LONG).show();
                }

                return;
            }
        }
    }

    // Inicializa a camera
    // Salva e Busca a imagem no storage, e depois guarda a mesma nos atributos para depois setar no ImageView
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                photoFile = File.createTempFile("CameraApp", ".jpg", storageDir);
                mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
            }
            catch (IOException ex) {
                Toast.makeText(getApplicationContext(), "Erro ao tirar a foto: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Caso a foto tenha sido tirada, ele seleciona o ImageView e seta a imagem recém tirada
    // Senão apresenta um toast com aviso
    // É chamado automaticamente depois de tirar a foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                ImageView image = findViewById(R.id.image);
                Bitmap bm1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(mCurrentPhotoPath)));
                image.setImageBitmap(bm1);
            } catch (FileNotFoundException fnex) {
                Toast.makeText(getApplicationContext(), "Foto não encontrada!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Métodos que ja vem com a class
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
