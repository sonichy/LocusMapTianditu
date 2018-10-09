package com.hty.locusmaptianditu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Log;

import com.tianditu.android.maps.GeoPoint;

public class RWXML {
    static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    static double distance;
    static int seconds, h, m, s;
    static DecimalFormat DF1 = new DecimalFormat("0.0");

    static void create(String time) {
        String fp = Environment.getExternalStorageDirectory().getPath() + "/LocusMap/";
        String fn = time + "TD.gpx";
        String fpn = fp + fn;
        // Log.e("xmlFile", fpn);
        File filepath = new File(fp);
        if (!filepath.exists()) {
            filepath.mkdirs();
        }
        File file = new File(fpn);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Log.e("RWXML", "createFile");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("RWXML:create", "IOException");
            }
        }
        StringBuilder sb = new StringBuilder(time);
        sb.insert(4, "-");
        sb.insert(7, "-");
        sb.insert(10, " ");
        sb.insert(13, ":");
        sb.insert(16, ":");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(fpn, true);
            bw = new BufferedWriter(fw);
            String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gpx version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><metadata><starttime>" + sb + "</starttime><endtime>" + sb + "</endtime><distance>0</distance><duration>00:00:00</duration><maxspeed>0</maxspeed></metadata><trk><trkseg></trkseg></trk></gpx>";
            bw.write(content);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    static void add(String filename, String time, String latitude, String longitude, String distance, String duration) {
        String fp = Environment.getExternalStorageDirectory().getPath() + "/LocusMap/" + filename;
        //String fn = filename + ".gpx";
        //String fpn = fp + fn;
        DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
        DocumentBuilder DB = null;
        try {
            DB = DBF.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = DB.parse(new FileInputStream(fp));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().getElementsByTagName("endtime").item(0).setTextContent(time);
        doc.getDocumentElement().getElementsByTagName("distance").item(0).setTextContent(distance);
        doc.getDocumentElement().getElementsByTagName("duration").item(0).setTextContent(duration);
        Element Etrkpt = doc.createElement("trkpt");
        Attr attr = doc.createAttribute("lat");
        attr.setValue(latitude);
        Etrkpt.setAttributeNode(attr);
        Attr attr2 = doc.createAttribute("lon");
        attr2.setValue(longitude);
        Etrkpt.setAttributeNode(attr2);
        Element Etime = doc.createElement("time");
        Etime.setTextContent(time);
        Etrkpt.appendChild(Etime);
        doc.getDocumentElement().getElementsByTagName("trkseg").item(0).appendChild(Etrkpt);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fp));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    static String[] gpxlist() {
        String fp = Environment.getExternalStorageDirectory().getPath() + "/LocusMap/";
        File file = new File(fp);
        if (!file.exists()) {
            String[] m = {"目录不存在"};
            return m;
        } else {
            String[] names = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(".gpx");
                }
            });
            if (names.length == 0) {
                String[] m = {"没有轨迹文件"};
                return m;
            } else {
                java.util.Arrays.sort(names, Collections.reverseOrder());
                return names;
            }
        }
    }

    static ArrayList<GeoPoint> read(String filename) {
        String starttime = "", endtime = "", sdistance = "", sduration = "", info = "";
        MainApplication.setmsg(info);
        ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
        String fp = Environment.getExternalStorageDirectory().getPath() + "/LocusMap/" + filename;
        DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
        DocumentBuilder DB = null;
        try {
            DB = DBF.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = DB.parse(new FileInputStream(fp));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            starttime = doc.getDocumentElement().getElementsByTagName("starttime").item(0).getFirstChild().getTextContent();
        } catch (Exception e) {
        }
        try {
            endtime = doc.getDocumentElement().getElementsByTagName("endtime").item(0).getFirstChild().getTextContent();
        } catch (Exception e) {
        }
        try {
            sdistance = doc.getDocumentElement().getElementsByTagName("distance").item(0).getFirstChild().getTextContent();
            distance = Double.parseDouble(sdistance);
            sdistance = sdistance.substring(0, sdistance.indexOf("."));
        } catch (Exception e) {
        }
        try {
            sduration = doc.getDocumentElement().getElementsByTagName("duration").item(0).getFirstChild().getTextContent();
            h = Integer.parseInt(sduration.substring(0, 2));
            m = Integer.parseInt(sduration.substring(3, 5));
            s = Integer.parseInt(sduration.substring(6));
            seconds = h * 3600 + m * 60 + s;
        } catch (Exception e) {
        }
        String v = "?";
        if (seconds != 0)
            v = DF1.format(distance / seconds);
        Element root = doc.getDocumentElement();
        NodeList NL = root.getElementsByTagName("trkpt");
        int NLL = NL.getLength();
        // Log.e("trkpt length", NLL + "");
        if (NLL > 0) {
            info = filename + "\n开始：" + starttime + "\n结束：" + endtime + "\n路程：" + sdistance + " 米\n时长：" + sduration + " ( " + seconds + " 秒)\n平均速度：" + v + " 米/秒\n" + NLL + " 个点";
            MainApplication.setmsg(info);
            for (int i = 0; i < NLL; i++) {
                Element element = (Element) NL.item(i);
                GeoPoint p = new GeoPoint((int) (Double.parseDouble(element.getAttribute("lat")) * 1E6), (int) (Double.parseDouble(element.getAttribute("lon")) * 1E6));
                points.add(p);
            }
            return points;
        } else {
            return null;
        }
    }

    static void del(String filename) {
        String fp = Environment.getExternalStorageDirectory().getPath() + "/LocusMap/" + filename;
        File file = new File(fp);
        file.delete();
        MainApplication.setfn("");
        Log.e("del", fp);
    }

    static void append(String filename, String conent) {
        String fp = Environment.getExternalStorageDirectory().getPath() + "/LocusMap/" + filename;
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fp, true)));
            Date date = new Date();
            out.write(SDF.format(date) + ": " + conent + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}