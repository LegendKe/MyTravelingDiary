package com.ruihai.xingka.ui.trackway;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.ruihai.android.network.http.Params;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.TravelLineMoudle;
import com.ruihai.xingka.api.model.TravelTogetherInfo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.adapter.SelectedPhotoAdapter;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishBean;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishType;
import com.ruihai.xingka.ui.common.MultiImageSelectorActivity;
import com.ruihai.xingka.ui.common.PhotoPagerActivity;
import com.ruihai.xingka.ui.common.PhotoPickerActivity;
import com.ruihai.xingka.ui.common.enter.EmojiTranslate;
import com.ruihai.xingka.ui.common.enter.EmojiconSpan;
import com.ruihai.xingka.ui.trackway.publisher.service.PublishService;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.widget.FullyGridLayoutManager;
import com.ruihai.xingka.widget.wheel.OptionsPopupWindow;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gebixiaozhang on 16/5/16.
 */
public class TrackWayAddActivity extends BaseActivity implements SelectedPhotoAdapter.OnPhotoItemClickListener {
    public final static int REQUEST_PHOTO_PICKER_CODE = 1;
    public final static int REQUEST_READ_PICTURE_CODE = 4;
    public final static int REQUEST_TRAVEL_TIME_CODE = 2;
    public static final int REQUEST_CHOOSE_ROUTE_CODE = 5;
    public static final int REQUEST_CHOOSE_REQUIREMENT_CODE = 6;
    private static final int TEXT_MAX_COUNT = 3000;

    @BindView(R.id.tv_title)
    TextView titleView;
    @BindView(R.id.et_title)
    EditText topicEditText;
    @BindView(R.id.at_time_layout)
    LinearLayout timeLayout;
    @BindView(R.id.at_route_layout)
    LinearLayout routeLayout;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.et_content)
    EditText travelContentEt;
    @BindView(R.id.tv_count)
    TextView mCountTextView;

    @BindView(R.id.tv_selected_time)
    TextView travelTimeTv;
    @BindView(R.id.tv_selected_route)
    TextView routeTv;
    @BindView(R.id.tv_select_expenses)
    TextView expensesTv;
    @BindView(R.id.tv_slelct_numbers)
    TextView travelNumberTv;
    @BindView(R.id.tv_slelct_requirement)
    TextView requirementTv;

    private OptionsPopupWindow chooseExpensePW;
    private OptionsPopupWindow chooseNumberPW;
    private ArrayList<String> expenseTypeItems;
    private ArrayList<String> traverlNumberItems = new ArrayList<>();
    private String[] expenseTypes = {"AA", "免费"};

    private SelectedPhotoAdapter mPhotoAdapter;
    private ArrayList<String> mSelectedPath = new ArrayList<>();
    public PublishBean mPublishBean;

    private String beginTime;
    private String endTime;
    private int costType;
    private int personNum;
    private ArrayList<TravelLineMoudle> travelLines = new ArrayList<>();
    private String ruleContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_way_add);
        ButterKnife.bind(this);

        ruleContent = OnlineConfigAgent.getInstance().getConfigParams(mContext, "travelReleaseNotes");
        if (TextUtils.isEmpty(ruleContent)) {
            ruleContent = getString(R.string.msg_first_time_show_travel_together);
        }
        initView();
        initListenner();
        progress();
        if (AccountInfo.getInstance().isFirstTimeLogin()) {
            showRuleDialog();
        }
    }

    private void showRuleDialog() {
        AccountInfo.getInstance().setFirstTime(false);
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.setCancelable(false); // 设置点击返回键Dialog不消失
        dialog.setCanceledOnTouchOutside(false); // 设置点击屏幕Dialog不消失
        dialog.isTitleShow(true)
                .style(NormalDialog.STYLE_TWO)//
                .bgColor(Color.parseColor("#ffffff"))
                .cornerRadius(5)
                .title("自驾约行，拼车结伴")
                .titleTextSize(15.0f)
                .titleTextColor(Color.parseColor("#333333"))
                .content(ruleContent)
                .contentTextSize(12.0f)
                .btnNum(1)
                .btnText("知道了")
                .contentTextColor(Color.parseColor("#67676f"))
                .dividerColor(Color.parseColor("#dcdce4"))
                .btnTextSize(15f)
                .btnTextColor(Color.parseColor("#ff2814"))
                .widthScale(0.85f)
                .show();


        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
    }

    private void initView() {
        titleView.setText("发布旅拼");
        topicEditText.setSelection(topicEditText.length()); // 将光标移动至最后一个字符后面
        //费用类型选项选择器
        chooseExpensePW = new OptionsPopupWindow(this, 2);
        expenseTypeItems = new ArrayList<>(Arrays.asList(expenseTypes));
        // 1级联动效果
        chooseExpensePW.setPicker(expenseTypeItems);
        //设置标题
        chooseExpensePW.setTitle("请选择费用类型");
        //监听确定选择按钮
        chooseExpensePW.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                // 返回的分别是三个级别的选中位置
                String expenseType = expenseTypeItems.get(options1);
                expensesTv.setText(expenseType);
                costType = options1 + 1;
            }
        });
        chooseExpensePW.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });


        //人数选项选择器
        chooseNumberPW = new OptionsPopupWindow(this, 2);
        for (int i = 0; i <= 20; i++) {

            if (i == 0) {
                traverlNumberItems.add(0, "不限");
            } else {
                traverlNumberItems.add(i, String.valueOf(i));
            }
        }

        // 1级联动效果
        chooseNumberPW.setPicker(traverlNumberItems);
        //设置标题
        chooseNumberPW.setTitle("请选择旅伴人数");
        //监听确定选择按钮
        chooseNumberPW.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                // 返回的分别是三个级别的选中位置
                String travelNum = traverlNumberItems.get(options1);
                travelNumberTv.setText(travelNum);
                personNum = options1;

            }
        });
        chooseNumberPW.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

    }

    private void initListenner() {
        travelContentEt.addTextChangedListener(new TextWatcher() {
                                                   private int editStart;
                                                   private int editEnd;

                                                   @Override
                                                   public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                   }

                                                   @Override
                                                   public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                       if (count == 1 || count == 2) {
                                                           String newString = s.subSequence(start, start + count).toString();
                                                           String imgName = EmojiTranslate.sEmojiMap.get(newString);
                                                           if (imgName != null) {
                                                               final String format = ":%s:";
                                                               String replaced = String.format(format, imgName);
                                                               Editable editable = travelContentEt.getText();
                                                               editable.replace(start, start + count, replaced);
                                                               editable.setSpan(new EmojiconSpan(TrackWayAddActivity.this, imgName), start, start + replaced.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                           }
                                                       } else {
                                                           int emojiStart = 0;
                                                           int emojiEnd;
                                                           boolean startFinded = false;
                                                           int end = start + count;
                                                           for (int i = start; i < end; ++i) {
                                                               if (s.charAt(i) == ':') {
                                                                   if (!startFinded) {
                                                                       emojiStart = i;
                                                                       startFinded = true;
                                                                   } else {
                                                                       emojiEnd = i;
                                                                       if (emojiEnd - emojiStart < 2) { // 指示的是两个：的位置，如果是表情的话，间距肯定大于1
                                                                           emojiStart = emojiEnd;
                                                                       } else {
                                                                           String newString = s.subSequence(emojiStart + 1, emojiEnd).toString();
                                                                           EmojiconSpan emojiSpan = new EmojiconSpan(TrackWayAddActivity.this, newString);
                                                                           if (emojiSpan.getDrawable() != null) {
                                                                               Editable editable = travelContentEt.getText();
                                                                               editable.setSpan(emojiSpan, emojiStart, emojiEnd + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                               startFinded = false;
                                                                           } else {
                                                                               emojiStart = emojiEnd;
                                                                           }
                                                                       }
                                                                   }
                                                               }
                                                           }
                                                       }
                                                   }

                                                   @Override
                                                   public void afterTextChanged(Editable s) {
                                                       editStart = travelContentEt.getSelectionStart();
                                                       editEnd = travelContentEt.getSelectionEnd();

                                                       // 先去掉监听器，否则会出现栈溢出
                                                       travelContentEt.removeTextChangedListener(this);
                                                       // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
                                                       // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
                                                       while (calculateLength(s.toString()) > TEXT_MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                                                           s.delete(editStart - 1, editEnd);
                                                           editStart--;
                                                           editEnd--;
                                                           travelContentEt.setText(s);
                                                           travelContentEt.setSelection(editStart);
                                                       }


                                                       // 恢复监听器
                                                       travelContentEt.addTextChangedListener(this);

                                                       setLeftCount();
                                                   }
                                               }

        );


    }

    private void progress() {
        if (mPublishBean == null) {
            mPublishBean = newPublishBean();
        }
        mRecyclerView.setLayoutManager(new FullyGridLayoutManager(this, 4));
        mPhotoAdapter = new SelectedPhotoAdapter(this, mSelectedPath);
        mPhotoAdapter.setOnPhotoItemClickListener(this);
        mRecyclerView.setAdapter(mPhotoAdapter);
        if (AccountInfo.getInstance().getTravelTogetherDraftBoxInfo() != null) {
            TravelTogetherInfo data = AccountInfo.getInstance().getTravelTogetherDraftBoxInfo();
            if (data.title != null) {
                topicEditText.setText(data.title);
            }
            if (data.lineModule != null) {
                travelLines.addAll(data.lineModule);
                StringBuffer sb = new StringBuffer(256);
                for (int i = 0; i < travelLines.size(); i++) {
                    if (i == 0) {
                        sb.append(travelLines.get(i).address);
                    } else {
                        sb.append("-" + travelLines.get(i).address);
                    }
                }
                routeTv.setText(sb.toString());
            }
            if (data.beginTime != null && data.endTime != null) {
                beginTime = data.beginTime;
                endTime = data.endTime;
                travelTimeTv.setText(String.format("%s~%s", beginTime, endTime));
            }
            if (data.partnerContent != null) {
                requirementTv.setText(data.partnerContent);
            }
            if (data.content != null) {
                travelContentEt.setText(data.content);
            }
            if (data.personNum != 0) {
                chooseNumberPW.setSelectOptions(data.personNum);
                String travelNum = traverlNumberItems.get(data.personNum);
                travelNumberTv.setText(travelNum);
            }
            if (data.costType != 0) {
                chooseExpensePW.setSelectOptions(data.costType);
                String expenseType = expenseTypeItems.get(data.costType - 1);
                expensesTv.setText(expenseType);
            }
            if (data.imgPath != null && data.imgPath.size() > 0) {
                mSelectedPath.addAll(data.imgPath);
            }
            mPhotoAdapter.notifyDataSetChanged();

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<String> photos = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_PICKER_CODE) { // 从相册选择照片
                if (data != null) {
                    photos = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                }
                mSelectedPath.clear();
                if (photos != null) {
                    mSelectedPath.addAll(photos);
                }
                mPhotoAdapter.notifyDataSetChanged();

            } else if (requestCode == REQUEST_TRAVEL_TIME_CODE) { // 选择时间
                beginTime = data.getStringExtra("beginTime");
                endTime = data.getStringExtra("endTime");
                travelTimeTv.setText(String.format("%s~%s", beginTime, endTime));

            } else if (requestCode == REQUEST_CHOOSE_ROUTE_CODE) {//选择路线
                travelLines.clear();
                ArrayList<String> routeList = data.getStringArrayListExtra("route");
                StringBuffer sb = new StringBuffer(256);
                for (int i = 0; i < routeList.size() - 1; i++) {
                    TravelLineMoudle travelLineMoudle = new TravelLineMoudle();
                    travelLineMoudle.address = routeList.get(i);
                    travelLines.add(travelLineMoudle);
                    if (i == 0) {
                        sb.append(routeList.get(i));
                    } else {
                        sb.append("-" + routeList.get(i));
                    }
                }
                routeTv.setText(sb.toString());

            } else if (requestCode == REQUEST_CHOOSE_REQUIREMENT_CODE) {//选择旅伴要求
                String requirement = data.getStringExtra("requirement");
                requirementTv.setText(requirement);

            }
        } else if (requestCode == REQUEST_READ_PICTURE_CODE) {
            mSelectedPath.clear();
            mSelectedPath.addAll(data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS));
            //mSelectedPath = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            mPhotoAdapter.notifyDataSetChanged();

        }
    }


    /**
     * 返回按钮
     */
    @OnClick(R.id.tv_back)
    void onBack() {
        showExitConfirmDialog();
        //finish();
    }

    /**
     * 发布按钮
     */
    @OnClick(R.id.tv_right)
    void onRight() {
        publishTravelTogether();
    }

    /**
     * 是否将未保存的内容存入草稿箱
     */
    private void showExitConfirmDialog() {
        final String[] actionItems = {"保存草稿", "不保存"};
        final ActionSheetDialog dialog = new ActionSheetDialog(TrackWayAddActivity.this, actionItems, null);

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // 临时保存
                    //存入草稿箱
                    TravelTogetherInfo data = new TravelTogetherInfo();
                    data.beginTime = beginTime;
                    data.endTime = endTime;
                    data.content = travelContentEt.getText().toString();
                    data.title = topicEditText.getText().toString();
                    data.imgPath = mSelectedPath;
                    data.costType = costType;
                    data.personNum = personNum;
                    data.partnerContent = requirementTv.getText().toString();
                    data.lineModule = travelLines;
                    AccountInfo.getInstance().saveTravelTogetherDraftBoxInfo(data);
                } else if (position == 1) { //不保存
                    AccountInfo.getInstance().clearTravelTogetherDraftBoxInfo();
                }
                finish();
                dialog.dismiss();
            }
        });
        if (!TextUtils.isEmpty(travelContentEt.getText().toString()) || mSelectedPath.size() > 0 || !TextUtils.isEmpty(requirementTv.getText().toString()) || !"请添加".equals(routeTv.getText().toString()) || !"请选择".equals(travelTimeTv.getText().toString())) {
            dialog.title("是否保存草稿?").titleTextSize_SP(14.5f).isTitleShow(true).show();
        } else {
            finish();
        }
    }

    private void publishTravelTogether() {//发布按钮

        if (TextUtils.isEmpty(topicEditText.getText().toString())) {
            AppUtility.showToast("请输入旅拼主题");
            return;
        }
        if ("请选择".equals(travelTimeTv.getText().toString())) {
            AppUtility.showToast("请输入往返时间");
            return;
        }
        if ("请添加".equals(routeTv.getText().toString())) {
            AppUtility.showToast("请输入活动路线");
            return;
        }
        if (TextUtils.isEmpty(travelContentEt.getText().toString())) {
            AppUtility.showToast("请描述一下你的行程");
            return;
        }
        if (mSelectedPath.size() <= 0) {
            AppUtility.showToast("亲,请选择几张炫图吧");
            return;
        }


        TravelTogetherInfo data = new TravelTogetherInfo();
        data.beginTime = beginTime;
        data.endTime = endTime;
        data.content = travelContentEt.getText().toString();
        data.title = topicEditText.getText().toString();
        data.costType = costType;
        data.personNum = personNum;
        data.partnerContent = requirementTv.getText().toString();
        data.lineModule = travelLines;

        getPublishBean().setTravelTogetherInfo(data);

        String[] pics = getPublishBean().getPics();
        pics = mSelectedPath.toArray(new String[0]);
        getPublishBean().setPics(pics);


        PublishService.publish(mContext, getPublishBean());
//        if (isFromDraftBox) {//删除草稿箱的内容
//            AccountInfo.getInstance().clearUserDraftBoxInfo(AccountInfo.getInstance().getUserDraftBoxInfo());
//        }
        MainActivity.launch(TrackWayAddActivity.this, 2);
        finish();
    }

    public PublishBean getPublishBean() {
        return mPublishBean;
    }

    private PublishBean newPublishBean() {
        PublishBean bean = new PublishBean();
        bean.setStatus(PublishBean.PublishStatus.create);
        bean.setType(PublishType.travelTogether);
        Params params = new Params();
        // 默认所有人可见
        bean.setParams(params);
        return bean;
    }

    /**
     * 选择时间
     */
    @OnClick(R.id.at_time_layout)
    void chooseTime() {
        Intent intent = new Intent(TrackWayAddActivity.this, ChooseDateActivity.class);
        intent.putExtra("beginTime", beginTime);
        intent.putExtra("endTime", endTime);
        startActivityForResult(intent, REQUEST_TRAVEL_TIME_CODE);
        // startActivity(new Intent(TrackWayAddActivity.this, ChooseDateActivity.class));
    }

    /**
     * 选择地点
     */
    @OnClick(R.id.at_route_layout)
    void chooseRoute() {
        Intent intent = new Intent(TrackWayAddActivity.this, ChooseRouteActivity.class);
        ArrayList<String> datas = new ArrayList<>();
        if (travelLines.size() != 0) {
            for (int i = 0; i <= travelLines.size(); i++) {
                if (i == travelLines.size()) {
                    datas.add(i, "添加地点");
                } else {
                    datas.add(i, travelLines.get(i).address);
                }
            }
        }
        intent.putStringArrayListExtra("travel_route", datas);
        startActivityForResult(intent, REQUEST_CHOOSE_ROUTE_CODE);
        // startActivity(new Intent(TrackWayAddActivity.this, ChooseRouteActivity.class));
    }

    /**
     * 选择费用类型
     */
    @OnClick(R.id.at_expenses_layout)
    void chooseExpenseType() {
        chooseExpensePW.setSelectOptions(0);
        backgroundAlpha(0.8f); // 设置背景颜色变暗
        chooseExpensePW.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 选择人数
     */
    @OnClick(R.id.at_numbers_layout)
    void chooseNumberType() {
        chooseNumberPW.setSelectOptions(0);
        backgroundAlpha(0.8f); // 设置背景颜色变暗
        chooseNumberPW.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 选择旅伴要求
     */
    @OnClick(R.id.at_requirement_layout)
    void chooseRequirement() {
        Intent intent = new Intent(TrackWayAddActivity.this, ChooseRequirementActivity.class);
        intent.putExtra("travel_requirement", requirementTv.getText().toString());
        startActivityForResult(intent, REQUEST_CHOOSE_REQUIREMENT_CODE);
        //startActivity(new Intent(TrackWayAddActivity.this, ChooseRequirementActivity.class));
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {

        if (chooseExpensePW != null && chooseExpensePW.isShowing()) {
            chooseExpensePW.dismiss();
            return;
        } else if (chooseNumberPW != null && chooseNumberPW.isShowing()) {
            chooseNumberPW.dismiss();
            return;
        } else {
            showExitConfirmDialog();
            return;
        }


    }

    @Override
    public void onPhotoClick(View v, int position) {
        if (position == mSelectedPath.size()) { // 点击加号
            Intent intent = new Intent(mContext, MultiImageSelectorActivity.class);
            // 是否显示拍摄图片
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
            // 最大可选择图片数量
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
            // 选择模式
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            // 默认选择
            if (mSelectedPath != null && mSelectedPath.size() > 0) {
                intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectedPath);
            }
            startActivityForResult(intent, REQUEST_PHOTO_PICKER_CODE);
        } else {
            Intent intent = new Intent(mContext, PhotoPagerActivity.class);
            intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
            intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, mSelectedPath);
            intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 3);
            startActivityForResult(intent, REQUEST_READ_PICTURE_CODE);
        }
    }

    /**
     * 刷新剩余输入字数，最大值3000个字
     */
    private void setLeftCount() {
        mCountTextView.setText(String.format(getString(R.string.edittext_content_count),
                getInputCount(), TEXT_MAX_COUNT));
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength(travelContentEt.getText().toString());
    }

    /**
     * 计算图说内容的字数，一个汉字=两个英文字符，一个中文标点=两个英文标点
     * 注意：该函数的不是用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
//                len += 0.5;
                len += 1;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }
}
