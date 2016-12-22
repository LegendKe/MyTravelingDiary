package com.ruihai.xingka.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ruihai.xingka.R;
import com.ruihai.xingka.entity.Image;
import com.ruihai.xingka.widget.SimpleTagImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    public ImageGridAdapter(Context context, boolean showCamera) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
    }

    /**
     * 显示选择指示器
     *
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     *
     * @param image
     */
    public void select(Image image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     *
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for (String path : resultList) {
            Image image = getImageByPath(path);
            if (image != null) {
                mSelectedImages.add(image);
            }
        }
        if (mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }

    /**
     * 通过图片路径更新默认图片
     *
     * @param resultList
     */
    public void updateSelected(ArrayList<String> resultList) {
        mSelectedImages.clear();
        for (String path : resultList) {
            Image image = getImageByPath(path);
            if (image != null) {
                mSelectedImages.add(image);
            }
        }
        notifyDataSetChanged();
    }


    private Image getImageByPath(String path) {
        if (mImages != null && mImages.size() > 0) {
            for (Image image : mImages) {
                if (image.path.equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     *
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 重置每个Column的Size
     *
     * @param columnWidth
     */
    public void setItemSize(int columnWidth) {

        if (mItemSize == columnWidth) {
            return;
        }

        mItemSize = columnWidth;

        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        } else {
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if (type == TYPE_CAMERA) {
            view = mInflater.inflate(R.layout.list_item_camera, viewGroup, false);
            view.setTag(null);
        } else if (type == TYPE_NORMAL) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
                holder = new ViewHolder(view);
            } else {
                holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
                    holder = new ViewHolder(view);
                }
            }
            if (holder != null) {
                holder.bindData(getItem(i));
            }
        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if (lp.height != mItemSize) {
            view.setLayoutParams(mItemLayoutParams);
        }

        return view;
    }

    class ViewHolder {
        SimpleTagImageView image;
        ImageView indicator;
        View mask;

        ViewHolder(View view) {
            image = (SimpleTagImageView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        void bindData(final Image data) {
            if (data == null) return;
            if (showSelectIndicator) { // 处理单选和多选状态

//                for (int i = 0; i < mSelectedImages.size(); i++) {
//                    if (mSelectedImages.contains(data)) {
//
//                    }
//                }

                indicator.setVisibility(View.VISIBLE);
                if (mSelectedImages.contains(data)) { // 设置选中状态
                    indicator.setSelected(true);
                    mask.setVisibility(View.VISIBLE);
                    image.setTagEnable(false);
//                    image.setTagText(String.format("0%d", mSelectedImages.size()));
                } else { // 未选择
                    indicator.setSelected(false);
                    mask.setVisibility(View.GONE);
                    image.setTagEnable(false);
                }
            } else {
                indicator.setVisibility(View.GONE);
                image.setTagEnable(false);
            }

            File imageFile = new File(data.path);
            if (mItemSize > 0) { // 显示图片
                Glide.with(mContext)
                        .load(imageFile)
                        .thumbnail(0.1f)
                        .centerCrop()
                        .error(R.drawable.default_error)
                        .into(image);
            }
        }
    }

}















//public class ImageGridAdapter extends BaseAdapter {
//
//    private static final int TYPE_CAMERA = 0;
//    private static final int TYPE_NORMAL = 1;
//
//    private Context mContext;
//
//    private LayoutInflater mInflater;
//    private boolean showCamera = true;
//    private boolean showSelectIndicator = true;
//
//    private List<Image> mImages = new ArrayList<>();
//    private List<Image> mSelectedImages = new ArrayList<>();
//
//    private int mItemSize;
//    private GridView.LayoutParams mItemLayoutParams;
//
//    public OnItemCheckedListener mOnItemClickListener;
//
//    public void setOnCheckedListener(OnItemCheckedListener listener) {
//        this.mOnItemClickListener = listener;
//    }
//
//    public ImageGridAdapter(Context context, boolean showCamera) {
//        mContext = context;
//        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.showCamera = showCamera;
//        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
//    }
//
//    /**
//     * 显示选择指示器
//     *
//     * @param b
//     */
//    public void showSelectIndicator(boolean b) {
//        showSelectIndicator = b;
//    }
//
//    public void setShowCamera(boolean b) {
//        if (showCamera == b) return;
//
//        showCamera = b;
//        notifyDataSetChanged();
//    }
//
//    public boolean isShowCamera() {
//        return showCamera;
//    }
//
//    /**
//     * 选择某个图片，改变选择状态
//     *
//     * @param image
//     */
//    public void select(Image image) {
//        if (mSelectedImages.contains(image)) {
//            mSelectedImages.remove(image);
//        } else {
//            mSelectedImages.add(image);
//        }
//        notifyDataSetChanged();
//    }
//
//
//    /**
//     * 通过图片路径设置默认选择
//     *
//     * @param resultList
//     */
//    public void setDefaultSelected(ArrayList<String> resultList) {
//        for (String path : resultList) {
//            Image image = getImageByPath(path);
//            if (image != null) {
//                mSelectedImages.add(image);
//            }
//        }
//        if (mSelectedImages.size() > 0) {
//            notifyDataSetChanged();
//        }
//    }
//
//    /**
//     * 通过图片路径更新默认图片
//     *
//     * @param resultList
//     */
//    public void updateSelected(ArrayList<String> resultList) {
//        mSelectedImages.clear();
//        for (String path : resultList) {
//            Image image = getImageByPath(path);
//            if (image != null) {
//                mSelectedImages.add(image);
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//
//    private Image getImageByPath(String path) {
//        if (mImages != null && mImages.size() > 0) {
//            for (Image image : mImages) {
//                if (image.path.equalsIgnoreCase(path)) {
//                    return image;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 设置数据集
//     *
//     * @param images
//     */
//    public void setData(List<Image> images) {
//        mSelectedImages.clear();
//
//        if (images != null && images.size() > 0) {
//            mImages = images;
//        } else {
//            mImages.clear();
//        }
//        notifyDataSetChanged();
//    }
//
//    /**
//     * 重置每个Column的Size
//     *
//     * @param columnWidth
//     */
//    public void setItemSize(int columnWidth) {
//
//        if (mItemSize == columnWidth) {
//            return;
//        }
//
//        mItemSize = columnWidth;
//
//        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);
//
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (showCamera) {
//            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
//        }
//        return TYPE_NORMAL;
//    }
//
//    @Override
//    public int getCount() {
//        return showCamera ? mImages.size() + 1 : mImages.size();
//    }
//
//    @Override
//    public Image getItem(int i) {
//        if (showCamera) {
//            if (i == 0) {
//                return null;
//            }
//            return mImages.get(i - 1);
//        } else {
//            return mImages.get(i);
//        }
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(final int position, View view, ViewGroup viewGroup) {
//
//        int type = getItemViewType(position);
//        if (type == TYPE_CAMERA) {
//            view = mInflater.inflate(R.layout.list_item_camera, viewGroup, false);
//            view.setTag(null);
//        } else if (type == TYPE_NORMAL) {
//            ViewHolder holder;
//            if (view == null) {
//                view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
//                holder = new ViewHolder(view);
//            } else {
//                holder = (ViewHolder) view.getTag();
//                if (holder == null) {
//                    view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
//                    holder = new ViewHolder(view);
//                }
//            }
//            if (holder != null) {
//                holder.bindData(getItem(position), position);
//                final View mask = holder.mask;
//                holder.indicator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        mOnItemClickListener.OnItemChecked(compoundButton, position,b,mask);
//                    }
//                });
//            }
//        }
//
//        /** Fixed View Size */
//        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
//        if (lp.height != mItemSize) {
//            view.setLayoutParams(mItemLayoutParams);
//        }
//
//        return view;
//    }
//
//    class ViewHolder {
//        //        SimpleTagImageView mImage;
//        ImageView mImage;
//        CheckBox indicator;
//        View mask;
//
//        ViewHolder(View view) {
////            mImage = (SimpleTagImageView) view.findViewById(R.id.image);
//            mImage = (ImageView) view.findViewById(R.id.image);
//            indicator = (CheckBox) view.findViewById(R.id.checkmark);
//            mask = view.findViewById(R.id.mask);
//            view.setTag(this);
//        }
//
//        void bindData(final Image data, final int position) {
//            if (data == null) return;
//            if (showSelectIndicator) { // 处理单选和多选状态
//
////                for (int i = 0; i < mSelectedImages.size(); i++) {
////                    if (mSelectedImages.contains(data)) {
////
////                    }
////                }
//
//                indicator.setVisibility(View.VISIBLE);
//
////                indicator.setVisibility(View.VISIBLE);
////                if (mSelectedImages.contains(data)) { // 设置选中状态
////                    indicator.setSelected(true);
////                    mask.setVisibility(View.VISIBLE);
//////                    mImage.setTagEnable(false);
//////                    image.setTagText(String.format("0%d", mSelectedImages.size()));
////                } else { // 未选择
////                    indicator.setSelected(false);
////                    mask.setVisibility(View.GONE);
//////                    mImage.setTagEnable(false);
////                }
//            } else {
//                indicator.setVisibility(View.GONE);
////                mImage.setTagEnable(false);
//            }
//
//            File imageFile = new File(data.path);
//            if (mItemSize > 0) { // 显示图片
//                Glide.with(mContext)
//                        .load(imageFile)
//                        .thumbnail(0.1f)
//                        .centerCrop()
//                        .error(R.drawable.default_error)
//                        .into(mImage);
//            }
//        }
//    }
//
//}
