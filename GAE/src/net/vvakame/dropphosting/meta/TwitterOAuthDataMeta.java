package net.vvakame.dropphosting.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-08-02 20:18:11")
/** */
public final class TwitterOAuthDataMeta extends org.slim3.datastore.ModelMeta<net.vvakame.dropphosting.model.TwitterOAuthData> {

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<net.vvakame.dropphosting.model.TwitterOAuthData, twitter4j.http.AccessToken> accessToken = new org.slim3.datastore.UnindexedAttributeMeta<net.vvakame.dropphosting.model.TwitterOAuthData, twitter4j.http.AccessToken>(this, "accessToken", "accessToken", twitter4j.http.AccessToken.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.TwitterOAuthData, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.TwitterOAuthData, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.TwitterOAuthData, java.lang.Integer> oauthHashCode = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.TwitterOAuthData, java.lang.Integer>(this, "oauthHashCode", "oauthHashCode", java.lang.Integer.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.TwitterOAuthData> screenName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.TwitterOAuthData>(this, "screenName", "screenName");

    private static final TwitterOAuthDataMeta slim3_singleton = new TwitterOAuthDataMeta();

    /**
     * @return the singleton
     */
    public static TwitterOAuthDataMeta get() {
       return slim3_singleton;
    }

    /** */
    public TwitterOAuthDataMeta() {
        super("TwitterOAuthData", net.vvakame.dropphosting.model.TwitterOAuthData.class);
    }

    @Override
    public net.vvakame.dropphosting.model.TwitterOAuthData entityToModel(com.google.appengine.api.datastore.Entity entity) {
        net.vvakame.dropphosting.model.TwitterOAuthData model = new net.vvakame.dropphosting.model.TwitterOAuthData();
        twitter4j.http.AccessToken _accessToken = blobToSerializable((com.google.appengine.api.datastore.Blob) entity.getProperty("accessToken"));
        model.setAccessToken(_accessToken);
        model.setKey(entity.getKey());
        model.setOauthHashCode(longToInteger((java.lang.Long) entity.getProperty("oauthHashCode")));
        model.setScreenName((java.lang.String) entity.getProperty("screenName"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        net.vvakame.dropphosting.model.TwitterOAuthData m = (net.vvakame.dropphosting.model.TwitterOAuthData) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setUnindexedProperty("accessToken", serializableToBlob(m.getAccessToken()));
        entity.setProperty("oauthHashCode", m.getOauthHashCode());
        entity.setProperty("screenName", m.getScreenName());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        net.vvakame.dropphosting.model.TwitterOAuthData m = (net.vvakame.dropphosting.model.TwitterOAuthData) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        net.vvakame.dropphosting.model.TwitterOAuthData m = (net.vvakame.dropphosting.model.TwitterOAuthData) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(net.vvakame.dropphosting.model.TwitterOAuthData) is not defined.");
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