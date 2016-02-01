package ca.uqac.florentinth.speakerauthentication.GUI.JustifiedTextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import ca.uqac.florentinth.speakerauthentication.R;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class JustifiedTextView extends WebView {

    private static final String CORE = "<html><body style='text-align:justify;color:rgba(%s);" +
            "font-size:%dpx;margin: 0px 0px 0px 0px;'>%s</body></html>";

    private String text;
    private int textColor, backgroundColor, textSize;

    public JustifiedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JustifiedTextView(Context context, AttributeSet attrs, int i) {
        super(context, attrs, i);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.JustifiedTextView);

        text = a.getString(R.styleable.JustifiedTextView_text);

        if(text == null) {
            text = "";
        }

        textColor = a.getColor(R.styleable.JustifiedTextView_textColor, Color.TRANSPARENT);
        backgroundColor = a.getColor(R.styleable.JustifiedTextView_backgroundColor, Color
                .TRANSPARENT);
        textSize = a.getInt(R.styleable.JustifiedTextView_textSize, 12);

        a.recycle();

        this.setWebChromeClient(new WebChromeClient() {
        });

        reloadData();
    }

    public void setText(String s) {
        if(s == null) {
            this.text = "";
        } else {
            this.text = s;
        }

        reloadData();
    }

    private void reloadData() {
        if(text != null) {
            String data = String.format(CORE, toRGBA(textColor), textSize, text);
            this.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        }

        super.setBackgroundColor(backgroundColor);

        if(android.os.Build.VERSION.SDK_INT >= 11) {
            this.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setTextColor(int hexColor) {
        textColor = hexColor;
        reloadData();
    }

    public void setBackgroundColor(int hexColor) {
        backgroundColor = hexColor;
        reloadData();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        reloadData();
    }

    private String toRGBA(int hexColor) {
        String h = Integer.toHexString(hexColor);
        int a = Integer.parseInt(h.substring(0, 2), 16);
        int r = Integer.parseInt(h.substring(2, 4), 16);
        int g = Integer.parseInt(h.substring(4, 6), 16);
        int b = Integer.parseInt(h.substring(6, 8), 16);
        return String.format("%d,%d,%d,%d", r, g, b, a);
    }
}
