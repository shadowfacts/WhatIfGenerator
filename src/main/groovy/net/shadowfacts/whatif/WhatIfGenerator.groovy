package net.shadowfacts.whatif

/**
 * @author shadowfacts
 */
class WhatIfGenerator {

	private def markovChain = new Hashtable<String, Vector<String>>();

	private static def rand = new Random();

	static void main(String[] args) {
		def generator = new WhatIfGenerator()
		def reference;
		if (new File("./reference.txt").exists()) {
			reference = ReferenceGenerator.getCachedReference()
		} else {
			reference = ReferenceGenerator.generateReference()
		}
		generator.markovChain.put("_start", new Vector<>())
		generator.markovChain.put("_end", new Vector<>())
		generator.parseReference(reference)

		def amount = Integer.parseInt(args[0])
		def saveToFile = args.length >= 2 && args[1].equalsIgnoreCase("--save");
		def questions = new ArrayList<String>();
		for (def i = 0; i < amount; i++) {
			def question = generator.generateSentence()
			if (!reference.contains(question)) {
				questions.add(question)
			} else {
				i--;
			}
		}
		if (saveToFile) {
			def questionFile = new File("./questions.txt");
			if (!questionFile.exists()) {
				questionFile.createNewFile()
			}

			questionFile.text = String.join("\n", questions)
		} else {
			questions.each({
				println(it)
			})
		}
	}

	void parseReference(String[] reference) {
		reference.each({
			it = it.substring(0, it.length() - 1)
			def words = it.split(" ")

			words.eachWithIndex({ String word, int i ->
				if ("#".equals(word) || word.empty) return;

				if (i == 0) {
					def startWords = markovChain.get("_start")
					startWords.add(word)
					def suffix = markovChain.get(word)
					if (suffix == null) {
						suffix = new Vector<>()
						suffix.add(words[i + 1])
						markovChain.put(word, suffix)
					}
				} else if (i == words.length - 1) {
					def endWords = markovChain.get("_end")
					endWords.add(word)
				} else {
					def suffix = markovChain.get(word)
					if (suffix == null) {
						suffix = new Vector<>();
						suffix.add(words[i + 1])
						markovChain.put(word, suffix);
					} else {
						suffix.add(words[i + 1])
						markovChain.put(word, suffix)
					}
				}
			})
		})
	}

	String generateSentence() {
			def newPhrase = new Vector<String>();

			def startWords = markovChain.get("_start");
			def startWordsLen = startWords.size()

			def nextWord = startWords.get(rand.nextInt(startWordsLen))
			newPhrase.add(nextWord)

			while (nextWord.charAt(nextWord.length() - 1) != "#" && newPhrase.size() <= 40 && !nextWord.endsWith("?")) {
				def wordSelection = markovChain.get(nextWord);
				if (wordSelection != null) {
					def wordSelectionLen = wordSelection.size();
					nextWord = wordSelection.get(rand.nextInt(wordSelectionLen))
					newPhrase.add(nextWord)
				} else {
					break;
				}
		}

		return String.join(" ", newPhrase)
	}

}