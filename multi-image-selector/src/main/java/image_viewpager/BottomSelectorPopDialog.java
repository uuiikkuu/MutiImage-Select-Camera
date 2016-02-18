package image_viewpager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import me.nereo.multi_image_selector.R;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Woode.Wang on 2015/10/23.
 * @since 1.0.0
 */
public class BottomSelectorPopDialog extends Dialog {


    public interface Listener {

        void onBtnItemClick(int position);

        void onCancelBtnClick();
    }

    private Listener mListener;

    public BottomSelectorPopDialog setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    public BottomSelectorPopDialog(Context context, CharSequence[] lBtnTexts) {
        this(context, lBtnTexts, Color.parseColor("#4aa6ff"));
    }

    public BottomSelectorPopDialog(Context context, CharSequence[] lBtnTexts, int textColor) {
        super(context, R.style.transparentFrameWindowStyle);
        LayoutInflater lLayoutInflater = ((Activity) context).getLayoutInflater();
        View view = lLayoutInflater.inflate(R.layout.popupwindow_nb_publish_type_seletor, null);
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.bottom_pop_window_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        LinearLayout lBtnsLl = (LinearLayout) findViewById(R.id.btns_ll_nm_publish_type_seletor);
        final int lLength = lBtnTexts.length;
        for (int i = 0; i < lLength; i++) {
            Button lBtn = new Button(context);

            lBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            lBtn.setText(lBtnTexts[i]);
            LinearLayout.LayoutParams lLayoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lLayoutParams.setMargins(0, 5, 0, 0);
            lBtn.setLayoutParams(lLayoutParams);
            int lBackgroundResource;
            if (i == 0) {
                lBackgroundResource = lLength == 1 ? R.drawable.shape_bottom_popdialog_selector_bg0 :
                        R.drawable.shape_bottom_popdialog_selector_bg1;
            } else if (i == lLength - 1) {
                lBackgroundResource = R.drawable.shape_bottom_popdialog_selector_bg3;
            } else {
                lBackgroundResource = R.drawable.shape_bottom_popdialog_selector_bg2;
            }
            lBtn.setTextColor(textColor);
            lBtn.setBackgroundResource(lBackgroundResource);
            final int lPosition = i;
            lBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onBtnItemClick(lPosition);
                    }
                    dismiss();
                }
            });
            lBtnsLl.addView(lBtn);
        }
        // 设置显示位置
        onWindowAttributesChanged(wl);
        // 设置点击外围解散
        setCanceledOnTouchOutside(true);
        final Button lCancelBtn = (Button) view.findViewById(R.id.cancel_btn_nm_publish_type_seletor);
        lCancelBtn.setTextColor(textColor);
        lCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancelBtnClick();
                }
                dismiss();
            }
        });
    }
}
