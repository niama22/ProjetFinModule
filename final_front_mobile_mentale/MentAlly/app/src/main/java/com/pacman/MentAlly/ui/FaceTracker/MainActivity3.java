package com.pacman.MentAlly.ui.FaceTracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.common.util.concurrent.ListenableFuture;
import com.pacman.MentAlly.R;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity3 extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE = 1;
    private Long userId;
    private SharedPreferences prefs;
    private FeelingApiService apiService;
    private static final Long USER_ID = 1L;
    private ImageView selectedImageView;
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private TextView textResult;
    private ConstraintLayout cameraLayout;
    private Button captureButton, galleryButton, switchModeButton;
    private Interpreter tflite;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private boolean isCameraMode = false;

    private String[] classNames = {"Colère", "Dégoût", "Peur", "Joie", "Tristesse", "Surprise", "Neutre"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        prefs = getSharedPreferences("MentAllyPrefs", Context.MODE_PRIVATE);  userId = getCurrentUserId();
        if (userId == -1L) {
            Toast.makeText(this, "Veuillez vous connecter d'abord", Toast.LENGTH_LONG).show();
            // You might want to redirect to login activity here
            finish();
            return;
        }
        initializeViews();
        setupListeners();
        initializeRetrofit();

        loadTFLiteModel();

        // Définir l'état initial
        updateUIForGalleryMode();
    }

    private void initializeViews() {
        viewFinder = findViewById(R.id.viewFinder);
        textResult = findViewById(R.id.text_result);
        cameraLayout = findViewById(R.id.camera_layout);
        captureButton = findViewById(R.id.capture_button);
        galleryButton = findViewById(R.id.gallery_button);
        switchModeButton = findViewById(R.id.switch_mode_button);
        selectedImageView = findViewById(R.id.selected_image_view);
    }

    private void setupListeners() {
        captureButton.setOnClickListener(v -> takePhoto());
        galleryButton.setOnClickListener(v -> openGallery());
        switchModeButton.setOnClickListener(v -> toggleMode());
    }

    private void updateUIForCameraMode() {
        cameraLayout.setVisibility(View.VISIBLE);
        textResult.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.VISIBLE);

        selectedImageView.setVisibility(View.GONE);
        galleryButton.setVisibility(View.GONE);

        switchModeButton.setText("Mode Galerie");
        textResult.setText("");
    }

    private void updateUIForGalleryMode() {
        cameraLayout.setVisibility(View.GONE);
        captureButton.setVisibility(View.GONE);

        textResult.setVisibility(View.VISIBLE);
        selectedImageView.setVisibility(View.VISIBLE);
        galleryButton.setVisibility(View.VISIBLE);

        switchModeButton.setText("Mode Caméra");
    }

    private void loadTFLiteModel() {
        try {
            tflite = new Interpreter(loadModelFile("emotion_model.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de chargement du modèle", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur démarrage caméra", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Bitmap bitmap = imageProxyToBitmap(image);
                if (bitmap != null) {
                    runOnUiThread(() -> {
                        String result = classifyImage(bitmap);
                        textResult.setText(result);
                    });
                }
                image.close();
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                runOnUiThread(() -> Toast.makeText(MainActivity3.this,
                        "Erreur capture: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }
        });
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        Image.Plane[] planes = image.getImage().getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(image.getImageInfo().getRotationDegrees());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                String result = classifyImage(bitmap);
                textResult.setText(result);

                selectedImageView.setImageBitmap(bitmap);
                selectedImageView.setVisibility(View.VISIBLE);
                updateUIForGalleryMode();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors du chargement de l'image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleMode() {
        isCameraMode = !isCameraMode;
        if (isCameraMode) {
            if (allPermissionsGranted()) {
                startCamera();
                updateUIForCameraMode();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            updateUIForGalleryMode();
        }
    }

    private MappedByteBuffer loadModelFile(String modelPath) throws IOException {
        FileInputStream inputStream = new FileInputStream(getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = getAssets().openFd(modelPath).getStartOffset();
        long declaredLength = getAssets().openFd(modelPath).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



    private ByteBuffer preprocessImage(Bitmap bitmap) {
        Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, 48, 48, true);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(48 * 48 * 1 * 4);
        byteBuffer.order(java.nio.ByteOrder.nativeOrder());

        for (int i = 0; i < 48; i++) {
            for (int j = 0; j < 48; j++) {
                int pixelValue = resizedImage.getPixel(j, i);
                int grayValue = (int) (0.299 * ((pixelValue >> 16) & 0xFF) +
                        0.587 * ((pixelValue >> 8) & 0xFF) +
                        0.114 * (pixelValue & 0xFF));
                byteBuffer.putFloat(grayValue / 255.0f);
            }
        }
        return byteBuffer;
    }

    private int argMax(float[] output) {
        int maxIndex = 0;
        for (int i = 1; i < output.length; i++) {
            if (output[i] > output[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                startCamera();
                updateUIForCameraMode();
            } else {
                Toast.makeText(this, "Permission caméra requise",
                        Toast.LENGTH_SHORT).show();
                isCameraMode = false;
                updateUIForGalleryMode();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
    }
    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8083/") // Replace with your actual backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(FeelingApiService.class);
    }
    private Long getCurrentUserId() {
        long userId = prefs.getLong("userId", -1L);
        Log.d("MainActivity3", "Getting userId from SharedPreferences: " + userId);
        return userId;
    }
    private void saveEmotion(String detectedEmotion) {
        // Use the instance userId instead of static USER_ID
        Feeling feeling = new Feeling(userId, detectedEmotion);

        apiService.createFeeling(feeling).enqueue(new Callback<Feeling>() {
            @Override
            public void onResponse(Call<Feeling> call, Response<Feeling> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(MainActivity3.this,
                            "Émotion enregistrée avec succès", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity3.this,
                            "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<Feeling> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(MainActivity3.this,
                        "Erreur de connexion", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Modify the classifyImage method to save the emotion
    private String classifyImage(Bitmap bitmap) {
        ByteBuffer inputBuffer = preprocessImage(bitmap);
        float[][] output = new float[1][7];
        tflite.run(inputBuffer, output);
        int predictedClass = argMax(output[0]);
        String detectedEmotion = classNames[predictedClass];

        // Save the detected emotion
        saveEmotion(detectedEmotion);

        return "Émotion détectée : " + detectedEmotion;
    }
}
