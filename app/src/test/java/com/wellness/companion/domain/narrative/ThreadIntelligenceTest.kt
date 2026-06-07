package com.wellness.companion.domain.narrative

import com.wellness.companion.domain.nlp.TextAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Test

class ThreadIntelligenceTest {

    @Test
    fun testSimilarityConsistency() {
        val kws1 = listOf("anxiety", "work", "stress")
        val kws2 = listOf("work", "stress", "boss")
        
        val sim = TextAnalyzer.similarity(kws1, kws2)
        // Intersection: work, stress (2)
        // Union: anxiety, work, stress, boss (4)
        // Jaccard: 2/4 = 0.5
        assertEquals(0.5f, sim, 0.01f)
    }

    @Test
    fun testStemmingNormalization() {
        val words = "I am feeling anxious and my anxiety is high"
        val kws = TextAnalyzer.keywords(words)
        // "feeling" -> "feel"
        // "anxious" -> "anxy"
        // "anxiety" -> "anxy"
        // "high" -> "high"
        // Keywords should include "anxy" and "feel"
        assert(kws.contains("anxy"))
    }
}
