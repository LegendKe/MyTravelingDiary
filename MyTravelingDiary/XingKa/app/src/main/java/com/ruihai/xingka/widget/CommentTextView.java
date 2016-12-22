package com.ruihai.xingka.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.CommentItem;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.common.enter.EmojiTranslate;
import com.ruihai.xingka.ui.mine.UserProfileActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 评论View
 */
public class CommentTextView extends TextView {
    private TextView instance;
    private Context mContext;
    private StyleSpan boldSpan;
    //回复者
    private ClickableSpan reviewSpan;
    //被回复者
    private ClickableSpan replySpan;
    private String mAccount;

    public CommentTextView(Context context) {
        super(context);
        mContext = context;
        mAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        initView();
    }

    public CommentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public CommentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        instance = this;
        setHighlightColor(getResources().getColor(android.R.color.transparent));
        setMovementMethod(LinkMovementMethod.getInstance());
        boldSpan = new StyleSpan(Typeface.NORMAL);
        reviewSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                CommentItem model1 = (CommentItem) widget.getTag();
                String account = model1.getReviewUid();
//                UserProfileActivity.launch(mContext, model1.getReviewUid());
                mAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
                if (mAccount.equals(account)) {
                    if (MainActivity.currentTabIndex != 3) {
                        MainActivity.launch(mContext, 1);
                    }
                } else {
                    if (model1.isAdmin()) {
                        UserProfileActivity.launch(mContext, account, 1, 1);
                    } else {
                        UserProfileActivity.launch(mContext, account, 1, 0);
                    }

                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setTypeface(Typeface.DEFAULT);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.orange));
            }
        };
        replySpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                CommentItem model1 = (CommentItem) widget.getTag();
                String account = model1.getReplyUid();
                mAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
                if (mAccount.equals(account)) {
                    if (MainActivity.currentTabIndex != 3) {
                        MainActivity.launch(mContext, 1);
                    }
                } else {
                    if (model1.isToisAdmin()){
                        UserProfileActivity.launch(mContext, account, 1,1);
                    }else {
                        UserProfileActivity.launch(mContext, account, 1,0);
                    }

                }
//                UserProfileActivity.launch(mContext, model1.getReviewUid());
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setTypeface(Typeface.DEFAULT);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.orange));
            }
        };
    }

    private TextBlankClickListener listener;

    public void setListener(TextBlankClickListener listener) {
        this.listener = listener;
    }

    private int mStart = -1;

    private int mEnd = -1;

    private android.os.Handler handler = new android.os.Handler();
    //计数
    private int leftTime;
    private Runnable countDownRunnable = new Runnable() {
        public void run() {
            leftTime--;
            if (leftTime == -1) {
                // 触发长按事件
                if (listener != null && !isMove) {
                    listener.onLongClick(instance);
                }
            } else {
                //100毫秒执行一次 超过500毫秒就说明触发长按
                handler.postDelayed(this, 100);
            }
        }
    };
    private boolean isMove = false;
    private float lastX;
    private float lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);

        int action = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            lastX = event.getX();
            lastY = event.getY();
            isMove = false;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float distanceX = Math.abs(lastX - event.getX());
            float distanceY = Math.abs(lastY - event.getY());
            if (distanceX > 1.5f || distanceY > 1.5f) {
                isMove = true;
//                return result;
            }
        }
        x -= getTotalPaddingLeft();
        y -= getTotalPaddingTop();

        x += getScrollX();
        y += getScrollY();

        Layout layout = getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);
        CharSequence text = getText();
        if (TextUtils.isEmpty(text) || !(text instanceof Spannable)) {
            return result;
        }
        Spannable buffer = (Spannable) text;
        ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
        if (link.length != 0) {
            if (action == MotionEvent.ACTION_DOWN) {
                mStart = buffer.getSpanStart(link[0]);
                mEnd = buffer.getSpanEnd(link[0]);
                if (mStart >= 0 && mEnd >= mStart) {
                    buffer.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.gray_press_bg)), mStart, mEnd,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (mStart >= 0 && mEnd >= mStart) {
                    buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), mStart, mEnd,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mStart = -1;
                    mEnd = -1;
                }
            }
            return true;
        } else {
            if (mStart >= 0 && mEnd >= mStart) {
                buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), mStart, mEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mStart = -1;
                mEnd = -1;
            }
            if (action == MotionEvent.ACTION_DOWN) {
                setBackgroundColor(mContext.getResources().getColor(R.color.gray_press_bg));
                //开始计数
                leftTime = 5;
                handler.post(countDownRunnable);
                return true;
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                setBackgroundColor(Color.TRANSPARENT);
                //如果没有调用长按 调用点击整个的监听
                if (leftTime > -1) {
                    leftTime = 10;
                    handler.removeCallbacks(countDownRunnable);//移除统计
                    if (listener != null && !isMove) {
                        listener.onBlankClick(this);
                    }
                }
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (isMove) {
                    setBackgroundColor(Color.TRANSPARENT);
                }
            }
            Selection.removeSelection(buffer);
            return result;
        }
    }

    /**
     * 设置回复的可点击文本
     *
     * @param model
     * @return
     */
    public void setReply(CommentItem model) {
        setText("");
        setTag(model);
        String reviewName = null;
        if (TextUtils.isEmpty(model.getReviewURemark())) {
            reviewName = model.getReviewUName();
        } else {
            reviewName = model.getReviewURemark();
        }
        SpannableStringBuilder stylesBuilder;
        String str;
        if (model.isReply()) {
            if (TextUtils.isEmpty(model.getReplyUName())) {

                str = reviewName + "：" + model.getReviewContent();
                //str = reviewName + "：" + EmojiTranslate.fromStringToEmoji(model.getReviewContent(), mContext);
                int reviewStart = str.indexOf(reviewName);
                //stylesBuilder = new SpannableStringBuilder(getEmojiText(str));
                stylesBuilder = new SpannableStringBuilder(str);
                setMySpan(str, stylesBuilder);
                stylesBuilder.setSpan(reviewSpan, reviewStart, reviewStart + reviewName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else {
                String replyRemark = model.getReplyURemark();
                String replyName = null;
                if (TextUtils.isEmpty(replyRemark)) {
                    replyName = model.getReplyUName();
                } else {
                    replyName = replyRemark;
                }
                str = reviewName + "回复" + replyName + "：" + model.getReviewContent();
                int reviewStart = str.indexOf(reviewName);
                int replyStart = str.indexOf(replyName);
                // stylesBuilder = new SpannableStringBuilder(getEmojiText(str));
                stylesBuilder = new SpannableStringBuilder(str);
                setMySpan(str, stylesBuilder);
                stylesBuilder.setSpan(reviewSpan, reviewStart, reviewStart + reviewName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stylesBuilder.setSpan(replySpan, replyStart, replyStart + replyName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            str = reviewName + "：" + model.getReviewContent();
            int reviewStart = str.indexOf(reviewName);
            stylesBuilder = new SpannableStringBuilder(str);
            setMySpan(str, stylesBuilder);
            stylesBuilder.setSpan(reviewSpan, reviewStart, reviewStart + reviewName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        append(stylesBuilder);
    }

    public interface TextBlankClickListener {
        void onBlankClick(View view);

        void onLongClick(View view);

    }


    /*
     *  讲带emoji表情的字符串解析成带表情的文本
     */
    public void setMySpan(String text, SpannableStringBuilder stylesBuilder) {
        Pattern mPattern = Pattern.compile(":(\\S+?):");
        Matcher matcher = mPattern.matcher(text);
        Integer resId = null;
        while (matcher.find()) {
            resId = EmojiTranslate.emojiSrcMap.get(matcher.group(0));
            if (resId != null && resId > 0) {
                Drawable drawable = mContext.getResources().getDrawable(resId);
                int w = (int) (drawable.getIntrinsicWidth() * 0.32);
                int h = (int) (drawable.getIntrinsicHeight() * 0.32);
                drawable.setBounds(0, 0, w, h);//这里设置图片的大小
                ImageSpan imageSpan = new ImageSpan(drawable);
                stylesBuilder.setSpan(imageSpan, matcher.start(),
                        matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private Html.ImageGetter emojiGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            // 根据资源名查找资源ID
            int id = getResources().getIdentifier(source, "drawable", mContext.getPackageName());

            if (id > 0) {
                Drawable emoji = getResources().getDrawable(id);
                int w = (int) (emoji.getIntrinsicWidth() * 0.4);
                int h = (int) (emoji.getIntrinsicHeight() * 0.4);
                emoji.setBounds(0, 0, w, h);
                return emoji;
            } else {
                Drawable emoji = getResources().getDrawable(R.mipmap.ic_launcher);
                int w = (int) (emoji.getIntrinsicWidth() * 0.4);
                int h = (int) (emoji.getIntrinsicHeight() * 0.4);
                emoji.setBounds(0, 0, w, h);
                return emoji;
            }


        }
    };
}
