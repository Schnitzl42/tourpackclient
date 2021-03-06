package at.sti_innsbruck.tourpackclient.logic.greendao;

import at.sti_innsbruck.tourpackclient.logic.greendao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "EXTRA_PROPERTIES".
 */
public class ExtraProperties {

    private Long id;
    private String thing_id;
    private String property_id;
    private String content;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ExtraPropertiesDao myDao;

    private Thing thing;
    private String thing__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ExtraProperties() {
    }

    public ExtraProperties(Long id) {
        this.id = id;
    }

    public ExtraProperties(Long id, String thing_id, String property_id, String content) {
        this.id = id;
        this.thing_id = thing_id;
        this.property_id = property_id;
        this.content = content;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getExtraPropertiesDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThing_id() {
        return thing_id;
    }

    public void setThing_id(String thing_id) {
        this.thing_id = thing_id;
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /** To-one relationship, resolved on first access. */
    public Thing getThing() {
        String __key = this.thing_id;
        if (thing__resolvedKey == null || thing__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThingDao targetDao = daoSession.getThingDao();
            Thing thingNew = targetDao.load(__key);
            synchronized (this) {
                thing = thingNew;
            	thing__resolvedKey = __key;
            }
        }
        return thing;
    }

    public void setThing(Thing thing) {
        synchronized (this) {
            this.thing = thing;
            thing_id = thing == null ? null : thing.getThing_id();
            thing__resolvedKey = thing_id;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
