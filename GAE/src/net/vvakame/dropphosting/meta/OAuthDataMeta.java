package net.vvakame.dropphosting.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-06-05 01:41:57")
/** */
public final class OAuthDataMeta extends org.slim3.datastore.ModelMeta<net.vvakame.dropphosting.model.OAuthData> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.OAuthData, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.OAuthData, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.OAuthData, java.lang.Integer> oauthHashCode = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.OAuthData, java.lang.Integer>(this, "oauthHashCode", "oauthHashCode", java.lang.Integer.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.OAuthData> screenName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.OAuthData>(this, "screenName", "screenName");

    private static final OAuthDataMeta slim3_singleton = new OAuthDataMeta();

    /**
     * @return the singleton
     */
    public static OAuthDataMeta get() {
       return slim3_singleton;
    }

    /** */
    public OAuthDataMeta() {
        super("OAuthData", net.vvakame.dropphosting.model.OAuthData.class);
    }

    @Override
    public net.vvakame.dropphosting.model.OAuthData entityToModel(com.google.appengine.api.datastore.Entity entity) {
        net.vvakame.dropphosting.model.OAuthData model = new net.vvakame.dropphosting.model.OAuthData();
        model.setKey(entity.getKey());
        model.setOauthHashCode(longToInteger((java.lang.Long) entity.getProperty("oauthHashCode")));
        model.setScreenName((java.lang.String) entity.getProperty("screenName"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        net.vvakame.dropphosting.model.OAuthData m = (net.vvakame.dropphosting.model.OAuthData) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("oauthHashCode", m.getOauthHashCode());
        entity.setProperty("screenName", m.getScreenName());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        net.vvakame.dropphosting.model.OAuthData m = (net.vvakame.dropphosting.model.OAuthData) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        net.vvakame.dropphosting.model.OAuthData m = (net.vvakame.dropphosting.model.OAuthData) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(net.vvakame.dropphosting.model.OAuthData) is not defined.");
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    public String getSchemaVersionName() {
        return "slim3.schemaVersion";
    }

    @Override
    public String getClassHierarchyListName() {
        return "slim3.classHierarchyList";
    }

}