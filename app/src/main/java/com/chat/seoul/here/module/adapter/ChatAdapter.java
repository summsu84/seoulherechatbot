package com.chat.seoul.here.module.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.seoul.here.R;
import com.chat.seoul.here.module.model.ChatMessage;
import com.chat.seoul.here.module.model.TypeWriter;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_INPUT_LATLON;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_PATH_SEARCH_NO_ENTITY_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_RECOMMEND_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_FESTIVAL;
import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_PLACE;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_CURRENT_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_END;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_HELLO;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_HELLO_PLACE_SUBMENU;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_HELP;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_NEGATIVE_ANSWER_WITHOUT_CONTEXT;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_UNKNOWN;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_WEB_REQ_FESTIVAL_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_BUTTON_CURRENT_LOCATION_USE;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_BUTTON_DESTINATION_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_BUTTON_HELLO;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_BUTTON_MENU;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_BUTTON_MENU_DETAIL_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_BUTTON_PLACE_SEARCH_RESULT;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_BUTTON_PLACE_SUBMENU;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_UNKNOWN;
import static com.chat.seoul.here.module.lib.conf.Common.UI_IMAGE;
import static com.chat.seoul.here.module.lib.conf.Common.UI_IMAGE_BUTTON;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_BUTTON;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_BUTTON_IMAGE;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_CARD_BUTTON;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_CARD_BUTTON_IMAGE;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_CARD_IMAGE;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_IMAGE;

/**
 * Created by brsingh on 1/6/2017.
 */
public class ChatAdapter extends BaseAdapter implements ViewPagerAdapterButtonListener{

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private final ArrayList<ChatMessage> chatMessages;
    private Activity context;
    private ChatAdapterButtonListener listener;
    //viewpager
    ViewPagerAdapter mViewPagerAdapter;
    private int viewType = 0;
    private int selectedIndex;

    public ChatAdapter(Activity context, ArrayList<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.listener = (ChatAdapterButtonListener) context;
    }

    public ArrayList<ChatMessage> getAllItems()
    {
        return chatMessages;
    }

    public void removeTypingIndicator()
    {
        int length = chatMessages.size();
        if(length < 1){
            return;
        }
        ChatMessage message = chatMessages.get(length-1);
        if("typing".equals(message.getType()))
        {
            chatMessages.remove(length - 1);
        }

    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //button의 개수를 파악한다.
/*        int buttonNum = ( chatMessage.getAttachments() != null) ? chatMessage.getAttachments().getButtonTitle().size() : 0;
        int imgNum = ( chatMessage.getAttachments() != null && chatMessage.getAttachments().getContentImageList() != null) ?
                chatMessage.getAttachments().getContentImageList().size() : 0;*/


        if (convertView == null) {
            System.out.println(">>>converView is null");
            //convertView = vi.inflate(R.layout.list_item_chat_message, null);
            convertView = vi.inflate(R.layout.list_item_chat_message, null);
            holder = createViewHolder(convertView);

            //뷰 페이저가 존재하는지 확인한다.
            /*if(chatMessage.isViewPager())
            {
                ViewPager vp = (ViewPager) convertView.findViewById(R.id.listViewPager);
                if (selectedIndex != -1 && position == selectedIndex) {
                    vp.setBackgroundColor(Color.RED);

                } else {

                    vp.setBackgroundColor(Color.YELLOW);
                }
                holder.viewPager = vp;
            }*/

            holder.chatMessageIdx = position;
            convertView.setTag(holder);

        } else {
            System.out.println(">>>converView is not null");
            holder = (ViewHolder) convertView.getTag();
            removeButtonList(holder);
            removeMessageImage(holder);
            removeTypingIndicator(holder);

            try {
                /*if (!chatMessage.isViewPager()) {
                    //remove 하자
                    ViewPager vp = (ViewPager) convertView.findViewById(R.id.listViewPager);
                    vp.removeAllViews();
                    holder.content.removeView(vp);
                    //holder.viewPager = null;
                } else {
                    holder.viewPager.setVisibility(View.VISIBLE);
                }*/
                /*ViewPager vp = (ViewPager) convertView.findViewById(R.id.listViewPager);
                vp.removeAllViews();*/
                holder.content.removeView(holder.viewPager);
            }catch(NullPointerException e)
            {
                e.printStackTrace();
            }
        }

        System.out.println(">>>>>Start position : " + position + ", isViewPager : " + chatMessage.isViewPager() + ", responseType : " + chatMessage.getActionType() + ", chatMessage : " + chatMessage.toString());
        if(chatMessage.getPlaceModelArrayList() != null) {
            for (int i = 0; i < chatMessage.getPlaceModelArrayList().size(); i++) {
                chatMessage.getPlaceModelArrayList().get(i).toString();
            }
        }

        createMessageImage(chatMessage, holder);      //이미지를 생성한다.
        createButtonList(chatMessage, holder);        //버튼을 생성한다.

        /*if (imgNum > 0) {
            createMessageImage(imgNum, holder);
        }*/

        boolean myMsg = chatMessage.getIsme();//Just a dummy check
        setAlignment(holder, myMsg, chatMessage.getType());
        String holderString = chatMessage.getMessage();     //chat message text
        if (Build.VERSION.SDK_INT >= 24) {

            holder.txtMessage.setText(Html.fromHtml(holderString, Html.FROM_HTML_MODE_LEGACY));
        } else {

            holder.txtMessage.setText(Html.fromHtml(holderString));
        }
        holder.txtInfo.setText(chatMessage.getDate());

        //20170418 - JJW - button
        //holder
        // 해당 부분에서 뷰페이저가 새롭게 만들어지는 현상 발생.. 여기 추후 수정 필요
        if(chatMessage.isViewPager()) {
            System.out.println(">>>ViewPager Dynamically Creating..");

            if(chatMessage.getChatMessageShowViewPager() == null)
            {
                holder.viewPager = new android.support.v4.view.ViewPager(context);
                holder.viewPager.setClipToPadding(false);           // false인 경우, 부분적으로 보이도록 해준다.
                holder.viewPager.setId(generateViewId());
                //px ==> dp로 변환
                int cpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());

                //20171010 - 뷰페이저를 카드뷰 형태로 변경하면서, Height를 Wrap Content로 변경
                // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 320);
                //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 400);
                int height = 0;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    System.out.println(">>>>>>>>>>>This is 7.0");
                    height = (int) (3800 / context.getResources().getSystem().getDisplayMetrics().density);

                }else
                {
                    System.out.println(">>>>>>>>>>>Lower than 7.0");
                    //height = (int) (800 / context.getResources().getSystem().getDisplayMetrics().density);
                    height = 400;
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
               //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

/*
                params.topMargin = 0;
                params.leftMargin = 0;
                params.rightMargin = 0;*/

                holder.viewPager.setLayoutParams(params);
                holder.content.addView(holder.viewPager);

                try {
                    //명소 검색 인경우..
                    if(chatMessage.getPLACE_TYPE() == CHAT_MESSAGE_PLACE) {
                        ArrayList<PlaceModel> placeModelList = chatMessage.getPlaceModelArrayList();
                        ViewPagerAdapter viewPagerAdapter = null;
                        if (chatMessage.getChatMessageViewPager() == null) {
                            System.out.println(">>h1");
                            viewPagerAdapter = new ViewPagerAdapter(placeModelList, context, this, CHAT_MESSAGE_PLACE);
                            chatMessage.setChatMessageViewPager(viewPagerAdapter);
                        } else {
                            System.out.println(">>h2");
                            viewPagerAdapter = chatMessage.getChatMessageViewPager();
                        }
                        holder.viewPager.setAdapter(viewPagerAdapter);

                    }else
                    {
                        //문화 행사 검색인 경우..
                        ArrayList<FestivalModel> festivalModelArrayList = chatMessage.getFestivalModelArrayList();
                        ViewPagerAdapter viewPagerAdapter = null;
                        if (chatMessage.getChatMessageViewPager() == null) {
                            System.out.println(">>h1");
                            viewPagerAdapter = new ViewPagerAdapter(festivalModelArrayList, context, this, CHAT_MESSAGE_FESTIVAL);
                            chatMessage.setChatMessageViewPager(viewPagerAdapter);
                        } else {
                            System.out.println(">>h2");
                            viewPagerAdapter = chatMessage.getChatMessageViewPager();
                        }
                        holder.viewPager.setAdapter(viewPagerAdapter);
                    }

                    chatMessage.setChatMessageShowViewPager(holder.viewPager);
                }catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }else
            {
                holder.viewPager = chatMessage.getChatMessageShowViewPager();
                holder.content.addView(holder.viewPager);
            }
        }

        if ("typing".equals(chatMessage.getType())) {
            //TypeIndicating 한다..
            createTypingIndicator("..", holder);
        }

        return convertView;
    }

    public void refreshAdapter()
    {
        notifyDataSetChanged();
    }

    public void add(ChatMessage message) {
        //마지막에 Typing인지 확인한다.
        if(chatMessages != null && chatMessages.size() > 0)
        {
            removeTypingIndicator();
        }
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        if(messages != null)
            chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe,  String type) {
        if (!isMe) {

            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            //레이아웃 속성
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            //holder.contentWithBG.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            holder.contentWithBG.setLayoutParams(layoutParams);

            //릴레이티브 레이아웃 속성
            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);

            //viewpager

        } else {


           holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);


            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);


        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        //holder.viewPager = (ViewPager) v.findViewById(R.id.listViewPager);

        return holder;
    }


    /**
     *  채팅 이미지를 생성한다..
     * @param chatMessage
     * @param holder
     */
    private void createMessageImage(final ChatMessage chatMessage, final ViewHolder holder)
    {

        int imgNum = 0;
        ArrayList<Drawable> imageResourceList = null;

        //UI 타입을 체크 한다.
        int messageUiType = chatMessage.getMessageUiType();

        System.out.println(">>message : " + chatMessage.getMessage() + ", ui type : " + chatMessage.getMessageUiType() + ", actionType : " + chatMessage.getActionType() + ", intent : " + chatMessage.getMeesageIntentType());
        //우선 UI가 텍스트 + 버튼 또는 텍스트 + 카드 + 버튼 인 경우를 체크한다.
        if(messageUiType == UI_IMAGE || messageUiType == UI_IMAGE_BUTTON || messageUiType == UI_TEXT_BUTTON_IMAGE || messageUiType == UI_TEXT_IMAGE || messageUiType == UI_TEXT_CARD_IMAGE || messageUiType == UI_TEXT_CARD_BUTTON_IMAGE) {

            //Intent를 체크한다..
            imageResourceList = new ArrayList<>();

            if (chatMessage.getMeesageIntentType() == INTENT_UNKNOWN) {
                imgNum = 1;
                imageResourceList.add(context.getResources().getDrawable(R.drawable.hello_bot));
            }


            if (imgNum > 0) {
                if (holder.imgContentList == null) {
                    holder.imgContentList = new ImageView[imgNum];

                    for (int i = 0; i < imgNum; i++) {
                        ImageView tmp = new ImageView(context);
                        //tmp.setBackgroundResource(R.drawable.border_rectangle_transparent_button);
                        tmp.setMaxHeight(240);
                        tmp.setMaxWidth(240);
                        tmp.setScaleType(ImageView.ScaleType.FIT_XY);
                        tmp.setImageDrawable(imageResourceList.get(i));
                        holder.contentWithBG.addView(tmp);
                        holder.imgContentList[i] = tmp;
                    }
                }
            }
        }
    }

    private void removeMessageImage(ViewHolder holder)
    {

        if(holder.contentWithBG.getChildCount() > 1){
            //holder.contentWithBG.removeAllViews();
            if(holder.imgContentList != null) {
                for (int i = 0; i < holder.imgContentList.length; i++) {
                    holder.contentWithBG.removeView(holder.imgContentList[i]);

                }
                holder.imgContentList = null;
            }
        }
    }

    private void removeViewPager(ViewHolder holder, View view)
    {
/*
        if(holder.viewPager != null){
            //holder.contentWithBG.removeAllViews();
            for (int i = 0; i < view.
            holder.viewPager.removeView();
            if(holder.imgContentList != null) {
                for (int i = 0; i < holder.imgContentList.length; i++) {
                    holder.contentWithBG.removeView(holder.imgContentList[i]);

                }
                holder.imgContentList = null;
            }
        }*/
    }

    //UI 타입에 따라서 버튼을 생성한다.
    private void createButtonList(final ChatMessage chatMessage, final ViewHolder holder)
    {
        //버튼을 생성한다..
        int buttonNum = 0;
        ArrayList<String> messageList = null;
        HashMap<String, String> hButtonList = new HashMap<>();
        //UI 타입을 체크 한다.
        int messageUiType = chatMessage.getMessageUiType();
        System.out.println(">>message : " + chatMessage.getMessage() + ", ui type : " + chatMessage.getMessageUiType() + ", actionType : " + chatMessage.getActionType() + ", intent : " + chatMessage.getMeesageIntentType());
        //우선 UI가 텍스트 + 버튼 또는 텍스트 + 카드 + 버튼 인 경우를 체크한다.
        if(messageUiType == UI_TEXT_BUTTON || messageUiType == UI_TEXT_CARD_BUTTON || messageUiType == UI_IMAGE_BUTTON || messageUiType == UI_TEXT_BUTTON_IMAGE || messageUiType == UI_TEXT_CARD_BUTTON_IMAGE)
        {
            //Intent를 체크한다..
            if (chatMessage.getMeesageIntentType() == INTENT_WEB_REQ_FESTIVAL_SEARCH) {
                buttonNum = 2;
                messageList.add("행사찾기");
                messageList.add("아니요");
            }
            /**
             *  HELLO Intent 인경우 버튼 생성
             */
            else if(chatMessage.getMeesageIntentType() == INTENT_HELLO || chatMessage.getMeesageIntentType() == INTENT_HELP)
            {
                messageList = generateButtonText(MSG_BUTTON_HELLO);
                buttonNum = messageList.size();
            }else if(chatMessage.getMeesageIntentType() == INTENT_UNKNOWN)
            {
                messageList = generateButtonText(MSG_UNKNOWN);
                buttonNum = messageList.size();
            }
            /**
             *  현재 위치 주변 명소 찾기 요청 시, 현재 위치를 사용할 것인가를 확인한다.
             */
            else if(chatMessage.getMeesageIntentType() == INTENT_CURRENT_PLACE_SEARCH)
            {
                messageList = generateButtonText(MSG_BUTTON_CURRENT_LOCATION_USE);
                buttonNum = messageList.size();
            }
            /**
             *  행사 정보를 물어보고 끝난 경우..
             */
            else if(chatMessage.getMeesageIntentType() == INTENT_END || chatMessage.getMeesageIntentType() == INTENT_NEGATIVE_ANSWER_WITHOUT_CONTEXT)
            {
                if(chatMessage.getActionType() == ACTION_RECOMMEND_PLACE_SEARCH)
                {
                    messageList = generateButtonText(MSG_BUTTON_MENU_DETAIL_SEARCH);
                    buttonNum = messageList.size();
                }else {
                    messageList = generateButtonText(MSG_BUTTON_MENU);
                    buttonNum = messageList.size();
                }
            }
            /**
             *  명소 검색 서브 메뉴 버튼
             */
            else if(chatMessage.getMeesageIntentType() == INTENT_HELLO_PLACE_SUBMENU)
            {
                messageList = generateButtonText(MSG_BUTTON_PLACE_SUBMENU);
                buttonNum = messageList.size();
            }
            else
            {
                if(chatMessage.getActionType() == ACTION_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE || chatMessage.getActionType() == ACTION_PATH_SEARCH_NO_ENTITY_INCOMPLETE)
                {
                    //hButtonList.put("장소 검색하기", "검색하기");
                    messageList = generateButtonText(MSG_BUTTON_DESTINATION_SEARCH);
                    buttonNum = messageList.size();
                }
                /**
                 *  현재 위치 찾기, 목적지 찾기(대화, 싱글톤) 의 결과로 행사 검색에 대한 질문
                 */
                else if(chatMessage.getActionType() == ACTION_INPUT_LATLON || chatMessage.getActionType() == ACTION_DEST_PLACE_SEARCH || chatMessage.getActionType() == ACTION_DEST_PLACE_SEARCH_NO_ENTITY)
                {
                    messageList = generateButtonText(MSG_BUTTON_PLACE_SEARCH_RESULT);
                    buttonNum = messageList.size();
                }
            }

            if (holder.btnContentList == null) {
                holder.btnContentList = new Button[buttonNum];

                for (int i = 0; i < buttonNum; i++) {

                    /**
                     * 버튼 스타일 적용
                    Button tmp = new Button(context);
                    tmp.setBackgroundResource(R.drawable.border_round_button);
                     **/

                    Button tmp = new Button(context);
                    tmp.setBackgroundResource(R.drawable.border_rectangle_blue_button);
                    tmp.setMaxWidth(240);
                    tmp.setTextColor(Color.WHITE);
                    //tmp.setTextColor(context.getResources().getColor(R.color.font_color));

                    //int height = convertPixelToDps(120);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.topMargin = 2;
                    params.leftMargin = 40;
                    params.rightMargin = 40;
                    /*tmp.setPadding(-10, -10, -10, -10);*/

                    tmp.setLayoutParams(params);
                    tmp.setIncludeFontPadding(false);
                    //tmp.setPadding(0, 2, 0, 2);
                    //tmp.setPadding(0, 10, 10, 0);
                    holder.content.addView(tmp);
                    final int index = i;
                    holder.btnContentList[i] = tmp;
                    holder.btnContentList[i].setText(messageList.get(i));

                    //버튼 클릭시 동작
                    holder.btnContentList[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println(">>>>>Holder Button Clicked..");

                            //목적지 검색 요청 시, 목적지를 '검색하기' 버튼 클릭 시 동작 리스너
                            if ("검색하기".equals(holder.btnContentList[index].getText().toString())) {
                                listener.onChatAdapterFindPlace();
                            }
                            //명소 검색하기 클릭 시..
                            else if (MSG_BUTTON_HELLO[0].equals(holder.btnContentList[index].getText().toString())) {
                                listener.onChatAdapterResponsePlaceSubButton();
                            }
                            //서브 메뉴 버튼 클릭 시...
                            else if (MSG_BUTTON_PLACE_SUBMENU[0].equals(holder.btnContentList[index].getText().toString())) {
                                listener.onChatAdapterSendMessage(MSG_BUTTON_PLACE_SUBMENU[0]);
                            }
                            //현재 위치 주변 명소
                            else if (MSG_BUTTON_PLACE_SUBMENU[1].equals(holder.btnContentList[index].getText().toString())) {
                                listener.onChatAdapterSendMessage(MSG_BUTTON_PLACE_SUBMENU[1]);
                            }
                            //현재 위치 주변 명소
                            else if (MSG_BUTTON_PLACE_SUBMENU[2].equals(holder.btnContentList[index].getText().toString())) {
                                listener.onChatAdapterSendMessage(MSG_BUTTON_PLACE_SUBMENU[2]);
                            }
                            //도착지 주변 명소
                            else if (MSG_BUTTON_PLACE_SUBMENU[3].equals(holder.btnContentList[index].getText().toString())) {
                                listener.onChatAdapterSendMessage(MSG_BUTTON_PLACE_SUBMENU[3]);
                            }
                            else if ("출도착지 주변 찾기".equals(holder.btnContentList[index].getText().toString())) {
                                listener.onChatAdapterFindOriginDestPathPlace();
                            }else
                            {
                                //나머지 버튼 클릭 시시
                                listener.onChatAdapterButtonClicked(holder.btnContentList[index].getText().toString(), chatMessage.getMeesageIntentType());
                            }

                        }
                    });
                }
            }
        }else
        {
            holder.btnContentList = null;
        }
    }

    private void createTypingIndicator(String text, ViewHolder holder)
    {

        if (holder.typeWriter == null) {
           // holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);
            //holder.contentWithBG.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(15,50);
            //tv.setLayoutParams(Params1);

            holder.typeWriter = new TypeWriter(context);
           // holder.typeWriter.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
            holder.typeWriter.setTextColor(context.getResources().getColor(R.color.font_color));
            holder.typeWriter.setTextSize(15);
            holder.typeWriter.setWidth(240);
            holder.typeWriter.setMaxWidth(240);
            holder.typeWriter.setText("");
            holder.typeWriter.setCharacterDelay(150);
            holder.typeWriter.animateText("봇이 입력 중입니다..");
            //텍스트 뷰의 크기를 고정 시키자...
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.typeWriter.setLayoutParams(params);

            holder.contentWithBG.addView(holder.typeWriter);
        }else
        {
            holder.typeWriter = null;

        }
    }

    private void removeTypingIndicator(ViewHolder holder)
    {

        if(holder.contentWithBG.getChildCount() > 1) {

            if(holder.typeWriter != null) {
                holder.contentWithBG.removeView(holder.typeWriter);
                holder.typeWriter = null;
            }

        }

    }


    private void removeButtonList(ViewHolder holder)
    {

        if(holder.btnContentList != null) {
            if (holder.content.getChildCount() > 1) {
                //holder.contentWithBG.removeAllViews();
                if (holder.content != null) {
                    for (int i = 0; i < holder.btnContentList.length; i++) {
                        holder.content.removeView(holder.btnContentList[i]);

                    }
                    holder.btnContentList = null;
                }
            }
        }
    }

    public void removeAllItems() {
        chatMessages.clear();
        notifyDataSetChanged();
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }



    /**
     * Generate a value suitable for use in .
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    //전체 보기 클릭 시
    @Override
    public void onViewPagerAdapterAllListPlaceButtonClicked(ArrayList<PlaceModel> viewPagerPlaceItems) {
        //ChatAdapter에 있는 ViewPagerButton 클릭시 호출된다.
        System.out.println(">>>>onViewPagerListPlaceButton Clicked..");
         listener.onChatAdapterViewPagerAdapterAllListPlaceButtonClicked(viewPagerPlaceItems);
    }

    //자세히 보기 클릭 시
    @Override
    public void onViewPagerAdapterPlaceDetailButtonClicked(PlaceModel viewPagerPlaceItem) {

        listener.onChatAdapterViewPagerAdapterPlaceDetailButtonClicked(viewPagerPlaceItem);

    }

    //행사 정보 상세 보기 클릭
    @Override
    public void onViewPagerAdapterFestivalDetailButtonClicked(FestivalModel festivalModel) {
        listener.onChatAdapterViewPagerAdapterFestivalDetailButtonClicked(festivalModel);
    }

    //행사 정보 전체 보기 클릭
    @Override
    public void onViewPagerAdapterAllListFestivalButtonClicked(ArrayList<FestivalModel> viewPagerFestivalItems) {
        listener.onChatAdapterViewPagerAdapterAllListFestivalButtonClicked(viewPagerFestivalItems);
    }


    private ArrayList<String> generateButtonText(String[] strings)
    {
        ArrayList<String> stringArrayList = new ArrayList<String>();
        for(int i = 0 ; i < strings.length ; i++)
        {
            stringArrayList.add(strings[i]);
        }

        return stringArrayList;
    }

    private int convertDpsToPixel()
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        //int pixels = (int) (dps * scale + 0.5f);
        return 0;
    }

    private int convertPixelToDps(int px)
    {
        return (int) (px / context.getResources().getSystem().getDisplayMetrics().density);
    }

    //View Holder
    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        //20170418 - JJW - Button
        public Button btnContentList[] = null;
        public int chatMessageIdx;
        //20170418 - JJW - image
        public ImageView imgContentList[] = null;
        //20170529 - JJW - Typing Indicator
        public TypeWriter typeWriter;
        //ViewPager
        public ViewPager viewPager;

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "txtMessage=" + txtMessage.getText() +
                    '}';
        }
    }

}