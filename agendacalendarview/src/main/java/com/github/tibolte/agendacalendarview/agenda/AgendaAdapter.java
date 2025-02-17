package com.github.tibolte.agendacalendarview.agenda;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.render.DefaultEventRenderer;
import com.github.tibolte.agendacalendarview.render.EventRenderer;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Adapter for the agenda, implements StickyListHeadersAdapter.
 * Days as sections and CalendarEvents as list items.
 */
public class AgendaAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<CalendarEvent> mEvents = new ArrayList<>();
    private List<EventRenderer<?>> mRenderers = new ArrayList<>();
    private int mCurrentDayColor;

    // region Constructor

    public AgendaAdapter(int currentDayTextColor) {
        this.mCurrentDayColor = currentDayTextColor;
    }

    // endregion

    // region Public methods

    public void updateEvents(List<CalendarEvent> events) {
        this.mEvents.clear();
        this.mEvents.addAll(events);
        notifyDataSetChanged();
    }

    // endregion

    // region Interface - StickyListHeadersAdapter

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        AgendaHeaderView agendaHeaderView = (AgendaHeaderView) convertView;
        if (agendaHeaderView == null) {
            agendaHeaderView = AgendaHeaderView.inflate(parent);
        }
        agendaHeaderView.setDay(getItem(position).getInstanceDay(), mCurrentDayColor, getItem(position).getDayReference().getShowBadge());
        return agendaHeaderView;
    }

    @Override
    public long getHeaderId(int position) {
        return mEvents.get(position).getInstanceDay().getTimeInMillis();
    }

    // endregion

    // region Class - BaseAdapter

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public CalendarEvent getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private EventRenderer getEventRenderer(CalendarEvent event) {
        EventRenderer eventRenderer = new DefaultEventRenderer();

        // Search for the correct event renderer
        for (EventRenderer renderer : mRenderers) {
            if(event.getClass().isAssignableFrom(renderer.getRenderType())) {
                eventRenderer = renderer;
                break;
            }
        }
        return eventRenderer;
    }

    @Override
    public int getViewTypeCount() {
        return mRenderers.size();
    }

    @Override
    public int getItemViewType(int position) {
        final CalendarEvent event = getItem(position);
        return mRenderers.indexOf(getEventRenderer(event));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CalendarEvent event = getItem(position);
        EventRenderer eventRenderer = getEventRenderer(event);

        if (convertView == null || ((int)convertView.getTag()) == eventRenderer.getEventLayout()) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(eventRenderer.getEventLayout(), parent, false);
            convertView.setTag(eventRenderer.getEventLayout());
        }
        eventRenderer.render(convertView, event);
        return convertView;
    }

    public void addEventRenderer(@NonNull final EventRenderer<?> renderer) {
        mRenderers.add(renderer);
    }

    // endregion
}
