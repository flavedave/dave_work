-- Enable pgvector extension
CREATE EXTENSION vector;

-- Create vector store table
CREATE TABLE public.vector_store (
    id VARCHAR(255) PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding VECTOR(3072)  -- Dimension für OpenAI embeddings (anpassen nach Bedarf)
);