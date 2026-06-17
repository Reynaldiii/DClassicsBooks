package com.example.dclassicsbooks;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.dclassicsbooks.data.AppData;
import com.example.dclassicsbooks.model.Store;
import com.google.android.material.card.MaterialCardView;

public class StoresActivity extends AppCompatActivity {

    private final Store[] stores = AppData.getStores();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createScreen());
    }

    private View createScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getColor(R.color.background_color));

        root.addView(createHeader(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(88)
        ));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(24), dp(20), dp(24), dp(28));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        for (Store store : stores) {
            content.addView(createStoreCard(store));
        }

        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        return root;
    }

    private View createHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setPadding(dp(14), dp(16), dp(24), 0);
        header.setBackgroundColor(getColor(R.color.primary_color));

        ImageButton backButton = new ImageButton(this);
        backButton.setImageResource(R.drawable.ic_chevron_left);
        backButton.setColorFilter(Color.WHITE);
        backButton.setContentDescription("Back");
        backButton.setPadding(dp(8), dp(8), dp(8), dp(8));
        backButton.setBackgroundResource(selectableBorderless());
        backButton.setOnClickListener(v -> finish());
        header.addView(backButton, new LinearLayout.LayoutParams(dp(42), dp(42)));

        TextView title = createText("Our Store", 22, R.font.noto_serif_bold, Color.WHITE);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        titleParams.setMarginStart(dp(8));
        header.addView(title, titleParams);

        return header;
    }

    private View createStoreCard(Store store) {
        MaterialCardView cardView = new MaterialCardView(this);
        cardView.setRadius(dp(10));
        cardView.setCardElevation(dp(3));
        cardView.setCardBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = dp(18);
        cardView.setLayoutParams(cardParams);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        ImageView storeImage = new ImageView(this);
        storeImage.setImageResource(store.imageRes);
        storeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        content.addView(storeImage, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(154)
        ));

        LinearLayout textGroup = new LinearLayout(this);
        textGroup.setOrientation(LinearLayout.VERTICAL);
        textGroup.setPadding(dp(16), dp(13), dp(16), dp(16));

        TextView title = createText(store.name, 16, R.font.noto_serif_bold, getColor(R.color.card_dark));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(2);
        textGroup.addView(title);

        TextView address = createText(store.address, 11, R.font.manrope_regular, Color.rgb(130, 125, 118));
        address.setEllipsize(TextUtils.TruncateAt.END);
        address.setMaxLines(1);
        LinearLayout.LayoutParams addressParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        addressParams.topMargin = dp(4);
        textGroup.addView(address, addressParams);

        content.addView(textGroup);
        cardView.addView(content);

        return cardView;
    }

    private TextView createText(String text, int textSizeSp, int fontRes, int color) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(textSizeSp);
        Typeface typeface = ResourcesCompat.getFont(this, fontRes);
        textView.setTypeface(typeface);
        textView.setIncludeFontPadding(false);
        return textView;
    }

    private int selectableBorderless() {
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        return outValue.resourceId;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

}
