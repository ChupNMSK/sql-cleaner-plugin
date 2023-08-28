package plugin.action;

import static java.util.Objects.isNull;

import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import lombok.extern.slf4j.Slf4j;
import plugin.parser.SQLQueryIterator;
import plugin.parser.SqlParser;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;

@Slf4j
public class FormatWholeSqlFileAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Get the currently active editor
        var project = e.getProject();
        if (isNull(project)) {
            return;
        }

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (isNull(editor)) {
            return;
        }

        var document = editor.getDocument();

        // Get the selected text or the entire content
        var selectedText = editor.getSelectionModel().getSelectedText();
        if (isNull(selectedText)) {
            selectedText = document.getText();
        }

        // Format the SQL code (simplified example)
        log.debug("Before formatting {} ", selectedText);
        var formattedSql = formatSql(selectedText);
        log.debug("After formatting {} ", formattedSql);

        // Replace the content of the editor with the formatted SQL within a write action
        CommandProcessor.getInstance().executeCommand(
                e.getProject(),
                () ->  WriteAction.run(() -> document.setText(formattedSql)),
                "Format All SQL",
                "Format All SQL Group"
        );
    }

    private String formatSql(String sql) {
        return SQLQueryIterator.streamOf(sql)
                .map(SqlParser::parseAndFormat)
                .collect(Collectors.joining("\n\n"));
    }
}
