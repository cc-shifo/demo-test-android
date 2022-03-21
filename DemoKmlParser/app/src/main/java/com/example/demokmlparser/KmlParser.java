/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20220119 	         LiuJian                  Create
 */

package com.example.demokmlparser;


import android.graphics.Point;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class KmlParser {
    public static final String PLACE_MARK = "Placemark";
    public static final String PLACE_MARK_NAME = "name";
    public static final String LINE = "LineString";
    public static final String POLYGON = "Polygon";
    public static final String OUTER_BOUNDARY_IS = "outerBoundaryIs";
    public static final String POINT = "Point";
    public static final String COORDINATES = "coordinates";
    private String mPlaceMarkName;
    private int mGeometryType;
    private List<Point> mPointList;


    public KmlParser() {
    }

    public List<KmlEntry> pull2xml(InputStream inputStream) throws Exception {
        mPlaceMarkName = null;
        mGeometryType = KmlEntry.GeometryType.NONE;
        mPointList = new ArrayList<>(0);

        List<KmlEntry> kmlEntryList = new ArrayList<>(0);
        // XmlPullParser parser = Xml.newPullParser();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, "utf-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && PLACE_MARK.equals(parser.getName())) {
                readPlaceMark(parser);
                if (parser.getEventType() == XmlPullParser.END_TAG && PLACE_MARK.equals(parser.getName())
                        && isValidItem()) {
                    kmlEntryList.add(new KmlEntry(mPlaceMarkName, mGeometryType, mPointList));
                }
            }

            eventType = parser.next();
        }
        return kmlEntryList;
    }

    private boolean isValidItem() {
        return (mGeometryType != KmlEntry.GeometryType.NONE
                && mGeometryType != KmlEntry.GeometryType.POINT)
                && (mPointList != null && !mPointList.isEmpty());
    }

    private void readPlaceMark(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.next();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT
                && (eventType != XmlPullParser.END_TAG || !PLACE_MARK.equals(parser.getName()))) {
            if (eventType == XmlPullParser.START_TAG) {
                readGeometry(parser);
            }

            eventType = parser.next();
        }
    }

    private void readPlaceMarkName(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.next();
        mPlaceMarkName = parser.getText();
    }

    private void readGeometry(XmlPullParser parser) throws IOException, XmlPullParserException {
        String tag = parser.getName();
        if (PLACE_MARK_NAME.equals(tag)) {
            readPlaceMarkName(parser);
        } else if (LINE.equals(tag)) {
            readLine(parser);
        } else if (POLYGON.equals(tag)) {
            readPolygon(parser);
        } else if (POINT.equals(tag)) {
            readPoint(parser);
        }
    }

    private void readLine(XmlPullParser parser) throws IOException, XmlPullParserException {
        readCoordinates(parser);
        if (mPointList.size() >= 2) {
            mGeometryType = KmlEntry.GeometryType.LINE;
        } else {
            mPointList.clear();
        }

    }

    private void readPolygon(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT && (eventType != XmlPullParser.END_TAG
                || !OUTER_BOUNDARY_IS.equals(parser.getName()))) {
            if (eventType == XmlPullParser.START_TAG && OUTER_BOUNDARY_IS.equals(parser.getName())) {
                readOuterBoundary(parser);
            }

            eventType = parser.next();
        }
    }

    private void readOuterBoundary(XmlPullParser parser) throws IOException, XmlPullParserException {
        readCoordinates(parser);
        if (mPointList.size() >= 3) {
            mGeometryType = KmlEntry.GeometryType.POLYGON;
        } else {
            mPointList.clear();
        }
    }

    private void readPoint(XmlPullParser parser) {
       // nothing
    }

    private void readCoordinates(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT
                && (eventType != XmlPullParser.END_TAG || !COORDINATES.equals(parser.getName()))) {
            if (eventType == XmlPullParser.START_TAG && COORDINATES.equals(parser.getName())) {
                eventType = parser.next();
                if (eventType == XmlPullParser.TEXT) {
                    processCoordinate(parser.getText());
                }
            }

            eventType = parser.next();
        }
    }

    private void processCoordinate(String text) {
        if (!TextUtils.isEmpty(text)) {
            text = text.replaceAll("(^\\s+)|([\\f|\\t| ]+)", "");
            text = text.replaceAll("[\\r|\\n]+", "\n");

            String[] coordinates = text.split("\\n");
            for (String s : coordinates) {
                String[] coordinate = s.split(",");
                if (coordinate.length >= 2) {
                    mPointList.add(new Point((int) Double.parseDouble(coordinate[0]),
                            (int) Double.parseDouble(coordinate[1])));
                }
            }
        }
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                default:
                    break;
            }
        }
    }

}
