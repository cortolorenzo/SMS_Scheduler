package com.example.sms_scheduler_final;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GetXML extends AsyncTask<String, Void, ArrayList<Messages>> {

    //List<Messages> messagesAR = new ArrayList<>()
    ArrayList<Messages> messagesAR = null;

    @Override
    public ArrayList<Messages> doInBackground(String... strings) {

        String TAG = "text";
        String xmlstring = "";



        try {

            Log.d(TAG, "hej ho");
            URL url = new URL("https://mmiszkurka.pl/MsgList.xml");
            URLConnection conn = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());


            NodeList nodes = doc.getElementsByTagName("MsgList");
            messagesAR = new ArrayList<>(nodes.getLength());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);

                NodeList msgtxt = element.getElementsByTagName("MsgTxt");
                Element msgtxtCNT = (Element) msgtxt.item(0);
                String _msgtxt = msgtxtCNT.getTextContent();

                NodeList msgGroup = element.getElementsByTagName("GroupID");
                Element msgGroupCNT = (Element) msgGroup.item(0);
                String _msgGroup = msgGroupCNT.getTextContent();

                NodeList msgID = element.getElementsByTagName("ID");
                Element msgIDCNT = (Element) msgID.item(0);
                String _msgID = msgIDCNT.getTextContent();

                xmlstring = _msgGroup + " " + _msgtxt;
                Log.d(TAG, xmlstring);
                //Thread.sleep(1000);

                Messages message = new Messages(Integer.parseInt(_msgID), _msgtxt, Integer.parseInt(_msgGroup), 0);
                messagesAR.add(message);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return messagesAR;
    }




}
