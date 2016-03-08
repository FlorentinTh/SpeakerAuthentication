package ca.uqac.florentinth.speakerauthentication.GUI.VisualizerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import ca.uqac.florentinth.speakerauthentication.R;

/**
 * Copyright 2016 Florentin Thullier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class VisualizerView extends FrameLayout {

    private static final int DEFAULT_NUM_COLUMNS = 20;
    private static final int TOP_RENDER_RANGE = 0;
    private static final int BOTTOM_RENDER_RANGE = 1;
    private static final int TOP_BOTTOM_RENDER_RANGE = 2;

    private int numColumns;
    private int renderColor;
    private int type;
    private int renderRange;

    private int mBaseY;

    private Canvas mCanvas;
    private Bitmap mCanvasBitmap;
    private Rect mRect = new Rect();
    private Paint mPaint = new Paint();
    private Paint mFadePaint = new Paint();

    private float mColumnWidth;
    private float mSpace;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        mPaint.setColor(renderColor);
        mFadePaint.setColor(Color.argb(138, 255, 255, 255));
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray args = context.obtainStyledAttributes(attrs, R.styleable.visualizerView);
        numColumns = args.getInteger(R.styleable.visualizerView_numColumns, DEFAULT_NUM_COLUMNS);
        renderColor = args.getColor(R.styleable.visualizerView_renderColor, Color.WHITE);
        type = args.getInt(R.styleable.visualizerView_renderType, Type.BAR.getFlag());
        renderRange = args.getInteger(R.styleable.visualizerView_renderRange, TOP_RENDER_RANGE);
        args.recycle();
    }

    public void setBaseY(int baseY) {
        mBaseY = baseY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mRect.set(0, 0, getWidth(), getHeight());

        if(mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap
                    .Config.ARGB_8888);
        }

        if(mCanvas == null) {
            mCanvas = new Canvas(mCanvasBitmap);
        }

        if(numColumns > getWidth()) {
            numColumns = DEFAULT_NUM_COLUMNS;
        }

        mColumnWidth = (float) getWidth() / (float) numColumns;
        mSpace = mColumnWidth / 8f;

        if(mBaseY == 0) {
            mBaseY = getHeight() / 2;
        }

        canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
    }

    public void receive(final int volume) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(mCanvas == null) {
                    return;
                }

                if(volume == 0) {
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                } else if((type & Type.FADE.getFlag()) != 0) {
                    mFadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
                    mCanvas.drawPaint(mFadePaint);
                } else {
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                }

                if((type & Type.BAR.getFlag()) != 0) {
                    drawBar(volume);
                }

                if((type & Type.PIXEL.getFlag()) != 0) {
                    drawPixel(volume);
                }

                invalidate();
            }
        });
    }

    private void drawBar(int volume) {
        for(int i = 0; i < numColumns; i++) {
            float height = getRandomHeight(volume);
            float left = i * mColumnWidth + mSpace;
            float right = (i + 1) * mColumnWidth - mSpace;

            RectF rect = createRectF(left, right, height);
            mCanvas.drawRect(rect, mPaint);
        }
    }

    private void drawPixel(int volume) {
        for(int i = 0; i < numColumns; i++) {
            float height = getRandomHeight(volume);
            float left = i * mColumnWidth + mSpace;
            float right = (i + 1) * mColumnWidth - mSpace;

            int drawCount = (int) (height / (right - left));
            if(drawCount == 0) {
                drawCount = 1;
            }
            float drawHeight = height / drawCount;

            for(int j = 0; j < drawCount; j++) {

                float top, bottom;
                RectF rect;

                switch(renderRange) {
                    case TOP_RENDER_RANGE:
                        bottom = mBaseY - (drawHeight * j);
                        top = bottom - drawHeight + mSpace;
                        rect = new RectF(left, top, right, bottom);
                        break;

                    case BOTTOM_RENDER_RANGE:
                        top = mBaseY + (drawHeight * j);
                        bottom = top + drawHeight - mSpace;
                        rect = new RectF(left, top, right, bottom);
                        break;

                    case TOP_BOTTOM_RENDER_RANGE:
                        bottom = mBaseY - (height / 2) + (drawHeight * j);
                        top = bottom - drawHeight + mSpace;
                        rect = new RectF(left, top, right, bottom);
                        break;

                    default:
                        return;
                }
                mCanvas.drawRect(rect, mPaint);
            }
        }
    }

    private float getRandomHeight(int volume) {
        double randomVolume = Math.random() * volume + 1;
        float height = getHeight();
        switch(renderRange) {
            case TOP_RENDER_RANGE:
                height = mBaseY;
                break;
            case BOTTOM_RENDER_RANGE:
                height = (getHeight() - mBaseY);
                break;
            case TOP_BOTTOM_RENDER_RANGE:
                height = getHeight();
                break;
        }
        return (height / 60f) * (float) randomVolume;
    }

    private RectF createRectF(float left, float right, float height) {
        switch(renderRange) {
            case TOP_RENDER_RANGE:
                return new RectF(left, mBaseY - height, right, mBaseY);
            case BOTTOM_RENDER_RANGE:
                return new RectF(left, mBaseY, right, mBaseY + height);
            case TOP_BOTTOM_RENDER_RANGE:
                return new RectF(left, mBaseY - height, right, mBaseY + height);
            default:
                return new RectF(left, mBaseY - height, right, mBaseY);
        }
    }

    public enum Type {
        BAR(0x1), PIXEL(0x2), FADE(0x4);

        private int mFlag;

        Type(int flag) {
            mFlag = flag;
        }

        public int getFlag() {
            return mFlag;
        }
    }
}
