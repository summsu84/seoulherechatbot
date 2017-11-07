package com.chat.seoul.here.module.lib.db;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;

import com.chat.seoul.here.module.model.ChatMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by JJW on 2017-04-27.
 */

public class FileClient {

    FileOutputStream outputStream;
    private File file;
    private static FileClient instance;
    private FileOutputStream out;
    private FileInputStream in;

    public static FileClient getInstance() {
        if (instance == null) {
            instance = new FileClient();
        }
        return instance;
    }

    public void writeUserContext(Context context, String filename, String mJsonResponse)
    {

        try {
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + filename);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    public String getData(Context context, String fileName)
    {
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + fileName);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    public void writeChatMessage(Context context, String filename, ArrayList<ChatMessage> messages)
    {
        file = new File(context.getFilesDir().getPath() + "/" + filename);
        try
        {
            out = new FileOutputStream(file);
            writeJsonStream(out, messages);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void writeJsonStream(OutputStream out, ArrayList<ChatMessage> messages) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeMessagesArray(writer, messages);
        writer.close();
    }

    public void writeMessagesArray(JsonWriter writer, ArrayList<ChatMessage> messages) throws IOException {
        writer.beginArray();
        for (ChatMessage message : messages) {
            writeMessage(writer, message);
        }
        writer.endArray();
    }

    public void writeMessage(JsonWriter writer, ChatMessage message) throws IOException {
        writer.beginObject();
//        writer.name("id").value(message.getId());
        writer.name("isMe").value(message.getIsme());
        writer.name("message").value(message.getMessage());
        writer.name("dateTime").value(message.getDate());

        writer.endObject();
    }



    public void writeButtonArray(JsonWriter writer, ArrayList<String> stringArrayList) throws IOException {
        writer.beginArray();

        for (String value : stringArrayList) {
            writer.value(value);
        }
        writer.endArray();
    }





    //Reader
    public ArrayList<ChatMessage> readChatMessage(Context context, String filename)
    {
        file = new File(context.getFilesDir().getPath() + "/" + filename);
        ArrayList<ChatMessage> retChatMessage = null;
        try
        {
            in = new FileInputStream(file);
            retChatMessage = readJsonStream(in);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return retChatMessage;
    }

    public ArrayList<ChatMessage> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public ArrayList<ChatMessage> readMessagesArray(JsonReader reader) throws IOException {
        ArrayList<ChatMessage>  messages = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    public ChatMessage readMessage(JsonReader reader) throws IOException {
        long id = -1;
        boolean isMe = false;
        String message = null;
        String dateTime = null;
        String isSpeech = null;



        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("isMe")) {
                isMe = reader.nextBoolean();
            } else if (name.equals("message")) {
                message = reader.nextString();
            }else if (name.equals("dateTime")) {
                dateTime = reader.nextString();
            }else if (name.equals("message")) {
                isSpeech = reader.nextString();
            }else if (name.equals("attachments") && reader.peek() != JsonToken.NULL){

            }

            /*else if (name.equals("geo") && reader.peek() != JsonToken.NULL) {
                geo = readDoublesArray(reader);
            } else if (name.equals("user")) {
                user = readUser(reader);
            } else {
                reader.skipValue();
            }*/
        }
        reader.endObject();
        return new ChatMessage(isMe, message, dateTime, false);
    }




    public ArrayList<String> readButtonArray(JsonReader reader) throws IOException {
        ArrayList<String> stringArrayList = new ArrayList<String>();

        reader.beginArray();
        while (reader.hasNext()) {
            stringArrayList.add(reader.nextString());
        }
        reader.endArray();
        return stringArrayList;
    }

    /*public List<Double> readDoublesArray(JsonReader reader) throws IOException {
        List<Double> doubles = new ArrayList<Double>();

        reader.beginArray();
        while (reader.hasNext()) {
            doubles.add(reader.nextDouble());
        }
        reader.endArray();
        return doubles;
    }

    public User readUser(JsonReader reader) throws IOException {
        String username = null;
        int followersCount = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                username = reader.nextString();
            } else if (name.equals("followers_count")) {
                followersCount = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new User(username, followersCount);
    }*/

    public boolean checkFileExist(Context context, String filename) {

        File files = new File(context.getFilesDir().getPath() + "/" + filename);
        //파일 유무를 확인합니다.
        if (files.exists() == true) {
            //파일이 있을시
            return true;
        } else {
            //파일이 없을시
            return false;
        }
    }

    public void deleteFile(Context context, String s) {
        if(checkFileExist(context, s))
        {
            File file = new File(context.getFilesDir().getPath() + "/" + s);
            file.delete();
        }
    }
}
