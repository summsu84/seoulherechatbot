package com.chat.seoul.here.module.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seoul.here.R;
import com.chat.seoul.here.module.model.festival.FestivalModel;

import java.util.ArrayList;

/**
 * Created by JJW on 2017-09-21.
 * Main화면의 리스트 뷰 관련 어뎁터
 */

public class MainRecycleViewAdapter extends RecyclerView.Adapter<MainRecycleViewAdapter.ViewHolder>
{


    private Context context;
    private ArrayList<FestivalModel> items;
    private int item_layout;



    //생성자자
   public MainRecycleViewAdapter(Context context, ArrayList<FestivalModel> items, int item_layout) {
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
    public MainRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item_card, null);
        return new ViewHolder(v);

    }

    /**
     *  리스트 화면 업데이트 시 호출
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MainRecycleViewAdapter.ViewHolder holder, int position) {

        final FestivalModel item = items.get(position);
        //Drawable drawable = ContextCompat.getDrawable(context, item.getImage());
        //holder.image.setBackground(drawable);
        String festivalName = "이름 " + item.getFESTIVAL_NAME();
        String festivalClass = item.getFESTIVAL_CLASS();
        String festivalPlace = "장소 " + item.getFESTIVAL_PLACE();

        holder.txtFestivalName.setText(festivalName);
        holder.txtFestivalClass.setText(festivalClass);
        holder.txtFestivalPlace.setText(festivalPlace);

        String startDate = item.getFESTIVAL_START_DATE().substring(0, 10);
        String endDate = item.getFESTIVAL_END_DATE().substring(0, 10);

        String date = "기간 " + startDate + " ~ " + endDate;

        holder.txtFestivalDate.setText(date);

        //이미지 체크ㅡ
        if("행사/대회".equals(festivalClass))
        {
            holder.imgFestivalClass.setImageResource(R.drawable.ic_festival_2);
        }else if("전시/관람".equals(festivalClass))
        {
            holder.imgFestivalClass.setImageResource(R.drawable.ic_festival_1);
        }else
        {
            holder.imgFestivalClass.setImageResource(R.drawable.ic_festival_3);
        }


        //자세히 보기 클릭 시 해당 웹페이지로 이동한다.
        holder.btnMoreInfo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                String url = item.getFESTIVAL_LINK();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);

            }
        });
/*        holder.cardview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, item.getFESTIVAL_LINK(), Toast.LENGTH_SHORT).show();
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFestivalClass;

        TextView txtFestivalName;
        TextView txtFestivalClass;
        TextView txtFestivalPlace;
        TextView txtFestivalDate;
        CardView cardview;
        Button btnMoreInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            imgFestivalClass = (ImageView) itemView.findViewById(R.id.imgFestivalClass);
            txtFestivalName = (TextView) itemView.findViewById(R.id.txtFestivalName);
            txtFestivalClass = (TextView) itemView.findViewById(R.id.txtFestivalClass);
            txtFestivalPlace = (TextView) itemView.findViewById(R.id.txtFestivalPlace);
            txtFestivalDate = (TextView) itemView.findViewById(R.id.txtFestivalDate);
            btnMoreInfo = (Button) itemView.findViewById(R.id.btnFestivalMore);

            cardview = (CardView) itemView.findViewById(R.id.mainCardView);
        }
    }




}
