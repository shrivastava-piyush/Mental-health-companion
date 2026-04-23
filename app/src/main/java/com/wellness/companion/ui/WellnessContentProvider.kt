package com.wellness.companion.ui

enum class MoodCategory {
    POSITIVE, NEUTRAL, NEGATIVE;

    companion object {
        fun fromValence(valence: Int): MoodCategory = when {
            valence > 20 -> POSITIVE
            valence < -20 -> NEGATIVE
            else -> NEUTRAL
        }
    }
}

object WellnessContentProvider {
    data class Quote(val text: String, val author: String)

    fun getQuote(category: MoodCategory): Quote {
        val positive = listOf(
            Quote("Radiate boundless love.", "Buddha"),
            Quote("The sun shines within.", "John Muir"),
            Quote("Joy is the journey.", "Tim Cook"),
            Quote("Be the light.", "Unknown"),
            Quote("Choose happiness now.", "Dalai Lama")
        )
        val neutral = listOf(
            Quote("Breathe. You are here.", "Zen"),
            Quote("Be still. Know thyself.", "Aristotle"),
            Quote("Nature does not hurry.", "Lao Tzu"),
            Quote("Focus on this breath.", "Unknown"),
            Quote("The present is enough.", "Thich Nhat Hanh")
        )
        val negative = listOf(
            Quote("This too shall pass.", "Persian"),
            Quote("The wound is the light.", "Rumi"),
            Quote("Softly navigate the storm.", "Unknown"),
            Quote("You are enough.", "Ram Dass"),
            Quote("Peace is an anchor.", "Unknown")
        )

        return when (category) {
            MoodCategory.POSITIVE -> positive.random()
            MoodCategory.NEUTRAL -> neutral.random()
            MoodCategory.NEGATIVE -> negative.random()
        }
    }

    const val LIBRARY_HERO = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80"
    const val INSIGHTS_HERO = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80"
    const val ATTRIBUTION = "Photography by Unsplash (Public Domain / CC0)"
}
