package net.vvakame.dropphosting.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-06-16 01:14:20")
/** */
public final class VariantDataMeta extends org.slim3.datastore.ModelMeta<net.vvakame.dropphosting.model.VariantData> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.VariantData, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.VariantData, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.VariantData> screenName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.VariantData>(this, "screenName", "screenName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.VariantData> variant = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.VariantData>(this, "variant", "variant");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.VariantData> version = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.VariantData>(this, "version", "version");

    private static final VariantDataMeta slim3_singleton = new VariantDataMeta();

    /**
     * @return the singleton
     */
    public static VariantDataMeta get() {
       return slim3_singleton;
    }

    /** */
    public VariantDataMeta() {
        super("VariantData", net.vvakame.dropphosting.model.VariantData.class);
    }

    @Override
    public net.vvakame.dropphosting.model.VariantData entityToModel(com.google.appengine.api.datastore.Entity entity) {
        net.vvakame.dropphosting.model.VariantData model = new net.vvakame.dropphosting.model.VariantData();
        model.setKey(entity.getKey());
        model.setScreenName((java.lang.String) entity.getProperty("screenName"));
        model.setVariant((java.lang.String) entity.getProperty("variant"));
        model.setVersion((java.lang.String) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        net.vvakame.dropphosting.model.VariantData m = (net.vvakame.dropphosting.model.VariantData) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("screenName", m.getScreenName());
        entity.setProperty("variant", m.getVariant());
        entity.setProperty("version", m.getVersion());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        net.vvakame.dropphosting.model.VariantData m = (net.vvakame.dropphosting.model.VariantData) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        net.vvakame.dropphosting.model.VariantData m = (net.vvakame.dropphosting.model.VariantData) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(net.vvakame.dropphosting.model.VariantData) is not defined.");
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