package com.chat.seoul.here.module.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seoul.here.R;
import com.chat.seoul.here.module.model.comment.CommentModel;

import java.util.ArrayList;

/**
 * Created by JJW on 2017-09-21.
 * Main화면의 리스트 뷰 관련 어뎁터
 */

public class CommentRecycleViewAdapter extends RecyclerView.Adapter<CommentRecycleViewAdapter.ViewHolder>
{


    private Context context;
    private ArrayList<CommentModel> items;
    private int item_layout;



    //생성자자
   public CommentRecycleViewAdapter(Context context, ArrayList<CommentModel> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    /**
     *  xml 레이아웃 사용 설정
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public CommentRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, null);


        return new ViewHolder(v);

    }

    /**
     *  리스트 화면 업데이트 시 호출
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(CommentRecycleViewAdapter.ViewHolder holder, int position) {

        if(item_layout != -1) {
            final CommentModel item = items.get(position);
            holder.imgIcon.setImageResource(R.drawable.ic_avatar);
            holder.txtUser.setText(item.getCOMMENT_USER());
            holder.message.setText(item.getCOMMENT_MESSAGE());
            holder.date.setText(item.getCOMMENT_DATE());
        }else
        {
            holder.message.setText("댓글이 없습니다. 댓글을 등록해주세요");
            holder.txtUser.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if(item_layout != -1)
            return this.items.size();
        else
            return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtUser;
        TextView message;
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            imgIcon = (ImageView) itemView.findViewById(R.id.imageVieCommnetImg);
            txtUser = (TextView) itemView.findViewById(R.id.txtViewCommentAuthor);
            message = (TextView) itemView.findViewById(R.id.txtViewCommentMessage);
            date = (TextView) itemView.findViewById(R.id.txtViewCommentDate);
        }
    }

}
