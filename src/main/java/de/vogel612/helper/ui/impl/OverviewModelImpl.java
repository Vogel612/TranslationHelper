package de.vogel612.helper.ui.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;

public class OverviewModelImpl implements OverviewModel {

	private OverviewPresenter presenter;
	private Document translationDocument;
	private final ExecutorService fileOperationService = Executors
			.newSingleThreadExecutor();

	@Override
	public void register(OverviewPresenter p) {
		presenter = p;
	}

	@Override
	public void loadFromFile(Path resxFile) {
		Runnable buildDocument = () -> {
			SAXBuilder documentBuilder = new SAXBuilder();
			try {
				this.translationDocument = documentBuilder.build(resxFile
						.toFile());
				presenter.onParseCompletion();
			} catch (JDOMException e) {
				presenter.onException(e, "Unspecified Parsing error");
			} catch (IOException e) {
				presenter.onException(e, "Unspecified I/O Error");
			} // maybe do it better, tough
		};
		fileOperationService.submit(buildDocument);
	}

	@Override
	public List<Translation> getTranslations() {
		List<Element> translationElements = translationDocument
				.getContent(new ElementFilter("data"));
		
		return translationElements.stream().map(el -> {
			final String key = el.getAttribute("name").getValue();
			final String currentValue = el.getChildText("value");
			return new Translation(key, currentValue, currentValue);
		}).collect(Collectors.toList());
	}

}
