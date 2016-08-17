package view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zdd.blogcode_mc.R;


/**
 * Created by zdd on 16-7-11.
 */
public class PhotoText extends RelativeLayout {

    private final String TAG = "PHOTOTEXT";

    private ImageView iv;//图片
    private TextView tv;//文字

//以下是自定义View自己可以设置的属性
    private int mtextSize=15;
    private String mText;
    private Drawable mPhoto;
    private Drawable mBackGround;
    private ColorStateList mTextColor;

    public PhotoText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.photo_text,this,true);

        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.PhotoText);

        int n=ta.getIndexCount();
        for (int i=0;i<n;i++){
            int atrr = ta.getIndex(i);
            switch (atrr){
                case R.styleable.PhotoText_mText:
                    mText=ta.getString(atrr); break;
                case R.styleable.PhotoText_mphoto:
                    mPhoto=ta.getDrawable(atrr);break;
                case R.styleable.PhotoText_mBackGround:
                    mBackGround=ta.getDrawable(atrr);break;
                case R.styleable.PhotoText_mTextSize:
                    mtextSize=ta.getDimensionPixelSize(atrr,mtextSize);break;
                case R.styleable.PhotoText_mTextColor:
                    mTextColor=ta.getColorStateList(atrr);break;
            }
        }
        ta.recycle();

        iv= (ImageView) findViewById(R.id.photo);
        tv=(TextView)findViewById(R.id.photo_text);
        iv.setImageDrawable(mPhoto);
        tv.setText(mText);
        tv.setTextSize(mtextSize);
        tv.setTextColor(mTextColor!=null ? mTextColor : ColorStateList.valueOf(0xFF000000));
        setBackground(mBackGround);
    }

    public void setSelected(boolean is){
        iv.setSelected(is);
        tv.setSelected(is);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
