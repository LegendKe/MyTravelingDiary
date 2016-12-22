package com.ruihai.xingka.ui.common.enter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mac on 15/12/16.
 */
public class EmojiUtils {
    /**
     * 讲带emoji表情的字符串解析成带表情的文本
     *
     * @param text
     * @return
     */
    public static CharSequence fromStringToEmoji(CharSequence text, Context context) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Pattern mPattern = Pattern.compile(":(\\S+?):");
        Matcher matcher = mPattern.matcher(text);
        Integer resId = null;
        while (matcher.find()) {
            resId = EmojiTranslate.emojiSrcMap.get(matcher.group(0));
            if (resId != null && resId > 0) {
                Drawable drawable = context.getResources().getDrawable(resId);
                int w = (int) (drawable.getIntrinsicWidth() * 0.32);
                int h = (int) (drawable.getIntrinsicHeight() * 0.32);
                drawable.setBounds(0, 0, w, h);//这里设置图片的大小
                ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                builder.setSpan(imageSpan, matcher.start(),
                        matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }

    /**
     * 讲带emoji表情的字符串解析成带表情的文本
     *
     * @param text
     * @return
     */

    public static CharSequence fromStringToEmoji1(CharSequence text, Context context) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Pattern mPattern = Pattern.compile(":(\\S+?):");
        Matcher matcher = mPattern.matcher(text);
        Integer resId = null;
        while (matcher.find()) {
            resId = EmojiTranslate.emojiSrcMap.get(matcher.group(0));
            if (resId != null && resId > 0) {
                Drawable drawable = context.getResources().getDrawable(resId);
                int w = (int) (drawable.getIntrinsicWidth() * 0.38);
                int h = (int) (drawable.getIntrinsicHeight() * 0.38);
                drawable.setBounds(0, 0, w, h);//这里设置图片的大小
                ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                builder.setSpan(imageSpan, matcher.start(),
                        matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }


}
