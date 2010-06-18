package net.vvakame.dropphosting.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-06-16 01:14:20")
/** */
public final class TwitterAuthorizedDataMeta extends org.slim3.datastore.ModelMeta<net.vvakame.dropphosting.model.TwitterAuthorizedData> {

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<net.vvakame.dropphosting.model.TwitterAuthorizedData, twitter4j.http.AccessToken> accessToken = new org.slim3.datastore.UnindexedAttributeMeta<net.vvakame.dropphosting.model.TwitterAuthorizedData, twitter4j.http.AccessToken>(this, "accessToken", "accessToken", twitter4j.http.AccessToken.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.TwitterAuthorizedData, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.TwitterAuthorizedData, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.TwitterAuthorizedData> screenName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.TwitterAuthorizedData>(this, "screenName", "screenName");

    private static final TwitterAuthorizedDataMeta slim3_singleton = new TwitterAuthorizedDataMeta();

    /**
     * @return the singleton
     */
    public static TwitterAuthorizedDataMeta get() {
       return slim3_singleton;
    }

    /** */
    public TwitterAuthorizedDataMeta() {
        super("TwitterAuthorizedData", net.vvakame.dropphosting.model.TwitterAuthorizedData.class);
    }

    @Override
    public net.vvakame.dropphosting.model.TwitterAuthorizedData entityToModel(com.google.appengine.api.datastore.Entity entity) {
        net.vvakame.dropphosting.model.TwitterAuthorizedData model = new net.vvakame.dropphosting.model.TwitterAuthorizedData();
        twitter4j.http.AccessToken _accessToken = blobToSerializable((com.google.appengine.api.datastore.Blob) entity.getProperty("accessToken"));
        model.setAccessToken(_accessToken);
        model.setKey(entity.getKey());
        model.setScreenName((java.lang.String) entity.getProperty("screenName"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        net.vvakame.dropphosting.model.TwitterAuthorizedData m = (net.vvakame.dropphosting.model.TwitterAuthorizedData) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setUnindexedProperty("accessToken", serializableToBlob(m.getAccessToken()));
        entity.setProperty("screenName", m.getScreenName());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        net.vvakame.dropphosting.model.TwitterAuthorizedData m = (net.vvakame.dropphosting.model.TwitterAuthorizedData) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        net.vvakame.dropphosting.model.TwitterAuthorizedData m = (net.vvakame.dropphosting.model.TwitterAuthorizedData) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(net.vvakame.dropphosting.model.TwitterAuthorizedData) is not defined.");
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