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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsecure.apha.R;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.controls.ColorGenerator;
import com.bsecure.apha.controls.TextDrawable;
import com.bsecure.apha.models.VipModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class VipListAdapter extends RecyclerView.Adapter<VipListAdapter.ContactViewHolder> implements Filterable {


    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<VipModel> classModelList;
    private List<VipModel> contactListFiltered;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public VipListAdapter(List<VipModel> list, Context context, ContactAdapterListener listener) {
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
            VipModel classMode_lList = classModelList.get(position);
            contactViewHolder.tv_title.setText(classMode_lList.getVip_name());
            contactViewHolder.section_tv.setText(classMode_lList.getDesignation());
            if (classMode_lList.getImage().isEmpty()) {
                int color = generator.getColor(classMode_lList.getVip_name());
                TextDrawable ic1 = builder.build(classMode_lList.getVip_name().substring(0, 2), color);

                contactViewHolder.imgProfile.setImageDrawable(ic1);
            } else {
                String path = Paths.up_load + classMode_lList.getImage();
                Glide.with(context).load(path).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.imgProfile);
            }
            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, classModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<VipModel> classModelList, final int position) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.vip_row, parent, false);
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
        public ImageView imgProfile;
        public LinearLayout contact_user_ll;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.cl_name);
            section_tv = (TextView) v.findViewById(R.id.section_tv);
            imgProfile = (ImageView) v.findViewById(R.id.icon_profile);
            contact_user_ll = v.findViewById(R.id.view_list_main_content);
            v.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ContactAdapterListener {
        void onMessageRowClicked(List<VipModel> matchesList, int position);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    classModelList = contactListFiltered;
                } else {
                    List<VipModel> filteredList = new ArrayList<>();
                    for (VipModel row : contactListFiltered) {

                        if (row.getVip_name().contains(charSequence) || row.getVip_name().toLowerCase().contains(charSequence) || row.getPhone_number().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    classModelList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = classModelList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                classModelList = (ArrayList<VipModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
