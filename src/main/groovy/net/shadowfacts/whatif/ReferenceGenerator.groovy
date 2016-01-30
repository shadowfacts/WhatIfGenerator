package net.shadowfacts.whatif

import org.jsoup.Jsoup

/**
 * @author shadowfacts
 */
class ReferenceGenerator {

	static String[] getCachedReference() {
		return new File("./reference.txt").text.split("\n")
	}

	static String[] generateReference() {
		def temp = createTempDir()
		def reference = createReferenceFile()


		int latest = getLatest();
		for (int i = 1; i < latest; i++) {
			def url = "http://what-if.xkcd.com/" + i
			def f = new File(temp, i + ".html")
			def out = f.newOutputStream()
			out << new URL(url).openStream()
			out.close()

			def doc = Jsoup.parse(f, null)
			def elements = doc.getElementsByAttributeValue("id", "question")
			elements.each {
				reference.text += it.text() + "\n"
			}
		}

		return reference.text.split("\n")
	}

	private static int getLatest() {
		def feed = createFeedFile()
		def out = feed.newOutputStream()
		out << new URL("http://what-if.xkcd.com/feed.atom").openStream()
		out.close()

		def doc = XMLUtils.parseDOM(feed)
		def firstId = doc.getElementsByTagName("id").item(1)
		String url = firstId.firstChild.nodeValue
		String[] bits = url.split("/")
		return Integer.parseInt(bits[bits.length - 1])
	}

	private static File createTempDir() {
		def f = new File("./temp");
		if (!f.exists()) {
			f.mkdirs()
		}
		Runtime.getRuntime().addShutdownHook(new Thread({
			f.deleteDir()
		}))
		return f
	}

	private static File createReferenceFile() {
		def f = new File("./reference.txt")
		if (!f.exists()) {
			f.createNewFile()
		} else {
			f.text = ""
		}
//		f.deleteOnExit()
		return f
	}

	private static File createFeedFile() {
		def f = new File("./feed.atom")
		if (!f.exists()) {
			f.createNewFile()
		} else {
			f.text = ""
		}
		f.deleteOnExit()
		return f
	}

}
