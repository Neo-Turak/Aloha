package cn.nurasoft.aloha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<LifeUtil> {

    public UserAdapter(@NonNull Context context, ArrayList<LifeUtil> life) {
        super(context,0, life);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LifeUtil util=getItem(position);
        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
        }
            TextView cel=convertView.findViewById(R.id.celcius);
            TextView date=convertView.findViewById(R.id.date);
            cel.setText(util.Celcius);
            date.setText(util.date);
        return  convertView;
       // return super.getView(position, convertView, parent);
    }

}
