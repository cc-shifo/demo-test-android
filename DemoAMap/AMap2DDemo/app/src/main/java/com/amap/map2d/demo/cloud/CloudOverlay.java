package com.amap.map2d.demo.cloud;


import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.cloud.CloudItem;

import java.util.ArrayList;
import java.util.List;


public class CloudOverlay {
	private final List<CloudItem> mPois;
	private final AMap mAMap;
	private final ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

	public CloudOverlay(AMap amap, List<CloudItem> pois) {
		mAMap = amap;
		mPois = pois;
	}

	public void addToMap() {
		for (int i = 0; i < mPois.size(); i++) {
			Marker marker = mAMap.addMarker(getMarkerOptions(i));
			marker.setObject(i);
			mPoiMarks.add(marker);
		}
	}

	public void removeFromMap() {
		for (Marker mark : mPoiMarks) {
			mark.remove();
		}
	}

	public void zoomToSpan() {
		if (mPois != null && mPois.size() > 0) {
			if (mAMap == null)
				return;
			LatLngBounds bounds = getLatLngBounds();
			mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
		}
	}

	private LatLngBounds getLatLngBounds() {
		LatLngBounds.Builder b = LatLngBounds.builder();
		for (int i = 0; i < mPois.size(); i++) {
			b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
					mPois.get(i).getLatLonPoint().getLongitude()));
		}
		return b.build();
	}

	private MarkerOptions getMarkerOptions(int index) {
		return new MarkerOptions()
				.position(
						new LatLng(mPois.get(index).getLatLonPoint()
								.getLatitude(), mPois.get(index)
								.getLatLonPoint().getLongitude()))
				.title(getTitle(index)).snippet(getSnippet(index))
				.icon(getBitmapDescriptor(index));
	}

	protected BitmapDescriptor getBitmapDescriptor(int index) {
		return null;
	}

	protected String getTitle(int index) {
		return mPois.get(index).getTitle();
	}

	protected String getSnippet(int index) {
		return mPois.get(index).getSnippet();
	}

	public int getPoiIndex(Marker marker) {
		for (int i = 0; i < mPoiMarks.size(); i++) {
			if (mPoiMarks.get(i).equals(marker)) {
				return i;
			}
		}
		return -1;
	}

	public CloudItem getPoiItem(int index) {
		if (index < 0 || index >= mPois.size()) {
			return null;
		}
		return mPois.get(index);
	}
}
