package at.sti_innsbruck.tourpackclient.view.events;

import at.sti_innsbruck.tourpackclient.logic.greendao.Thing;


public class ThingSavedEvent {

    public Thing t;

    public ThingSavedEvent(Thing t) {
        this.t = t;
    }
}
