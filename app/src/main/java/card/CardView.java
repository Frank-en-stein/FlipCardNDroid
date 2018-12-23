package card;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import constants.Constants;
import com.example.home.flipcardndroid.R;


public class CardView {
    public Context context;
    ImageView imageView;
    int imageResource;
    boolean isShowingFront;
    boolean canFlip;

    public CardView(ImageView imgVw, Context context) {
        this.context = context;
        this.imageView = imgVw;
        this.isShowingFront = false;
        this.canFlip = false;
        this.imageView.setImageResource(R.drawable.backside);
        this.imageResource = -1;
        this.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flip();
            }
        });
    }

    public void setCard(String cardType) {
        try {
            this.imageResource = R.drawable.class.getField("card_" + cardType).getInt(null);
            this.canFlip = true;
        }
        catch (Exception e) {
            Log.e("card not found", e.getMessage());
        }
    }

    public void flipBack() {
        if (isShowingFront) flip();
    }

    private void flip() {
        if (!canFlip) return;
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imageView.setImageResource(!isShowingFront ? imageResource : R.drawable.backside);
                isShowingFront = !isShowingFront;
                oa2.start();
            }
        });
        oa1.start();
    }
}
