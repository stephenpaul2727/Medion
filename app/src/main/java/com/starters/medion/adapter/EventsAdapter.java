package com.starters.medion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.starters.medion.R;
import com.starters.medion.model.Event;

/**
 * Created by Ashish on 12/5/2016.
 */
public class EventsAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public EventsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Event object){
        list.add(object);
        super.add(object);
    }
    @Override
    public int getCount(){
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EventsHolder eventsHolder;
        if(row==null){
            LayoutInflater layoutInflater =(LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.display_event_info,parent,false);
            eventsHolder = new EventsHolder();
            eventsHolder.t_eventame =(TextView) row.findViewById(R.id.t_eventname);
            eventsHolder.t_date = (TextView) row.findViewById(R.id.t_date);
            eventsHolder.t_time = (TextView) row.findViewById(R.id.t_time);
            row.setTag(eventsHolder);

        }
        else {
            eventsHolder = (EventsHolder) row.getTag();
        }
        Event event = (Event) getItem(position);
        eventsHolder.t_eventame.setText(event.getEventName().toString()+" ( "+event.getAdmin().toString()+" )");
        eventsHolder.t_date.setText(event.getEventDate().toString());
        eventsHolder.t_time.setText(event.getEventTime().toString());


        return row;
    }

    static class EventsHolder{
        TextView t_eventame, t_date, t_time;
    }
}
