package net.shadowfacts.whatif;

import net.shadowfacts.shadowlib.util.FileUtils;
import net.shadowfacts.shadowlib.util.InternetUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shadowfacts
 */
public class WhatIfGenerator {

	public static void main(String[] args) {
		WhatIfGenerator instance = new WhatIfGenerator();
		instance.createReference();
	}

	private void createReference() {
		File temp = createTempDir();
		File reference = createReferenceFile();

		PrintWriter referenceWriter = null;
		try {
			referenceWriter = new PrintWriter(reference);
		} catch (FileNotFoundException ignored) {}

		int latest = getLatest();
		for (int i = 1; i < latest; i++) {
			String url = String.format("http://what-if.xkcd.com/%d/", i);
			File f = new File(temp, i + ".html");
			try {
				InternetUtils.downloadFile(url, f);
			} catch (IOException e) {
				System.err.println("Problem downloading what-if " + i);
				e.printStackTrace();
				System.exit(3);
			}

			org.jsoup.nodes.Document doc = null;
			try {
				doc = Jsoup.parse(f, null);
			} catch (IOException e) {
				System.err.println("Problem parsing what-if HTML from " + f.getPath());
				e.printStackTrace();
				System.exit(4);
			}
			Elements elements = doc.getElementsByAttributeValue("id", "question");
			List<String> questions = elements.stream()
					.map(org.jsoup.nodes.Element::text)
					.collect(Collectors.toList());
			String data = String.join("\n", questions);
			referenceWriter.append(data);
			referenceWriter.append("\n");
		}

		referenceWriter.close();
	}

	private int getLatest() {
		File feed = createFeedFile();
		try {
			InternetUtils.downloadFile("http://what-if.xkcd.com/feed.atom", feed);
		} catch (IOException e) {
			System.err.println("Problem downloading feed");
			e.printStackTrace();
			System.exit(3);
		}

		Document doc = XMLUtils.parseDOM(feed);
		Node firstId = doc.getElementsByTagName("id").item(1);
		String url = firstId.getFirstChild().getNodeValue();
		String[] bits = url.split("/");
		return Integer.parseInt(bits[bits.length - 1]);
	}

	private File createReferenceFile() {
		File f = new File("./reference.txt");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				System.err.println("Problem creating reference file");
				e.printStackTrace();
				System.exit(2);
			}
		} else {
			try {
				new PrintWriter(f).close();
			} catch (FileNotFoundException ignored) {}
		}
//		f.deleteOnExit();
		return f;
	}

	private File createFeedFile() {
		File f = new File("./feed.atom");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				System.err.println("Problem creating feed file");
				e.printStackTrace();
				System.exit(3);
			}
		} else {
			try {
				PrintWriter writer = new PrintWriter(f);
				writer.close();
			} catch (FileNotFoundException ignored) {}
		}
		f.deleteOnExit();
		return f;
	}

	private File createTempDir() {
		File f = new File("./temp");
		if (!f.exists()) {
			f.mkdirs();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			FileUtils.deleteDirRecursive(f);
		}));
		return f;
	}

}
