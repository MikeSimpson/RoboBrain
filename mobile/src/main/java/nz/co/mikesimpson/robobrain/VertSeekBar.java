package nz.co.mikesimpson.robobrain;

        import android.content.Context;
        import android.graphics.Canvas;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.widget.SeekBar;

public class VertSeekBar extends SeekBar {

    private int x;
    private int y;
    private int z;
    private int w;

    public VertSeekBar(Context context) {
        super(context);
    }

    public VertSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public synchronized void setProgress(int progress) {

        super.setProgress(progress);

        onSizeChanged(x, y, z, w);

    }

    public VertSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
        this.x=w;
        this.y=h;
        this.z=oldw;
        this.w=oldh;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        this.setThumbOffset( getMax() - (int) (getMax() * event.getY() / getHeight()) * (this.getBottom()-this.getTop()) );

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}