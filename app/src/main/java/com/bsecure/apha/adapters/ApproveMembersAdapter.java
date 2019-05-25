package com.bsecure.apha.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bsecure.apha.R;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.models.Members;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class ApproveMembersAdapter extends RecyclerView.Adapter<ApproveMembersAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<Members> matchesList;
    private List<Members> contactListFiltered;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public ApproveMembersAdapter(List<Members> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactListFiltered = list;
        this.matchesList = list;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //    @Override
//    public int getItemCount() {
//        return array.length();
//    }
    public void clear() {
        // matchesList=null;
        final int size = matchesList.size();
        matchesList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (matchesList.size() == 0) {
                arr = 0;

            } else {
                arr = matchesList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {

        try {
            Members mycontactlist = matchesList.get(position);
            //JSONObject jsonObject = array.getJSONObject(i);

            contactViewHolder.tv_title.setText((mycontactlist).getMember_name());
            contactViewHolder.contact_ph.setText((mycontactlist).getReg_mobile_no());

            String pic_url = mycontactlist.getProfile_image();

            String f_path = Paths.up_load + pic_url;
            if (pic_url.length() != 0 || !pic_url.isEmpty()) {
                Glide.with(context).load(f_path).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.contact_image);

            }
            if (mycontactlist.getApproval_status().equalsIgnoreCase("0")) {
                contactViewHolder.chk_name.setChecked(false);
            } else {
                contactViewHolder.chk_name.setChecked(true);
            }
            String majic_user = mycontactlist.getMember_id();
            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));
            applyClickEvents(contactViewHolder, matchesList, position, value, contactViewHolder.chk_name);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<Members> matchesList, final int position, final boolean value, final Switch chk_name) {
        contactViewHolder.chk_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClicked(matchesList, value, chk_name, position);
                } catch (Exception e) {

                }
            }
        });

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.members_items_app, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;

    }

//    public long getItemId(int position) {
//        return position + 1;
//    }
//
//    public void setItems(JSONArray aArray) {
//        this.array = aArray;
//    }
//
//    public JSONArray getItems() {
//        return this.array;
//    }
//
//    public void clear() {
//        array = null;
//        array = new JSONArray();
//    }
//
//    public void release() {
//        array = null;
//    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_title;
        protected TextView contact_ph;
        protected ImageView contact_image;
        protected Switch chk_name;
        RelativeLayout row_user_ll;
        LinearLayout contact_user_ll;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.cl_name);
            contact_ph = (TextView) v.findViewById(R.id.section_tv);
            chk_name = (Switch) v.findViewById(R.id.check_sw);
            contact_image = (ImageView) v.findViewById(R.id.icon_profile);
//            row_user_ll = itemView.findViewById(R.id.row_user);
//            contact_user_ll = itemView.findViewById(R.id.contact_row);

        }
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);

        }
        notifyItemChanged(pos);
    }

    public interface ContactAdapterListener {

        void onRowClicked(List<Members> matchesList, boolean value, Switch chk_name, int position);
    }

}
