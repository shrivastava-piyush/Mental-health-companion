package com.wellness.companion.ui.journal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wellness.companion.di.AppContainer
import com.wellness.companion.di.ViewModelFactories

@Composable
fun JournalEditorScreen(
    container: AppContainer,
    entryId: Long,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
) {
    val vm: JournalEditorViewModel = viewModel(
        key = "editor-$entryId",
        factory = ViewModelFactories.journalEditor(container, entryId),
    )
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == 0L) "New entry" else "Edit entry") },
                navigationIcon = {
                    IconButton(onClick = {
                        vm.save { onBack() }
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Save and back")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.save { /* stay */ } }) {
                        Icon(Icons.Outlined.Save, contentDescription = "Save")
                    }
                    if (state.id > 0L) {
                        IconButton(onClick = { vm.delete { onBack() } }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = contentPadding.calculateBottomPadding()),
        ) {
            BasicTextField(
                value = state.title,
                onValueChange = vm::onTitleChange,
                textStyle = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                singleLine = true,
                decorationBox = { inner ->
                    if (state.title.isEmpty()) {
                        Text(
                            "Title",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    inner()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            )
            BasicTextField(
                value = state.body,
                onValueChange = vm::onBodyChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                decorationBox = { inner ->
                    if (state.body.isEmpty()) {
                        Text(
                            "Write freely…",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    inner()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )
        }
    }
}
