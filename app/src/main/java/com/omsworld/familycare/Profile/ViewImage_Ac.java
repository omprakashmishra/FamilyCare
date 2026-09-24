package com.omsworld.familycare.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewImage_Ac extends Activity {
    CommonFunctions cmf;
    ImageView IV_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image_ac);
        initialization();
    }

    private void initialization() {
        cmf = new CommonFunctions(this);
        try {
            Bundle bdl = getIntent().getExtras();
            String ImageUrl = bdl.getString("ImageUrl");
            loadImage(ImageUrl);
        } catch (Exception ex) {
        }

        toolBarControl();
    }

    private void toolBarControl() {
        //----------------------------------------------------------------
        //for toolbar control...

        RelativeLayout RL_head = (RelativeLayout) findViewById(R.id.RL_head);
        RL_head.setBackgroundColor(getResources().getColor(R.color.black));
        ImageView IV_backimage = (ImageView) findViewById(R.id.IV_backimage);
        IV_backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        //----------------------------------------
    }

    private void loadImage(String imageUrl) {
        try {
            IV_image = (ImageView) findViewById(R.id.IV_image);

            final ProgressBar mprogressBar = (ProgressBar) findViewById(R.id.mprogressBar);
            Picasso.with(this).load(imageUrl).fit().into(IV_image, new Callback() {
                @Override
                public void onSuccess() {
                    mprogressBar.setVisibility(View.GONE);
                    freeZoom(IV_image);
                }

                @Override
                public void onError() {
                    mprogressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception ex) {
        }
    }


    //==============================================================================================
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;


    @SuppressLint("ClickableViewAccessibility")
    private void freeZoom(ImageView imageDetail) {
        imageDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                System.out.println("matrix=" + savedMatrix.toString());
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);
                return true;
            }

            @SuppressLint("FloatMath")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.floor(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });


    }
}
