package com.github.tibolte.agendacalendarview.agenda;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.R;
import com.github.tibolte.agendacalendarview.utils.BusProvider;
import com.github.tibolte.agendacalendarview.utils.Events;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

public class AgendaView extends FrameLayout {

    private AgendaListView mAgendaListView;
    private View mShadowView;
    private boolean enablePlaceholder = true;
    private final List<Subscription> subscriptions = new ArrayList<>();

    // region Constructors

    public AgendaView(Context context) {
        super(context);
    }

    public AgendaView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_agenda, this, true);
    }

    // endregion

    // region Class - View

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        for (Subscription sub: subscriptions) {
            sub.unsubscribe();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mAgendaListView = (AgendaListView) findViewById(R.id.agenda_listview);
        mShadowView = findViewById(R.id.view_shadow);

        Subscription sub = BusProvider.getInstance().toObserverable()
                .subscribe(event -> {
                    if (event instanceof Events.DayClickedEvent) {
                        Events.DayClickedEvent clickedEvent = (Events.DayClickedEvent) event;
                        getAgendaListView().scrollToCurrentDate(clickedEvent.getCalendar());
                    } else if (event instanceof Events.CalendarScrolledEvent) {
                        int offset = (int) (3 * getResources().getDimension(R.dimen.day_cell_height));
                        translateList(offset);
                    } else if (event instanceof Events.EventsFetched) {
                        if (getAgendaListView().getAdapter() != null)
                            ((AgendaAdapter) getAgendaListView().getAdapter()).updateEvents(CalendarManager.getInstance().getEvents());

                        getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        if (getWidth() != 0 && getHeight() != 0) {
                                            // display only two visible rows on the calendar view
                                            if (enablePlaceholder) {
                                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                                                int height = getHeight();
                                                int margin = (int) (getContext().getResources().getDimension(R.dimen.calendar_header_height) + 2 * getContext().getResources().getDimension(R.dimen.day_cell_height));
                                                layoutParams.height = height;
                                                layoutParams.setMargins(0, margin, 0, 0);
                                                setLayoutParams(layoutParams);
                                            }

                                            getAgendaListView().scrollToCurrentDate(CalendarManager.getInstance().getToday());

                                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                        }
                                    }
                                }

                        );
                    } else if (event instanceof Events.ForecastFetched) {
                        ((AgendaAdapter) getAgendaListView().getAdapter()).updateEvents(CalendarManager.getInstance().getEvents());
                    }
                });
        subscriptions.add(sub);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                // if the user touches the listView, we put it back to the top
                translateList(0);
                BusProvider.getInstance().send(new Events.AgendaListViewTouchedEvent());
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    // endregion

    // region Public methods

    public AgendaListView getAgendaListView() {
        return mAgendaListView;
    }

    public void translateList(int targetY) {
        if (targetY != getTranslationY()) {
            ObjectAnimator mover = ObjectAnimator.ofFloat(this, "translationY", targetY);
            mover.setDuration(150);
            mover.start();
        }
    }

    public void enablePlaceholderForCalendar(boolean enable) {
        this.enablePlaceholder = enable;
    }

    // endregion
}
