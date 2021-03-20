package com.github.tibolte.agendacalendarview.render;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.github.tibolte.agendacalendarview.R;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;

/**
 * Class helping to inflate our default layout in the AgendaAdapter
 */
public class DefaultEventRenderer extends EventRenderer<BaseCalendarEvent> {

    // region class - EventRenderer

    @Override
    public void render(@NonNull View view, @NonNull BaseCalendarEvent event) {
        CardView card = view.findViewById(R.id.view_agenda_event_card_view);
        TextView txtTitle = view.findViewById(R.id.view_agenda_event_title);
        TextView txtLocation = view.findViewById(R.id.view_agenda_event_location);
        LinearLayout descriptionContainer = view.findViewById(R.id.view_agenda_event_description_container);
        LinearLayout locationContainer = view.findViewById(R.id.view_agenda_event_location_container);

        descriptionContainer.setVisibility(View.VISIBLE);

        txtTitle.setText(event.getTitle());
        txtLocation.setText(event.getLocation());
        if (event.getLocation().length() > 0) {
            locationContainer.setVisibility(View.VISIBLE);
            txtLocation.setText(event.getLocation());
        } else {
            locationContainer.setVisibility(View.GONE);
        }

        if (!event.isPlaceholder()/*!event.getTitle().equals(view.getResources().getString(R.string.agenda_event_no_events))*/) {
            txtTitle.setTextColor(event.getTextColor());
            txtLocation.setTextColor(event.getTextColor());
            card.setCardBackgroundColor(event.getColor());
        }
        else {
            card.setCardBackgroundColor(Color.TRANSPARENT);
            card.setCardElevation(0);
            card.setBackgroundColor(Color.TRANSPARENT);
            card.setRadius(0);
            card.setBackgroundDrawable(null);
        }
    }

    @Override
    public int getEventLayout() {
        return R.layout.view_agenda_event;
    }

    // endregion
}
