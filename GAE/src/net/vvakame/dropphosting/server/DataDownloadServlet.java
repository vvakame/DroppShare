package net.vvakame.dropphosting.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.vvakame.dropphosting.model.DroxmlData;

import org.w3c.dom.Document;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class DataDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = -7504558110741757404L;

	private static final Logger log = Logger
			.getLogger(DataDownloadServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// u screenName
		// v variant (予定)
		// p packageName (予定)
		// format

		String u = req.getParameter("u");
		String format = req.getParameter("format");

		if (u == null) {
			throw new IllegalArgumentException("Must need screenName");
		} else if (!"xml".equals(format)) {
			format = "html";
		}

		log.info("query data download, u=" + u + ", format=" + format);

		DatastoreService datastoreService = DatastoreServiceFactory
				.getDatastoreService();

		DroxmlData droData = DroxmlData.getDroxmlData(datastoreService, u);

		try {
			Document document = droData.getDOMDocument();
			DOMSource src = new DOMSource(document);

			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = null;
			if ("xml".equals(format)) {
				transformer = transFactory.newTransformer();
				res.setContentType("application/xml");
			} else {
				Source stylesheet = transFactory.getAssociatedStylesheet(src,
						null, null, null);
				Templates xsltTemplate = transFactory.newTemplates(stylesheet);
				transformer = xsltTemplate.newTransformer();
				res.setContentType("text/html");
			}
			transformer.setOutputProperty("encoding", "UTF-8");

			StreamResult result = new StreamResult(res.getOutputStream());
			transformer.transform(src, result);

		} catch (TransformerConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
