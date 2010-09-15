package net.vvakame.dropphosting.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-08-02 20:18:11")
/** */
public final class IconDataMeta extends org.slim3.datastore.ModelMeta<net.vvakame.dropphosting.model.IconData> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.IconData, java.util.Date> createAt = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.IconData, java.util.Date>(this, "createAt", "createAt", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.IconData> fileName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.IconData>(this, "fileName", "fileName");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.IconData, java.lang.Long> height = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.IconData, java.lang.Long>(this, "height", "height", java.lang.Long.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<net.vvakame.dropphosting.model.IconData, com.google.appengine.api.images.Image> icon = new org.slim3.datastore.UnindexedAttributeMeta<net.vvakame.dropphosting.model.IconData, com.google.appengine.api.images.Image>(this, "icon", "icon", com.google.appengine.api.images.Image.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.IconData, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.IconData, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.IconData> packageName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.IconData>(this, "packageName", "packageName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.IconData> register = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.IconData>(this, "register", "register");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.IconData, java.lang.Long> width = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.IconData, java.lang.Long>(this, "width", "width", java.lang.Long.class);

    private static final IconDataMeta slim3_singleton = new IconDataMeta();

    /**
     * @return the singleton
     */
    public static IconDataMeta get() {
       return slim3_singleton;
    }

    /** */
    public IconDataMeta() {
        super("IconData", net.vvakame.dropphosting.model.IconData.class);
    }

    @Override
    public net.vvakame.dropphosting.model.IconData entityToModel(com.google.appengine.api.datastore.Entity entity) {
        net.vvakame.dropphosting.model.IconData model = new net.vvakame.dropphosting.model.IconData();
        model.setCreateAt((java.util.Date) entity.getProperty("createAt"));
        model.setFileName((java.lang.String) entity.getProperty("fileName"));
        model.setHeight((java.lang.Long) entity.getProperty("height"));
        com.google.appengine.api.images.Image _icon = blobToSerializable((com.google.appengine.api.datastore.Blob) entity.getProperty("icon"));
        model.setIcon(_icon);
        model.setKey(entity.getKey());
        model.setPackageName((java.lang.String) entity.getProperty("packageName"));
        model.setRegister((java.lang.String) entity.getProperty("register"));
        model.setWidth((java.lang.Long) entity.getProperty("width"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        net.vvakame.dropphosting.model.IconData m = (net.vvakame.dropphosting.model.IconData) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("createAt", m.getCreateAt());
        entity.setProperty("fileName", m.getFileName());
        entity.setProperty("height", m.getHeight());
        entity.setUnindexedProperty("icon", serializableToBlob(m.getIcon()));
        entity.setProperty("packageName", m.getPackageName());
        entity.setProperty("register", m.getRegister());
        entity.setProperty("width", m.getWidth());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        net.vvakame.dropphosting.model.IconData m = (net.vvakame.dropphosting.model.IconData) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        net.vvakame.dropphosting.model.IconData m = (net.vvakame.dropphosting.model.IconData) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(net.vvakame.dropphosting.model.IconData) is not defined.");
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