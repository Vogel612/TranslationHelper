package de.vogel612.helper.ui.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;

public class OverviewModelImpl implements OverviewModel {

	private static final String VALUE_NAME = "value";

	private static final String KEY_NAME = "name";

	private static final String ELEMENT_NAME = "data";

	private static final String FILE_NAME_FORMAT = "RubberduckUI%s.resx";

	private OverviewPresenter presenter;
	private Document translationDocument;
	private final Map<String, String> originalLocale = new HashMap<String, String>();
	private final ExecutorService fileOperationService = Executors
			.newSingleThreadExecutor();

	private final BiConsumer<Path, Boolean> loadResxFile = (path, isTarget) -> {
		SAXBuilder documentBuilder = new SAXBuilder();
		try {
			if (isTarget) {
				this.translationDocument = documentBuilder.build(path.toFile());
			} else {
				Document doc = documentBuilder.build(path.toFile());
				List<Element> translationElements = doc.getRootElement()
						.getChildren(ELEMENT_NAME);

				translationElements.stream()
					.forEach(element -> {
								originalLocale.put(
										element.getAttribute(KEY_NAME).getValue(), 
										element.getChildText(VALUE_NAME));
							});
			}
		} catch (JDOMException e) {
			presenter.onException(e, "Unspecified Parsing error");
		} catch (IOException e) {
			presenter.onException(e, "Unspecified I/O Error");
		} catch (Exception e) {
			presenter.onException(e, "Something went really wrong");
		}
	};

	@Override
	public void register(OverviewPresenter p) {
		presenter = p;
	}

	@Override
	public void loadFromDirectory(Path resxFolder, String rootLocale,
			String targetLocale) {
		final Path rootFile = resxFolder.resolve(fileNameString(rootLocale));
		final Path targetFile = resxFolder
				.resolve(fileNameString(targetLocale));
		Runnable buildDocument = () -> {
			originalLocale.clear();
			loadResxFile.accept(rootFile, false);
			loadResxFile.accept(targetFile, true);
			presenter.onParseCompletion();
		};
		fileOperationService.submit(buildDocument);
	}

	private String fileNameString(final String localeIdent) {
		return String.format(FILE_NAME_FORMAT, localeIdent.isEmpty() ? "" : "."
				+ localeIdent.toLowerCase());
	}

	@Override
	public List<Translation> getTranslations() {
		List<Element> translationElements = translationDocument
				.getRootElement().getChildren(ELEMENT_NAME);

		return translationElements.stream().map(el -> {
			final String key = el.getAttribute(KEY_NAME).getValue();
			final String currentValue = el.getChildText(VALUE_NAME);
			return new Translation(key, originalLocale.get(key), currentValue);
		}).collect(Collectors.toList());
	}

}
