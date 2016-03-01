package com.getbase.floatingactionbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.ShapeDrawable.ShaderFactory;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FloatingActionButton extends ImageButton {

    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_MINI = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SIZE_NORMAL, SIZE_MINI})
    public @interface FAB_SIZE {
    }

    int mColorNormal;
    int mColorPressed;
    int mColorDisabled;
    String mTitle;
    @DrawableRes
    private int mIcon;
    private Drawable mIconDrawable;
    private int mSize;

    private float mCircleSize;
    private float mShadowRadius;
    private float mShadowOffset;
    private int mDrawableSize;
    boolean mStrokeVisible;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attributeSet) {
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.FloatingActionButton, 0, 0);
        //按钮普通状态下的颜色
        mColorNormal = attr.getColor(R.styleable.FloatingActionButton_fab_colorNormal, getColor(android.R.color.holo_blue_dark));
        //按钮点击状态下的颜色
        mColorPressed = attr.getColor(R.styleable.FloatingActionButton_fab_colorPressed, getColor(android.R.color.holo_blue_light));
        //按钮被禁用的颜色  enabled = false
        mColorDisabled = attr.getColor(R.styleable.FloatingActionButton_fab_colorDisabled, getColor(android.R.color.darker_gray));
        //决定circle的大小 SIZE_NORMAL 则 mCircleSize = 56dp , SIZE_MINI 则 mCircleSize = 40dp
        mSize = attr.getInt(R.styleable.FloatingActionButton_fab_size, SIZE_NORMAL);
        //按钮图标
        mIcon = attr.getResourceId(R.styleable.FloatingActionButton_fab_icon, 0);
        //用于配合floatingAction
        mTitle = attr.getString(R.styleable.FloatingActionButton_fab_title);
        //是否显示边框线
        mStrokeVisible = attr.getBoolean(R.styleable.FloatingActionButton_fab_stroke_visible, true);
        attr.recycle();

        updateCircleSize();
        //阴影的半径
        mShadowRadius = getDimension(R.dimen.fab_shadow_radius);
        //阴影的边距
        mShadowOffset = getDimension(R.dimen.fab_shadow_offset);
        updateDrawableSize();

        updateBackground();
    }

    /**
     * 计算整个drawable的大小（也是view的大小） 圆大小 + 2 * 阴影半径
     */
    private void updateDrawableSize() {
        mDrawableSize = (int) (mCircleSize + 2 * mShadowRadius);
    }

    /**
     * 更新circle圆的大小根据size
     */
    private void updateCircleSize() {
        mCircleSize = getDimension(mSize == SIZE_NORMAL ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
    }

    public void setSize(@FAB_SIZE int size) {
        if (size != SIZE_MINI && size != SIZE_NORMAL) {
            throw new IllegalArgumentException("Use @FAB_SIZE constants only!");
        }

        if (mSize != size) {
            mSize = size;
            updateCircleSize();
            updateDrawableSize();
            updateBackground();
        }
    }

    @FAB_SIZE
    public int getSize() {
        return mSize;
    }

    public void setIcon(@DrawableRes int icon) {
        if (mIcon != icon) {
            mIcon = icon;
            mIconDrawable = null;
            updateBackground();
        }
    }

    public void setIconDrawable(@NonNull Drawable iconDrawable) {
        if (mIconDrawable != iconDrawable) {
            mIcon = 0;
            mIconDrawable = iconDrawable;
            updateBackground();
        }
    }

    /**
     * @return the current Color for normal state.
     */
    public int getColorNormal() {
        return mColorNormal;
    }

    public void setColorNormalResId(@ColorRes int colorNormal) {
        setColorNormal(getColor(colorNormal));
    }

    public void setColorNormal(int color) {
        if (mColorNormal != color) {
            mColorNormal = color;
            updateBackground();
        }
    }

    /**
     * @return the current color for pressed state.
     */
    public int getColorPressed() {
        return mColorPressed;
    }

    public void setColorPressedResId(@ColorRes int colorPressed) {
        setColorPressed(getColor(colorPressed));
    }

    public void setColorPressed(int color) {
        if (mColorPressed != color) {
            mColorPressed = color;
            updateBackground();
        }
    }

    /**
     * @return the current color for disabled state.
     */
    public int getColorDisabled() {
        return mColorDisabled;
    }

    public void setColorDisabledResId(@ColorRes int colorDisabled) {
        setColorDisabled(getColor(colorDisabled));
    }

    public void setColorDisabled(int color) {
        if (mColorDisabled != color) {
            mColorDisabled = color;
            updateBackground();
        }
    }

    public void setStrokeVisible(boolean visible) {
        if (mStrokeVisible != visible) {
            mStrokeVisible = visible;
            updateBackground();
        }
    }

    public boolean isStrokeVisible() {
        return mStrokeVisible;
    }

    int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

    public void setTitle(String title) {
        mTitle = title;
        TextView label = getLabelView();
        if (label != null) {
            label.setText(title);
        }
    }

    TextView getLabelView() {
        return (TextView) getTag(R.id.fab_label);
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mDrawableSize, mDrawableSize);
    }

    //更新背景颜色
    void updateBackground() {
        final float strokeWidth = getDimension(R.dimen.fab_stroke_width);
        final float halfStrokeWidth = strokeWidth / 2f;

        LayerDrawable layerDrawable = new LayerDrawable(
                new Drawable[]{
                        getResources().getDrawable(mSize == SIZE_NORMAL ? R.drawable.fab_bg_normal : R.drawable.fab_bg_mini),
                        createFillDrawable(strokeWidth),
                        createOuterStrokeDrawable(strokeWidth),
                        getIconDrawable()
                });

        int iconOffset = (int) (mCircleSize - getDimension(R.dimen.fab_icon_size)) / 2;

        int circleInsetHorizontal = (int) (mShadowRadius);
        int circleInsetTop = (int) (mShadowRadius - mShadowOffset);
        int circleInsetBottom = (int) (mShadowRadius + mShadowOffset);

        //设置第一次drawable的偏移量 setLayerInset(layer, leftOffset, topOffset, rightOffset, bottomOffset)
        layerDrawable.setLayerInset(1,
                circleInsetHorizontal,
                circleInsetTop,
                circleInsetHorizontal,
                circleInsetBottom);

        //设置最外层边框的偏移量
        layerDrawable.setLayerInset(2,
                (int) (circleInsetHorizontal - halfStrokeWidth),
                (int) (circleInsetTop - halfStrokeWidth),
                (int) (circleInsetHorizontal - halfStrokeWidth),
                (int) (circleInsetBottom - halfStrokeWidth));

        //将图标设置为居中
        layerDrawable.setLayerInset(3,
                circleInsetHorizontal + iconOffset,
                circleInsetTop + iconOffset,
                circleInsetHorizontal + iconOffset,
                circleInsetBottom + iconOffset);

        //给定背景
        setBackgroundCompat(layerDrawable);
    }

    Drawable getIconDrawable() {
        if (mIconDrawable != null) {
            return mIconDrawable;
        } else if (mIcon != 0) {
            return getResources().getDrawable(mIcon);
        } else {
            return new ColorDrawable(Color.TRANSPARENT);
        }
    }

    private StateListDrawable createFillDrawable(float strokeWidth) {
        StateListDrawable drawable = new StateListDrawable();
        //禁用drawable
        drawable.addState(new int[]{-android.R.attr.state_enabled}, createCircleDrawable(mColorDisabled, strokeWidth));
        //按下drawable
        drawable.addState(new int[]{android.R.attr.state_pressed}, createCircleDrawable(mColorPressed, strokeWidth));
        //正常drawable
        drawable.addState(new int[]{}, createCircleDrawable(mColorNormal, strokeWidth));
        return drawable;
    }

    private Drawable createCircleDrawable(int color, float strokeWidth) {
        //拿到color中的透明度
        int alpha = Color.alpha(color);
        //拿到不透明的颜色
        int opaqueColor = opaque(color);

        //创建圆形的shape drawable
        ShapeDrawable fillDrawable = new ShapeDrawable(new OvalShape());

        final Paint paint = fillDrawable.getPaint();
        //抗锯齿
        paint.setAntiAlias(true);
        //设置画笔颜色
        paint.setColor(opaqueColor);

        Drawable[] layers = {
                fillDrawable,
                createInnerStrokesDrawable(opaqueColor, strokeWidth)//边框
        };

        //根据是否有透明度和是否显示边框来决定填充色是否显示透明
        LayerDrawable drawable = alpha == 255 || !mStrokeVisible
                ? new LayerDrawable(layers)
                : new TranslucentLayerDrawable(alpha, layers);

        //边框线的内边距
        int halfStrokeWidth = (int) (strokeWidth / 2f);
        drawable.setLayerInset(1, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth);

        return drawable;
    }

    private static class TranslucentLayerDrawable extends LayerDrawable {
        private final int mAlpha;

        public TranslucentLayerDrawable(int alpha, Drawable... layers) {
            super(layers);
            mAlpha = alpha;
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, mAlpha, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            canvas.restore();
        }
    }

    private Drawable createOuterStrokeDrawable(float strokeWidth) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

        final Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setAlpha(opacityToAlpha(0.02f));

        return shapeDrawable;
    }

    private int opacityToAlpha(float opacity) {
        return (int) (255f * opacity);
    }

    private int darkenColor(int argb) {
        return adjustColorBrightness(argb, 0.9f);
    }

    private int lightenColor(int argb) {
        return adjustColorBrightness(argb, 1.1f);
    }

    private int adjustColorBrightness(int argb, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(argb, hsv);

        hsv[2] = Math.min(hsv[2] * factor, 1f);

        return Color.HSVToColor(Color.alpha(argb), hsv);
    }

    private int halfTransparent(int argb) {
        return Color.argb(
                Color.alpha(argb) / 2,
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb)
        );
    }

    private int opaque(int argb) {
        return Color.rgb(
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb)
        );
    }

    private Drawable createInnerStrokesDrawable(final int color, float strokeWidth) {
        if (!mStrokeVisible) {
            //不显示边框
            return new ColorDrawable(Color.TRANSPARENT);
        }

        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        //底部暗色边框色
        final int bottomStrokeColor = darkenColor(color);
        //底部透明度 / 2
        final int bottomStrokeColorHalfTransparent = halfTransparent(bottomStrokeColor);
        //头部亮色边框
        final int topStrokeColor = lightenColor(color);
        //头部透明度 / 2
        final int topStrokeColorHalfTransparent = halfTransparent(topStrokeColor);

        final Paint paint = shapeDrawable.getPaint();
        //抗锯齿
        paint.setAntiAlias(true);
        //边框宽度
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Style.STROKE);
        //渐变 shape drawable
        shapeDrawable.setShaderFactory(new ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(width / 2, 0, width / 2, height,
                        new int[]{topStrokeColor, topStrokeColorHalfTransparent, color, bottomStrokeColorHalfTransparent, bottomStrokeColor},
                        new float[]{0f, 0.2f, 0.5f, 0.8f, 1f},
                        TileMode.CLAMP
                );
            }
        });

        return shapeDrawable;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        TextView label = getLabelView();
        if (label != null) {
            label.setVisibility(visibility);
        }

        super.setVisibility(visibility);
    }
}
