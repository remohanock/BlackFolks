package com.loopz.blackfolks.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.loopz.blackfolks.R;

public class NothingLayout extends LinearLayout {
    private String text;
    private String button;
    private String image;
    private Context context;
    View view;

    public NothingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        View inflate = inflater.inflate(R.layout.nothing_layout, this);

        ImageView imageView = inflate.findViewById(R.id.image);
        TextView textView = inflate.findViewById(R.id.text);
        Button button = inflate.findViewById(R.id.button);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NothingLayout);
        imageView.setBackground(attributes.getDrawable(R.styleable.NothingLayout_image));
        textView.setText(attributes.getText(R.styleable.NothingLayout_text));
        button.setText(attributes.getText(R.styleable.NothingLayout_button));
        attributes.recycle();
    }

   /* public NothingLayout(Context context) {

        this.context = context;
    }*/

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void init() {
    }
}
