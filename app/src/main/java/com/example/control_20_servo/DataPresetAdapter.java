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
    private int positionSelect = -1;

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
        TextView[] txtDataXYZSaved = new TextView[20];
        txtDataXYZSaved[0] = (TextView) convertView.findViewById(R.id.txtAnglePresetListView1);
        final DataPreset data = listDataPreset.get(position);

        if (listDataPreset != null && !listDataPreset.isEmpty()) {
            txtNamePresetListView.setText(data.getName());
            int[] angle = data.getAngle();
            for(int i = 0; i < angle.length; i++){
                txtDataXYZSaved[0].setText(String.valueOf(i) + ": " + String.valueOf(angle[i]));
            }
        }
        return convertView;
    }
}
