package net.vvakame.dropphosting.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-06-10 00:48:06")
/** */
public final class AppDataSrvMeta extends org.slim3.datastore.ModelMeta<net.vvakame.dropphosting.model.AppDataSrv> {

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv> appName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv>(this, "appName", "appName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv> description = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv>(this, "description", "description");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv, org.slim3.datastore.ModelRef<net.vvakame.dropphosting.model.IconData>, net.vvakame.dropphosting.model.IconData> iconRef = new org.slim3.datastore.ModelRefAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv, org.slim3.datastore.ModelRef<net.vvakame.dropphosting.model.IconData>, net.vvakame.dropphosting.model.IconData>(this, "iconRef", "iconRef", org.slim3.datastore.ModelRef.class, net.vvakame.dropphosting.model.IconData.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv> packageName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv>(this, "packageName", "packageName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv> screenName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv>(this, "screenName", "screenName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv> variant = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv>(this, "variant", "variant");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv, org.slim3.datastore.ModelRef<net.vvakame.dropphosting.model.VariantData>, net.vvakame.dropphosting.model.VariantData> variantRef = new org.slim3.datastore.ModelRefAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv, org.slim3.datastore.ModelRef<net.vvakame.dropphosting.model.VariantData>, net.vvakame.dropphosting.model.VariantData>(this, "variantRef", "variantRef", org.slim3.datastore.ModelRef.class, net.vvakame.dropphosting.model.VariantData.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv, java.lang.Integer> versionCode = new org.slim3.datastore.CoreAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv, java.lang.Integer>(this, "versionCode", "versionCode", int.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv> versionName = new org.slim3.datastore.StringAttributeMeta<net.vvakame.dropphosting.model.AppDataSrv>(this, "versionName", "versionName");

    private static final AppDataSrvMeta slim3_singleton = new AppDataSrvMeta();

    /**
     * @return the singleton
     */
    public static AppDataSrvMeta get() {
       return slim3_singleton;
    }

    /** */
    public AppDataSrvMeta() {
        super("AppData", net.vvakame.dropphosting.model.AppDataSrv.class);
    }

    @Override
    public net.vvakame.dropphosting.model.AppDataSrv entityToModel(com.google.appengine.api.datastore.Entity entity) {
        net.vvakame.dropphosting.model.AppDataSrv model = new net.vvakame.dropphosting.model.AppDataSrv();
        model.setAppName((java.lang.String) entity.getProperty("appName"));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        if (model.getIconRef() == null) {
            throw new NullPointerException("The property(iconRef) is null.");
        }
        model.getIconRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("iconRef"));
        model.setKey(entity.getKey());
        model.setPackageName((java.lang.String) entity.getProperty("packageName"));
        model.setScreenName((java.lang.String) entity.getProperty("screenName"));
        model.setVariant((java.lang.String) entity.getProperty("variant"));
        if (model.getVariantRef() == null) {
            throw new NullPointerException("The property(variantRef) is null.");
        }
        model.getVariantRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("variantRef"));
        model.setVersionCode(longToPrimitiveInt((java.lang.Long) entity.getProperty("versionCode")));
        model.setVersionName((java.lang.String) entity.getProperty("versionName"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        net.vvakame.dropphosting.model.AppDataSrv m = (net.vvakame.dropphosting.model.AppDataSrv) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("appName", m.getAppName());
        entity.setProperty("description", m.getDescription());
        if (m.getIconRef() == null) {
            throw new NullPointerException("The property(iconRef) must not be null.");
        }
        entity.setProperty("iconRef", org.slim3.datastore.ModelMeta.assignKeyIfNecessary(m.getIconRef().getModelMeta(), m.getIconRef().getModel()));
        entity.setProperty("packageName", m.getPackageName());
        entity.setProperty("screenName", m.getScreenName());
        entity.setProperty("variant", m.getVariant());
        if (m.getVariantRef() == null) {
            throw new NullPointerException("The property(variantRef) must not be null.");
        }
        entity.setProperty("variantRef", org.slim3.datastore.ModelMeta.assignKeyIfNecessary(m.getVariantRef().getModelMeta(), m.getVariantRef().getModel()));
        entity.setProperty("versionCode", m.getVersionCode());
        entity.setProperty("versionName", m.getVersionName());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        net.vvakame.dropphosting.model.AppDataSrv m = (net.vvakame.dropphosting.model.AppDataSrv) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        net.vvakame.dropphosting.model.AppDataSrv m = (net.vvakame.dropphosting.model.AppDataSrv) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(net.vvakame.dropphosting.model.AppDataSrv) is not defined.");
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