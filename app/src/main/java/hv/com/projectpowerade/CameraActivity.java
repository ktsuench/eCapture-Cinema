package hv.com.projectpowerade;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyBGkYyoMJtfhh-gdGuPnoA56AasJjms3LM";

    private static final String TAG = "MainActivity";
    private Button takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 270);
        ORIENTATIONS.append(Surface.ROTATION_90, 270);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    ArrayList<Integer> reacts = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<Genre> categories = new ArrayList<>();

    ImageView poster1;
    TextView movtitle;
    Button next;
    static int i = 0;

    final int MAX = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poster_page);
        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (Button) findViewById(R.id.buttonnext);
        takePictureButton.setVisibility(View.VISIBLE);
        assert takePictureButton != null;

        for(int j = 0; j < MAX; j++) {
            reacts.add(0);
        }

        //place movie titles here yo
        titles.add(getString(R.string.movietitle1));
        titles.add(getString(R.string.movietitle2));
        titles.add(getString(R.string.movietitle3));
        titles.add(getString(R.string.movietitle4));
        titles.add(getString(R.string.movietitle5));
        titles.add(getString(R.string.movietitle6));
        titles.add(getString(R.string.movietitle7));
        titles.add(getString(R.string.movietitle8));
        titles.add(getString(R.string.movietitle9));
        titles.add(getString(R.string.movietitle10));
        titles.add(getString(R.string.movietitle11));
        titles.add(getString(R.string.movietitle12));
        titles.add(getString(R.string.movietitle13));
        titles.add(getString(R.string.movietitle14));
        titles.add(getString(R.string.movietitle15));
        titles.add(getString(R.string.movietitle16));
        titles.add(getString(R.string.movietitle17));
        titles.add(getString(R.string.movietitle18));
        titles.add(getString(R.string.movietitle19));
        titles.add(getString(R.string.movietitle20));
        titles.add(getString(R.string.movietitle21));

        categories.add(new Genre("action-comedy", 28, R.drawable.actioncomedy));
        categories.add(new Genre("action-crime", 28, R.drawable.actioncrime));
        categories.add(new Genre("action-thriller", 28, R.drawable.actionthriller));
        categories.add(new Genre("comedy-romance", 35, R.drawable.comedyromance));
        categories.add(new Genre("comedy-sick", 35, R.drawable.comedysick));
        categories.add(new Genre("comedy-sports", 35, R.drawable.comedysports));
        categories.add(new Genre("documentary-biography", 99, R.drawable.documentarybiography));
        categories.add(new Genre("documentary-comedy", 99, R.drawable.documentarycomedy));
        categories.add(new Genre("documentary-history", 99, R.drawable.documentaryhistory));
        categories.add(new Genre("drama-musical", 18, R.drawable.dramamusical));
        categories.add(new Genre("drama-romance", 18, R.drawable.dramaromance));
        categories.add(new Genre("drama-war", 18, R.drawable.dramawar));
        categories.add(new Genre("family-adventure", 10751, R.drawable.familyadventure));
        categories.add(new Genre("family-comedy", 10751, R.drawable.familycomedy));
        categories.add(new Genre("family-fantasy", 10751, R.drawable.familyfantasy));
        categories.add(new Genre("scifi-animation", 878, R.drawable.scifianimation));
        categories.add(new Genre("scifi-family", 878, R.drawable.scififamily));
        categories.add(new Genre("scifi-horror", 878, R.drawable.scifihorror));
        categories.add(new Genre("thriller-comedy", 53, R.drawable.thrillercomedy));
        categories.add(new Genre("thriller-crime", 53, R.drawable.thrillercrime));
        categories.add(new Genre("thriller-mystery", 53, R.drawable.thrillermystery));

        poster1 = (ImageView)findViewById(R.id.poster);
        movtitle = (TextView)findViewById(R.id.movietitle);

        poster1.setImageResource(R.drawable.actioncomedy);
        movtitle.setText(getString(R.string.movietitle1));

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i<MAX) {
                    takePicture();
                    takePictureButton.setVisibility(View.INVISIBLE);
                }
                else{
                    Intent gotoResults = new Intent(CameraActivity.this, ResultsPage.class);
                    startActivity(gotoResults);
                }
            }
        });
    }
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            createCameraPreview();
        }
    };
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void takePicture() {
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory() + getString(R.string.directorypath) + getString(R.string.filename));
            Log.i(TAG, file.getAbsolutePath());
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();

                    Toast.makeText(CameraActivity.this, "Please wait as this processes.", Toast.LENGTH_SHORT).show();
                    uploadImage(Uri.fromFile(getCameraFile()));
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[1];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }
    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(CameraActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(getString(R.string.directorypath));
        return new File(dir, getString(R.string.filename));
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Google Cloud Vision Failed due to faulty image data", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "Google Cloud Vision Failed due to no image data provided", Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        com.google.api.services.vision.v1.model.Image base64EncodedImage = new com.google.api.services.vision.v1.model.Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature faceDetection = new Feature();
                            faceDetection.setType("FACE_DETECTION");
                            faceDetection.setMaxResults(10);
                            add(faceDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return getFaceAnnotations(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                // Enable button to go to next poster
                i++;
                takePictureButton.setVisibility(View.VISIBLE);

                // Development purposes only
                Toast.makeText(CameraActivity.this, "You may continue now.", Toast.LENGTH_SHORT).show();

                poster1.setImageResource(categories.get(i).getImgfile());
                movtitle.setText(titles.get(i));

                ArrayList<Genre> genres = new ArrayList<>();
                if (i == MAX){
                    // This is not logically correct (due to the categories arraylist order)
                    for (int j = MAX - 1; j > 0; j--) {
                        if (reacts.get(j) > 0) {
                            if (j == 0) {
                                genres.add(new Genre("action", 28, 0));
                            } else if (j == 1) {
                                genres.add(new Genre("comedy", 35, 0));
                            } else if (j == 2) {
                                genres.add(new Genre("documentary", 99, 0));
                            } else if (j == 3) {
                                genres.add(new Genre("drama", 18, 0));
                            } else if (j == 4) {
                                genres.add(new Genre("family", 10751, 0));
                            } else if (j == 5) {
                                genres.add(new Genre("scifi", 878, 0));
                            } else if (j == 6) {
                                genres.add(new Genre("thriller", 53, 0));
                            }
                        }
                    }
                    JSONUtil jutil = new JSONUtil();
                    String jsonString = jutil.toJson(genres);
                    Log.i("JSON Result", jsonString);

                    try {
                        jutil.writeToFile(genres);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String getFaceAnnotations(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<FaceAnnotation> faces = response.getResponses().get(0).getFaceAnnotations();
        if (faces != null) {
            for (FaceAnnotation face : faces) {
                message += String.format("Anger: %s\n", face.getAngerLikelihood());
                message += String.format("Joy: %s\n", face.getJoyLikelihood());
                message += String.format("Sorrow: %s\n", face.getSorrowLikelihood());
                message += String.format("Surprise: %s\n", face.getSurpriseLikelihood());
                message += String.format("Headwear: %s\n", face.getHeadwearLikelihood());
                message += String.format("Exposed: %s\n", face.getUnderExposedLikelihood());
                message += String.format("Blurred: %s\n", face.getBlurredLikelihood());
                message += String.format("Confidence: %s\n", face.getDetectionConfidence());
                message += "\n";

                if ("VERY_LIKELY".equals(face.getJoyLikelihood()) || "LIKELY".equals(face.getJoyLikelihood())
                        || "VERY_LIKELY".equals(face.getSurpriseLikelihood()) || "LIKELY".equals(face.getSurpriseLikelihood())) {
                    reacts.set(i, reacts.get(i) + 1);
                }
            }
        } else {
            message += "nothing";
        }

        Log.i("API Response", message);
        String temp = "";
        for(int react : reacts) {
            temp += ((Integer)react).toString();
        }
        Log.i("Reacts", temp);

        return message;





    }
}