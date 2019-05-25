package com.bsecure.apha.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsecure.apha.R;
import com.bsecure.apha.controls.ColorGenerator;
import com.bsecure.apha.controls.TextDrawable;
import com.bsecure.apha.models.MemberModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class APHAListAdapter extends RecyclerView.Adapter<APHAListAdapter.ContactViewHolder> {


    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<MemberModel> classModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public APHAListAdapter(List<MemberModel> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.classModelList = list;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //    @Override
//    public int getItemCount() {
//        return array.length();
//    }
    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (classModelList.size() == 0) {
                arr = 0;

            } else {
                arr = classModelList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {

        try {
            MemberModel classMode_lList = classModelList.get(position);
            contactViewHolder.tv_title.setText(classMode_lList.getName());
            int color = generator.getColor(classMode_lList.getName());
            TextDrawable ic1 = builder.build(classMode_lList.getName().substring(0, 2), color);

            contactViewHolder.imgProfile.setImageDrawable(ic1);
            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, classModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getDate(long timeStamp) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "date";
        }
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<MemberModel> classModelList, final int position) {
        contactViewHolder.contact_user_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageRowClicked(classModelList, position);
                } catch (Exception e) {

                }
            }
        });

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.apha_row, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
        return myHoder;

    }


    public void clear() {
        // matchesList=null;
        final int size = classModelList.size();
        classModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void removeItem(int position) {
        classModelList.remove(position);
        notifyItemRemoved(position);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tv_title;
        public TextView section_tv;
        public TextView st_tps;
        public ImageView imgProfile;
        public LinearLayout contact_user_ll;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.cl_name);
            imgProfile = (ImageView) v.findViewById(R.id.icon_profile);
            contact_user_ll = v.findViewById(R.id.contact_user_ll);
            v.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ContactAdapterListener {
        void onMessageRowClicked(List<MemberModel> matchesList, int position);

    }


}
