package net.vvakame.dropphosting.model;

import java.util.Date;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;

public class IconData {
	private Image icon = null;
	private String register = null;
	private Date createAt = null;

	@SuppressWarnings("unused")
	private String screen = null;
	private DroxmlData droData = null;
	private AppDataSrv app = null;

	public IconData() {
	}

	public IconData(DroxmlData droData, AppDataSrv app) {
		icon = app.getIcon();
		register = droData.getScreenName();

		screen = droData.getScreen();
		this.droData = droData;
		this.app = app;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Entity getEntity(Date createAt) {

		Entity entity = new Entity("icon");
		entity.setProperty("iconNameWithHint", app
				.getIconNameWithScreenHint(droData));
		entity.setProperty("binary", new Blob(icon.getImageData()));
		entity.setProperty("register", droData.getScreenName());
		entity.setProperty("createAt", createAt);
		return entity;
	}

	public static IconData getIconData(DatastoreService datastoreService,
			Entity entity) throws EntityNotFoundException {
		IconData app = new IconData();
		if (entity.hasProperty("binary")) {
			Blob blob = (Blob) entity.getProperty("binary");
			app.setIcon(ImagesServiceFactory.makeImage(blob.getBytes()));
		}
		if (entity.hasProperty("register")) {
			app.setRegister((String) entity.getProperty("register"));
		}
		if (entity.hasProperty("createAt")) {
			app.setCreateAt((Date) entity.getProperty("createAt"));
		}

		return app;
	}
}
