package com.example.hesolutions.horizon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/*
public class SetZone extends AppCompatActivity {


    static class ViewHolder {
        TextView text;
    }
    public class HashBiMapAdapter extends BaseAdapter {

        private BiMap<String, String> devicelist = HashBiMap.create();
        private String[] deviceID;          //keys
        public HashBiMapAdapter(BiMap<String, String> device){
            devicelist = device;
            deviceID = devicelist.keySet().toArray(new String[device.size()]);
        }



        public String getKey(int position) {       //key
            return deviceID[position] ;
        }

        @Override
        public int getCount() {
            return devicelist.size();
        }

        @Override
        public String getItem(int position) {       //values
            return devicelist.get(deviceID[position]);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            if (rowView == null) {
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.text.setText(getItem(position));

            return rowView;

        }
        public BiMap<String, String> getList(){
            return devicelist;
        }

    }

    BiMap<String, String> items1, items2;
    ListView listView1, listView2;
    HashBiMapAdapter myItemsListAdapter1, myItemsListAdapter2;
    Button Sector,Zone;
    Button Cancel;
    Button Apply;
    EditText sectorname;
    TextView Sectorname;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_zone);
        listView1 = (ListView)findViewById(R.id.listview1);
        listView2 = (ListView)findViewById(R.id.listview2);
        Sector =(Button)findViewById(R.id.Sector);
        Zone =(Button)findViewById(R.id.Zone);
        Cancel = (Button)findViewById(R.id.Cancel);
        Apply =(Button)findViewById(R.id.Apply);
        sectorname = (EditText)findViewById(R.id.sectorname);
        Sectorname = (TextView)findViewById(R.id.Sectorname);


        sectorname.setVisibility(View.GONE);
        listView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashBiMapAdapter associatedAdapter = (HashBiMapAdapter) (parent.getAdapter());
                BiMap<String, String> associatedList = associatedAdapter.getList();
                String selectedKey = associatedAdapter.getKey(position);
                String selectedValue = associatedAdapter.getItem(position);

                associatedList.remove(selectedKey);
                view.invalidate();
                associatedAdapter.notifyDataSetChanged();

                HashBiMapAdapter list2Adapter = (HashBiMapAdapter) (listView2.getAdapter());
                items2.put(selectedKey, selectedValue);
                view.invalidate();
                list2Adapter.notifyDataSetChanged();
                myItemsListAdapter2 = new HashBiMapAdapter(items2);
                listView2.setAdapter(myItemsListAdapter2);

            }
        });
        listView2.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashBiMapAdapter associatedAdapter = (HashBiMapAdapter) (parent.getAdapter());
                BiMap<String, String> associatedList = associatedAdapter.getList();
                String selectedKey = associatedAdapter.getKey(position);
                String selectedValue = associatedAdapter.getItem(position);

                associatedList.remove(selectedKey);
                view.invalidate();
                associatedAdapter.notifyDataSetChanged();

                HashBiMapAdapter list1Adapter = (HashBiMapAdapter) (listView1.getAdapter());
                BiMap<String, String> list1List = list1Adapter.getList();
                list1List.put(selectedKey, selectedValue);
                list1Adapter.notifyDataSetChanged();
            }
        });

        Sectorname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sectorname.setVisibility(View.VISIBLE);

            }
        });

        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(v.getContext(), AdminPage.class);

                // NEW SECTOR ZONE SAVE TO DATA

                BiMap<String, BiMap> Sector;
                Sector = DataManager.getInstance().getsector();
                name = sectorname.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No name is given"
                            , Toast.LENGTH_LONG).show();

                } else {
                    Sector.put(name, items2);
                    DataManager.getInstance().setsector(Sector); //store back to datamanger

                    sectorname.setText("");
                    sectorname.setVisibility(View.GONE);
                    startActivity(intent1);
                }

            }
        });


        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(v.getContext(), AdminPage.class);
                startActivity(intent1);
            }
        });
        Zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(v.getContext(), ZoneSet.class);
                startActivity(intent1);
            }
        });
        Sector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(v.getContext(), SetZone.class);
                startActivity(intent1);
            }
        });
        Sector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items1 = HashBiMap.create();
                items2 = HashBiMap.create();

                BiMap<String, String> device;
                device = DataManager.getInstance().getdevice();

                for (Map.Entry<String, String> entry : device.entrySet()) {
                    String DeviceID = entry.getKey();
                    String DeviceName = entry.getValue();
                    items1.put(DeviceID, DeviceName);
                }
                myItemsListAdapter1 = new HashBiMapAdapter(items1);
                myItemsListAdapter2 = new HashBiMapAdapter(items2);
                listView1.setAdapter(myItemsListAdapter1);
                listView2.setAdapter(myItemsListAdapter2);
            }
        });


    }

}
*/