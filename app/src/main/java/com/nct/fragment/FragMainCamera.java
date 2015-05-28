package com.nct.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.nct.constants.Constants;
import com.nct.customview.PopupActionItem;
import com.nct.customview.QuickAction;
import com.nct.customview.TfTextView;
import com.nct.mv.AtCamera;

import java.io.File;

import thh.com.mycouper.R;

/**
 * Created by nghidv on 5/23/2015.
 */
public class FragMainCamera extends CameraFragment implements View.OnClickListener {

    private final String SCENE_MODE_CAMERA[] = {Camera.Parameters.FLASH_MODE_AUTO, Camera.Parameters.FLASH_MODE_ON, Camera.Parameters.FLASH_MODE_OFF};

    private static final int POP_UP_FLAST_AUTO = 1;
    private static final int POP_UP_FLAST_ON = 2;
    private static final int POP_UP_FLAST_OFF = 3;

    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";
    private long lastFaceToast=0L;
    String flashMode = null;

    private ImageView btnCapture, btnGallery, btnClose, btnSwitchCamera;
    private TfTextView btnFlast;
    private RelativeLayout contentBorder;

    private QuickAction quickAction;
    private PopupActionItem flastAuto;
    private PopupActionItem flastOn;
    private PopupActionItem flastOff;

    private String mFlastMode = SCENE_MODE_CAMERA[0];

    public static FragMainCamera newInstance(boolean useFFC) {
        FragMainCamera f=new FragMainCamera();
        Bundle args=new Bundle();

        args.putBoolean(KEY_USE_FFC, useFFC);
        f.setArguments(args);

        return(f);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(new DemoCameraHost(getActivity()));
        setHost(builder.useFullBleedPreview(true).build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View cameraView = super.onCreateView(inflater, container, savedInstanceState);
        View results=inflater.inflate(R.layout.frag_main_camera, container, false);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels - 60;
        int height = width / 2;

        contentBorder = (RelativeLayout) results.findViewById(R.id.ly_border);

        RelativeLayout.LayoutParams ly = (RelativeLayout.LayoutParams) contentBorder.getLayoutParams();
        ly.width = width;
        ly.height = height;

        ((ViewGroup)results.findViewById(R.id.camera)).addView(cameraView);

        btnGallery = (ImageView) results.findViewById(R.id.button_gallery);
        btnGallery.setOnClickListener(this);
        btnCapture = (ImageView) results.findViewById(R.id.button_capture);
        btnCapture.setOnClickListener(this);
        btnClose = (ImageView) results.findViewById(R.id.button_close);
        btnClose.setOnClickListener(this);
        btnSwitchCamera = (ImageView) results.findViewById(R.id.button_ChangeCamera);
        btnSwitchCamera.setOnClickListener(this);
        btnFlast = (TfTextView) results.findViewById(R.id.btn_flast);
        btnFlast.setOnClickListener(this);

        getContract().setSingleShotMode(true);

        autoFocus();
        setDefaulMode();
        setFlashMode(mFlastMode);
        innitPopup();

        if (isRecording()) {
            btnCapture.setClickable(false);
        }
        return(results);
    }

    private void setDefaulMode(){
        String mtype = getActivity().getResources().getString(R.string.camera_flast_auto);
        if(mFlastMode.equals("auto"))
            mtype = getActivity().getResources().getString(R.string.camera_flast_auto);
        else if(mFlastMode.equals("on"))
            mtype = getActivity().getResources().getString(R.string.camera_flast_on);
        else{
            mtype = getActivity().getResources().getString(R.string.camera_flast_off);
        }
        btnFlast.setText(mtype);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_capture:
                takeSimplePicture();
                break;
            case R.id.button_gallery:
                ((AtCamera) getActivity()).getImageFromGarelly();
                break;
            case R.id.button_close:
                getActivity().finish();
                break;
            case R.id.button_ChangeCamera:
                ((AtCamera) getActivity()).changeCamera();
                break;
            case R.id.btn_flast:
                quickAction.show(v);
                break;
        }
    }

    private void innitPopup(){
        quickAction = new QuickAction(getActivity(), QuickAction.VERTICAL);
        quickAction.setStatusBackround(false);
        flastAuto = new PopupActionItem(POP_UP_FLAST_AUTO, getActivity().getResources().getString(R.string.camera_flast_auto), null);
        flastOn = new PopupActionItem(POP_UP_FLAST_ON, getActivity().getResources().getString(R.string.camera_flast_on), null);
        flastOff = new PopupActionItem(POP_UP_FLAST_OFF, getActivity().getResources().getString(R.string.camera_flast_off), null);

        quickAction.addActionItem(flastAuto);
        quickAction.addActionItem(flastOn);
        quickAction.addActionItem(flastOff);

        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                String mtype = getActivity().getResources().getString(R.string.camera_flast_auto);
                switch (actionId){
                    case POP_UP_FLAST_AUTO:
                        mtype = getActivity().getResources().getString(R.string.camera_flast_auto);
                        mFlastMode = SCENE_MODE_CAMERA[0];
                        break;
                    case POP_UP_FLAST_ON:
                        mtype = getActivity().getResources().getString(R.string.camera_flast_on);
                        mFlastMode = SCENE_MODE_CAMERA[1];
                        break;
                    case POP_UP_FLAST_OFF:
                        mtype = getActivity().getResources().getString(R.string.camera_flast_off);
                        mFlastMode = SCENE_MODE_CAMERA[2];
                        break;
                }
                setFlashMode(mFlastMode);
                btnFlast.setText(mtype);
            }
        });
    }

    @Override
    public String getFlashMode() {
        return flashMode;
    }

    @Override
    public void setFlashMode(String flashMode) {
        this.flashMode = flashMode;
    }

    void takeSimplePicture() {
        PictureTransaction xact=new PictureTransaction(getHost());
        xact.flashMode(flashMode);
        takePicture(xact);
    }

    Contract getContract() {
        return((Contract)getActivity());
    }

    public interface Contract {
        boolean isSingleShotMode();

        void setSingleShotMode(boolean mode);
    }

    class DemoCameraHost extends SimpleCameraHost implements
            Camera.FaceDetectionListener {
        boolean supportsFaces=false;

        public DemoCameraHost(Context _ctxt) {
            super(_ctxt);
        }

        @Override
        public boolean useFrontFacingCamera() {
            if (getArguments() == null) {
                return(false);
            }
            return(getArguments().getBoolean(KEY_USE_FFC));
        }

        @Override
        public boolean useSingleShotMode() {
            return true;
        }

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            super.saveImage(xact, image);

            File file = getPhotoPath();
            String mPath = file.getPath();
            if(mPath != null){
                FragCropImage fragment = new FragCropImage();
                Bundle bundle = new Bundle();
                bundle.putString(FragCropImage.KEY_BUNDLE_PHOTO_GALLERY, mPath);
                fragment.setArguments(bundle);
                ((AtCamera) getActivity()).pushFragments(Constants.TAB_CAMERA_CAMERA, fragment, false, true);
            }else{
                FragCropImage.imageToShow = image;
                ((AtCamera) getActivity()).pushFragments(Constants.TAB_CAMERA_CAMERA, new  FragCropImage(), false, true);
            }
        }

        @Override
        public void autoFocusAvailable() {
            if (supportsFaces)
                startFaceDetection();
        }

        @Override
        public void autoFocusUnavailable() {
            stopFaceDetection();
        }

        @Override
        public void onCameraFail(CameraHost.FailureReason reason) {
            super.onCameraFail(reason);

            Toast.makeText(getActivity(), "Sorry, but you cannot use the camera now!", Toast.LENGTH_LONG).show();
        }

        @Override
        public Parameters adjustPreviewParameters(Parameters parameters) {
            flashMode = CameraUtils.findBestFlashModeMatch(parameters,
                            Parameters.FLASH_MODE_RED_EYE,
                            Parameters.FLASH_MODE_AUTO,
                            Parameters.FLASH_MODE_ON,
                            Parameters.FLASH_MODE_OFF);

            if (parameters.getMaxNumDetectedFaces() > 0) {
                supportsFaces = true;
            }

            return(super.adjustPreviewParameters(parameters));
        }

        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length > 0) {
                long now= SystemClock.elapsedRealtime();

                if (now > lastFaceToast + 10000) {
                    lastFaceToast=now;
                }
            }
        }

        @Override
        @TargetApi(16)
        public void onAutoFocus(boolean success, Camera camera) {
            super.onAutoFocus(success, camera);
            btnCapture.setClickable(true);
        }

        @Override
        public boolean mirrorFFC() {
            return true;
        }
    }

}
