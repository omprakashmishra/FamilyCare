package com.omsworld.familycare.SharedContacts;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by om's on 11/13/2016.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyviewHolder> implements View.OnClickListener {

    public List<SearchModel> mDataset;
    public Context context;
    CommonFunctions cmf;
    SlidingUpPanelLayout slidingPannel;
    private LayoutInflater LIoffer;
    private String status = "0";
    private String userImage;


    public SearchAdapter(List<SearchModel> mDataset, Context context, SlidingUpPanelLayout slidingPannel) {
        this.mDataset = mDataset;
        this.context = context;
        this.LIoffer = LayoutInflater.from(context);
        cmf=new CommonFunctions(context);
        userImage=cmf.myPreference.getString(context,cmf.gc.USER_IMAGE);
        this.slidingPannel = slidingPannel;
    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LIoffer.inflate(R.layout.search_item, parent, false);
        MyviewHolder holder = new MyviewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, final int position) {
        final SearchModel SearchModellst = mDataset.get(position);
        holder.TV_cat_text.setText(SearchModellst.name);
        holder.TV_cat_img.setText(SearchModellst.name);

        holder.TV_comment.setText(SearchModellst.comment);
        holder.TV_added_date.setText(SearchModellst.added_date);

        holder.TV_phone.setText(SearchModellst.phone);
        holder.TV_added_by.setText(SearchModellst.added_by);

        if (SearchModellst.added_by.equals("me")) {
            holder.IV_edit.setVisibility(View.VISIBLE);
            Picasso.with(context).load(userImage).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                    .into(holder.IV_cat_img);
            // then can delete and edit also show enable edit and delete button.
        } else {
            holder.IV_edit.setVisibility(View.GONE);
        }

        holder.IV_message.setOnClickListener(this);
        holder.IV_message.setTag(position);
        holder.IV_call.setOnClickListener(this);
        holder.IV_call.setTag(position);

        holder.RL_row.setOnClickListener(this);
        holder.RL_row.setTag(position);

        holder.IV_edit.setOnClickListener(this);
        holder.IV_edit.setTag(position);


    }

    public void setFilter(List<SearchModel> countryModels) {
        mDataset = new ArrayList<>();
        mDataset.addAll(countryModels);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        switch (view.getId()) {
            case R.id.IV_message:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "for you my friend " + "\n" + mDataset.get(position).name + " \n" + mDataset.get(position).phone);
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
                break;
            case R.id.IV_call:

                Intent intent = new Intent(Intent.ACTION_DIAL);
                //String CallPhone = Phone.replaceAll("\\D+", "");
                intent.setData(Uri.parse("tel:" + mDataset.get(position).phone));
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                }
                break;
            case R.id.RL_row:
                /*Bundle extras = new Bundle();
                // extras.putParcelable("imagebitmap", image);
                extras.putString("img_url", mDataset.get(position).url);
                Intent intt = new Intent(context, Fullsrc.class);
                intt.putExtras(extras);
                context.startActivity(intt);*/
                /*Sharedcontacts_fr.list_SelectedPos=position;
                slidingPannel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);*/
                break;
            case R.id.IV_edit:
                /*Bundle extras = new Bundle();
                // extras.putParcelable("imagebitmap", image);
                extras.putString("img_url", mDataset.get(position).url);
                Intent intt = new Intent(context, Fullsrc.class);
                intt.putExtras(extras);
                context.startActivity(intt);*/
                Sharedcontacts_fr.list_SelectedPos = position;
                slidingPannel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                break;


        }
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {

        public TextView TV_cat_text, TV_cat_img, TV_added_date, TV_comment, TV_added_by, TV_phone;
        public ImageView IV_call, IV_message, IV_edit;
        public CircleImageView IV_cat_img;
        public RelativeLayout RL_row;


        public MyviewHolder(View itemView) {
            super(itemView);
            TV_cat_text = (TextView) itemView.findViewById(R.id.TV_cat_text);
            TV_added_by = (TextView) itemView.findViewById(R.id.TV_added_by);
            TV_phone = (TextView) itemView.findViewById(R.id.TV_phone);
            TV_cat_img = (TextView) itemView.findViewById(R.id.TV_cat_img);
            TV_comment = (TextView) itemView.findViewById(R.id.TV_comment);
            TV_added_date = (TextView) itemView.findViewById(R.id.TV_added_date);
            IV_call = (ImageView) itemView.findViewById(R.id.IV_call);
            IV_cat_img = (CircleImageView) itemView.findViewById(R.id.IV_cat_img);
            IV_message = (ImageView) itemView.findViewById(R.id.IV_message);
            IV_edit = (ImageView) itemView.findViewById(R.id.IV_edit);
            TV_added_date = (TextView) itemView.findViewById(R.id.TV_added_date);
            RL_row = (RelativeLayout) itemView.findViewById(R.id.RL_row);

           /* IV_Likelike = (ImageView) itemView.findViewById(R.id.IV_Likelike);
            IV_Dislike = (ImageView) itemView.findViewById(R.id.IV_Dislike);
            shareText = (ImageView) itemView.findViewById(R.id.shareText);
            RL_row.setOnClickListener((View.OnClickListener) context);
            IV_Likelike.setOnClickListener((View.OnClickListener) context);
            shareText.setOnClickListener((View.OnClickListener) context);*/
        }
    }
}
