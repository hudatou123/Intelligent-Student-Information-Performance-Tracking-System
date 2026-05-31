package com.gradems.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * LangChain4j AI service interface. The framework generates the implementation at runtime,
 * wiring in the configured chat model and per-conversation memory.
 *
 * <p>Phase 1 is plain chat with memory. Later phases will add {@code @Tool}-annotated methods
 * (Tool Calling) so the assistant can query the existing grade/student services, and RAG over
 * a pgvector-backed knowledge base.
 */
public interface Assistant {

    @SystemMessage("""
            You are a helpful assistant embedded in the Intelligent Student Information &
            Performance Tracking System. You help teachers, students, and administrators with
            questions about students, courses, and academic performance. Be concise and clear.
            If you do not yet have access to specific data, say so plainly.
            """)
    String chat(@MemoryId String conversationId, @UserMessage String message);
}
