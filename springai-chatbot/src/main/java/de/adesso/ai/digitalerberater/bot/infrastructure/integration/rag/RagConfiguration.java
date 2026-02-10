package de.adesso.ai.digitalerberater.bot.infrastructure.integration.rag;

import java.util.List;

import lombok.extern.log4j.Log4j2;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Log4j2
public class RagConfiguration {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("classpath:/docs/allgemein/*")
    private Resource[] resources;

    @Value("classpath:/docs/haftpflicht/*")
    private Resource[] phv_resources;

    @Bean("phv_vectorStore")
    PgVectorStore getPHVVecotStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        log.info("SpringAiApplication.phv_pgVectorStore");
        PgVectorStore pgVectorStore =
                PgVectorStore.builder(jdbcTemplate, embeddingModel).build();

        for (Resource resource : phv_resources) {
            if (documentExists(resource.getFilename())) {
                log.info(resource.getFilename() + " already exist in Vectorstore");
            } else {
                log.info("Adding " + resource.getFilename() + " to Vectorstore");
                TextReader textReader = new TextReader(resource);
                textReader.getCustomMetadata().put("filename", "neu");
                List<Document> documents = textReader.get();

                // Sanitize documents to remove null bytes that cause PostgreSQL UTF-8 errors
                List<Document> sanitizedDocuments = documents.stream()
                        .map(doc -> new Document(sanitizeText(doc.getText()), doc.getMetadata()))
                        .toList();

                TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
                List<Document> splitDocuments = tokenTextSplitter.apply(sanitizedDocuments);
                pgVectorStore.add(splitDocuments);
            }
        }

        return pgVectorStore;
    }

    @Bean("vectorStore")
    PgVectorStore getVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        log.info("SpringAiApplication.pgVectorStore");
        PgVectorStore pgVectorStore =
                PgVectorStore.builder(jdbcTemplate, embeddingModel).build();

        for (Resource resource : resources) {
            if (documentExists(resource.getFilename())) {
                log.info(resource.getFilename() + " already exist in Vectorstore");
            } else {
                log.info("Adding " + resource.getFilename() + " to Vectorstore");
                TextReader textReader = new TextReader(resource);
                textReader.getCustomMetadata().put("filename", "neu");
                List<Document> documents = textReader.get();

                // Sanitize documents to remove null bytes that cause PostgreSQL UTF-8 errors
                List<Document> sanitizedDocuments = documents.stream()
                        .map(doc -> new Document(sanitizeText(doc.getText()), doc.getMetadata()))
                        .toList();

                TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
                List<Document> splitDocuments = tokenTextSplitter.apply(sanitizedDocuments);
                pgVectorStore.add(splitDocuments);
            }
        }
        return pgVectorStore;
    }

    private String sanitizeText(String text) {
        if (text == null) {
            return "";
        }
        // Remove null bytes and other control characters that cause PostgreSQL UTF-8 errors
        return text.replaceAll("\u0000", "") // Remove null bytes
                .replaceAll("[\u0001-\u0008\u000B-\u000C\u000E-\u001F\u007F]", ""); // Remove other control chars
    }

    public boolean documentExists(String documentId) {
        String sql = "SELECT COUNT(*) FROM vector_store WHERE metadata->>'source' = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, documentId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
