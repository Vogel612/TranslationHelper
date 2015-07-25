package de.vogel612.helper.ui.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;

public class OverviewModelImpl implements OverviewModel {

	private static final String VALUE_NAME = "value";
	private static final String KEY_NAME = "name";
	private static final String ELEMENT_NAME = "data";
	private static final String FILE_NAME_FORMAT = "RubberduckUI%s.resx";
	private static final String FILENAME_REGEX = "^.*RubberduckUI\\.?([a-z]{2})?\\.resx$";
	private static final String SINGLE_TRUTH_LOCALE = "";

	private static final Pattern localeFinder = Pattern.compile(FILENAME_REGEX);

	private final Map<String, Document> translations = new HashMap<String, Document>();
	private final XPathFactory xPathFactory = XPathFactory.instance();

	private OverviewPresenter presenter;
	private Path currentPath;

	@Override
	public void register(final OverviewPresenter p) {
		presenter = p;
	}

	@Override
	public void loadFromDirectory(final Path resxFolder) {
		this.currentPath = resxFolder;
		translations.clear();

		try (Stream<Path> resxFiles = Files.find(resxFolder, 1, (path,
				properties) -> path.toString().matches(FILENAME_REGEX),
				FileVisitOption.FOLLOW_LINKS)) {
			resxFiles.forEach(this::loadResxFile);
		} catch (IOException ex) {
			System.out.printf("Could not access %s due to %s", resxFolder, ex);
			presenter.onException(ex, String.format(
					"Could not access %s due to %s", resxFolder, ex));
		}

		normalizeLocaleFiles();
		presenter.onParseCompletion();
	}
	private void loadResxFile(final Path path) {
		SAXBuilder documentBuilder = new SAXBuilder();
		try {
			Document doc = documentBuilder.build(path.toFile());
			Matcher localeMatcher = localeFinder.matcher(path.getFileName()
					.toString());
			if (localeMatcher.find()) {
				String locale = localeMatcher.group(1) == null
					? SINGLE_TRUTH_LOCALE
					: localeMatcher.group(1);
				translations.put(locale, doc);
			}
		} catch (JDOMException e) {
			this.presenter.onException(e, "Unspecified Parsing error");
		} catch (IOException e) {
			this.presenter.onException(e, "Unspecified I/O Error");
		} catch (Exception e) {
			this.presenter.onException(e, "Something went really wrong");
		}
	}
	private void normalizeLocaleFiles() {
		final List<Element> translationElements = translations
				.get(SINGLE_TRUTH_LOCALE).getRootElement()
				.getChildren(ELEMENT_NAME);
		final Set<String> singleTruth = new HashSet<String>();
		Iterator<Element> it = translationElements.iterator();

		while (it.hasNext()) {
			Element el = it.next();
			singleTruth.add(el.getAttribute(KEY_NAME).getValue());
		}

		translations.values().forEach(
				doc -> normalizeDocument(doc, singleTruth));

	}

	private void normalizeDocument(final Document doc,
			final Set<String> singleTruth) {
		final List<Element> localeElements = doc.getRootElement().getChildren(
				ELEMENT_NAME);
		Iterator<Element> cleanupIt = localeElements.iterator();
		Set<String> localeKeys = new HashSet<>();

		// remove keys not present in the Single truth
		while (cleanupIt.hasNext()) {
			final Element el = cleanupIt.next();
			if (!singleTruth.contains(el.getAttribute(KEY_NAME).getValue())) {
				cleanupIt.remove();
				continue;
			}
			localeKeys.add(el.getAttribute(KEY_NAME).getValue());
		}

		// build new elements for newly created keys in root
		singleTruth.stream().filter(key -> !localeKeys.contains(key))
				.forEach(k -> {
					Element newElement = new Element(ELEMENT_NAME);
					Element valueContainer = new Element(VALUE_NAME);
					valueContainer.setText("");

					newElement.setAttribute(KEY_NAME, k);
					newElement.addContent(valueContainer);
					doc.getRootElement().addContent(newElement);
				});
	}

	private String fileNameString(final String localeIdent) {
		return String.format(FILE_NAME_FORMAT, localeIdent.isEmpty() ? "" : "."
				+ localeIdent.toLowerCase());
	}

	@Override
	public List<Translation> getTranslations(final String locale) {
		Document document = translations.get(locale);
		return getTranslations(document);
	}

	private final List<Translation> getTranslations(final Document document) {
		final List<Element> translationElements = document.getRootElement()
				.getChildren(ELEMENT_NAME);

		return translationElements.stream().map(el -> {
			final String key = el.getAttribute(KEY_NAME).getValue();
			final String currentValue = el.getChildText(VALUE_NAME);
			return new Translation(key, currentValue);
		}).collect(Collectors.toList());
	}

	@Override
	public void updateTranslation(final String locale, final String key,
			final String newTranslation) {
		Element translationToUpdate = getValueElement(locale, key);
		translationToUpdate.setText(newTranslation);
	}

	private Element getValueElement(final String locale, final String key) {
		XPathExpression<Element> expression = xPathFactory.compile("/*/"
				+ ELEMENT_NAME + "[@" + KEY_NAME + "='" + key + "']/"
				+ VALUE_NAME, Filters.element());
		return expression.evaluate(translations.get(locale)).get(0);
	}

	@Override
	public void saveAll() {
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		for (Map.Entry<String, Document> entry : translations.entrySet()) {
			final Path outFile = currentPath.resolve(fileNameString(entry
					.getKey()));
			try (OutputStream outStream = Files.newOutputStream(outFile,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE)) {
				outputter.output(entry.getValue(), outStream);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				presenter.onException(e, "Could not save File");
			}
		}
	}
	@Override
	public Translation getSingleTranslation(final String locale,
			final String key) {
		final String currentValue = getValueElement("", key).getText();
		return new Translation(key, currentValue);
	}

	@Override
	public List<String> getAvailableLocales() {
		return new ArrayList<>(translations.keySet());
	}

}
