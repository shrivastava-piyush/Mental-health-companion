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
            Quote("Happiness is not something readymade. It comes from your own actions.", "Dalai Lama"),
            Quote("The most important thing is to enjoy your life—to be happy—it's all that matters.", "Audrey Hepburn"),
            Quote("Let your joy be in your journey, not in some distant goal.", "Tim Cook"),
            Quote("The sun shines not on us but in us.", "John Muir"),
            Quote("Radiate boundless love towards the entire world.", "Buddha")
        )
        val neutral = listOf(
            Quote("Focus on the present moment. Breathe.", "Zen Proverb"),
            Quote("Knowing yourself is the beginning of all wisdom.", "Aristotle"),
            Quote("Be still. The world will uncover itself to you.", "Franz Kafka"),
            Quote("Nature does not hurry, yet everything is accomplished.", "Lao Tzu"),
            Quote("The present moment is filled with joy and happiness. If you are attentive, you will see it.", "Thich Nhat Hanh")
        )
        val negative = listOf(
            Quote("This too shall pass. Be gentle with yourself.", "Persian Proverb"),
            Quote("The wound is the place where the Light enters you.", "Rumi"),
            Quote("Out of difficulties grow miracles.", "Jean de la Bruyère"),
            Quote("You are loved just as you are.", "Ram Dass"),
            Quote("Softly, navigate the storm. Your heart is an anchor.", "Unknown")
        )

        return when (category) {
            MoodCategory.POSITIVE -> positive.random()
            MoodCategory.NEUTRAL -> neutral.random()
            MoodCategory.NEGATIVE -> negative.random()
        }
    }
}
