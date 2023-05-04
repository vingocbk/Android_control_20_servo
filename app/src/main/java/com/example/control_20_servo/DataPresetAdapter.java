package com.example.control_20_servo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DataPresetAdapter extends BaseAdapter {
    private Context context;
    private int idLayout;
    private List<DataPreset> listDataPreset;

    public DataPresetAdapter(Context context, int idLayout, List<DataPreset> listDataPreset) {
        this.context = context;
        this.idLayout = idLayout;
        this.listDataPreset = listDataPreset;
    }

    @Override
    public int getCount() {
        if (listDataPreset.size() != 0 && !listDataPreset.isEmpty()) {
            return listDataPreset.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(idLayout, parent, false);
        }
        TextView txtNamePresetListView = (TextView) convertView.findViewById(R.id.txtNamePresetListView);
        TextView txtSpeedPresetListView = (TextView) convertView.findViewById(R.id.txtSpeedPresetListView);
        TextView[] txtAnglePresetListView = new TextView[20];
        txtAnglePresetListView[0] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView1);
        txtAnglePresetListView[1] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView2);
        txtAnglePresetListView[2] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView3);
        txtAnglePresetListView[3] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView4);
        txtAnglePresetListView[4] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView5);
        txtAnglePresetListView[5] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView6);
        txtAnglePresetListView[6] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView7);
        txtAnglePresetListView[7] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView8);
        txtAnglePresetListView[8] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView9);
        txtAnglePresetListView[9] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView10);
        txtAnglePresetListView[10] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView11);
        txtAnglePresetListView[11] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView12);
        txtAnglePresetListView[12] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView13);
        txtAnglePresetListView[13] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView14);
        txtAnglePresetListView[14] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView15);
        txtAnglePresetListView[15] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView16);
        txtAnglePresetListView[16] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView17);
        txtAnglePresetListView[17] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView18);
        txtAnglePresetListView[18] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView19);
        txtAnglePresetListView[19] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView20);

        final DataPreset data = listDataPreset.get(position);
        if (listDataPreset != null && !listDataPreset.isEmpty()) {
            txtNamePresetListView.setText("Name: " + data.getName());
            txtSpeedPresetListView.setText("Speed: " + String.valueOf(data.getSpeed()));
            int[] angle = data.getAngle();
            for(int i = 0; i < angle.length; i++){
                txtAnglePresetListView[i].setText(String.valueOf(i+1) + ": " + String.valueOf(angle[i]));
            }
        }
        return convertView;
    }
}
