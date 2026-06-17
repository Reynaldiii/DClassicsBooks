package com.example.dclassicsbooks;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import com.example.dclassicsbooks.model.Book;
import com.google.android.material.card.MaterialCardView;

public class BooksActivity extends AppCompatActivity {

    private static final float BOOK_COVER_ZOOM = 1.0f;

    private final Book[] books = AppData.getAllBooks();

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

        for (int i = 0; i < books.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.TOP);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rowParams.bottomMargin = dp(18);
            content.addView(row, rowParams);

            row.addView(createBookItem(books[i], true));
            if (i + 1 < books.length) {
                row.addView(createBookItem(books[i + 1], false));
            }
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

        TextView title = createText("All Books", 22, R.font.noto_serif_bold, Color.WHITE);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        titleParams.setMarginStart(dp(8));
        header.addView(title, titleParams);

        return header;
    }

    private View createBookItem(Book book, boolean leftColumn) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        itemParams.setMarginEnd(leftColumn ? dp(12) : 0);
        itemParams.setMarginStart(leftColumn ? 0 : dp(12));
        item.setLayoutParams(itemParams);

        MaterialCardView coverCard = new MaterialCardView(this);
        coverCard.setRadius(dp(8));
        coverCard.setCardElevation(dp(2));
        coverCard.setStrokeWidth(dp(1));
        coverCard.setStrokeColor(Color.rgb(209, 178, 105));

        ImageView cover = new ImageView(this);
        cover.setImageResource(book.imageRes);
        applyBookCoverZoom(cover);
        coverCard.addView(cover, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        item.addView(coverCard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(198)
        ));

        TextView title = createText(book.title, 13, R.font.noto_serif_semi_bold, getColor(R.color.card_dark));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(2);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = dp(8);
        item.addView(title, titleParams);

        TextView author = createText(book.author, 10, R.font.manrope_regular, Color.rgb(130, 125, 118));
        author.setEllipsize(TextUtils.TruncateAt.END);
        author.setMaxLines(1);
        item.addView(author);

        return item;
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

    private void applyBookCoverZoom(ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                updateBookCoverMatrix(imageView)
        );
        imageView.post(() -> updateBookCoverMatrix(imageView));
    }

    private void updateBookCoverMatrix(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null || imageView.getWidth() == 0 || imageView.getHeight() == 0) {
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth <= 0 || drawableHeight <= 0) {
            return;
        }

        float viewWidth = imageView.getWidth();
        float viewHeight = imageView.getHeight();
        float scale = Math.max(viewWidth / drawableWidth, viewHeight / drawableHeight) * BOOK_COVER_ZOOM;
        float dx = (viewWidth - drawableWidth * scale) / 2f;
        float dy = (viewHeight - drawableHeight * scale) / 2f;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        imageView.setImageMatrix(matrix);
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
