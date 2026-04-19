package com.wellness.companion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun ReflectionCard(
    questions: List<String>,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onQuestionClick: ((String) -> Unit)? = null,
) {
    AnimatedVisibility(
        visible = visible && questions.isNotEmpty(),
        enter = fadeIn() + slideInVertically { it / 2 },
        modifier = modifier,
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Sit with this",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                if (onQuestionClick != null) {
                    Text(
                        "Tap a question to write about it",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                    )
                }
                Spacer(Modifier.height(12.dp))
                questions.forEachIndexed { i, question ->
                    if (i > 0) Spacer(Modifier.height(10.dp))
                    val rowModifier = if (onQuestionClick != null) {
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onQuestionClick(question) }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    } else {
                        Modifier.fillMaxWidth()
                    }
                    Row(
                        modifier = rowModifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            question,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontStyle = FontStyle.Italic,
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f),
                        )
                        if (onQuestionClick != null) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Write about this",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReframeCard(
    text: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible && text.isNotBlank(),
        enter = fadeIn() + slideInVertically { it / 2 },
        modifier = modifier,
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Another angle",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                    ),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
    }
}
