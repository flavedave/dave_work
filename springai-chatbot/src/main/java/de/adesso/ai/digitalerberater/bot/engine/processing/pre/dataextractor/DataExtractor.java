package de.adesso.ai.digitalerberater.bot.engine.processing.pre.dataextractor;

import java.util.List;

import de.adesso.ai.digitalerberater.bot.engine.model.ExtractedDataEntry;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.UserMessagePreProcessor;

public interface DataExtractor extends UserMessagePreProcessor<List<ExtractedDataEntry>> {}
