package de.adesso.ai.digitalerberater.bot.engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.adesso.ai.digitalerberater.bot.engine.processing.post.DefaultResponseGenerator;
import de.adesso.ai.digitalerberater.bot.engine.processing.post.ResponseGenerator;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.dataextractor.DataExtractor;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.dataextractor.DefaultDataExtractor;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.stepextractor.DefaultStepExtractor;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.stepextractor.StepExtractor;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.topicextractor.DefaultTopicExtractor;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.topicextractor.TopicExtractor;

@Configuration
public class BotEngineConfig {
    @Bean
    public TopicExtractor topicExtractor(DefaultTopicExtractor defaultTopicExtractor) {
        return defaultTopicExtractor;
    }

    @Bean
    public DataExtractor dataExtractor(DefaultDataExtractor defaultDataExtractor) {
        return defaultDataExtractor;
    }

    @Bean
    public StepExtractor stepExtractor(DefaultStepExtractor defaultStepExtractor) {
        return defaultStepExtractor;
    }

    @Bean
    public ResponseGenerator responseGenerator(DefaultResponseGenerator defaultResponseGenerator) {
        return defaultResponseGenerator;
    }
}
