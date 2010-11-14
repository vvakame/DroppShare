package net.vvakame.drphost.controller;

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

import net.vvakame.drphost.meta.VariantDataMeta;
import net.vvakame.drphost.model.VariantData;

import org.slim3.datastore.Datastore;
import org.w3c.dom.Document;

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
		// v variant
		// p packageName (予定)
		// format

		String u = req.getParameter("u");
		String v = req.getParameter("v");
		String format = req.getParameter("format");

		if (u == null) {
			throw new IllegalArgumentException("Must need screenName");
		} else if (!"xml".equals(format)) {
			format = "html";
		}

		if (v == null) {
			v = "default";
		}

		log.info("query data download, u=" + u + ", variant=" + v + ", format="
				+ format);

		VariantDataMeta vMeta = VariantDataMeta.get();
		VariantData variantData = Datastore.query(vMeta).filter(
				vMeta.screenName.equal(u), vMeta.variant.equal(v)).asSingle();
		if (variantData == null) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		try {
			Document document = variantData.getDOMDocument(req.getLocale());
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
