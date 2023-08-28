package plugin.action;

import static java.util.Objects.isNull;
import static plugin.config.PluginConstants.QUERY_POSITION_KEY;

import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;

import lombok.extern.slf4j.Slf4j;
import plugin.parser.SQLQueryIterator;
import plugin.parser.SqlParser;

@Slf4j
public class FormatSelectedSqlAction extends AnAction {

    public static final String FORMAT_SELECTED_ACTION = "FormatSelected";

    private Pair<LogicalPosition, LogicalPosition> sqlPosition;

    @Override
    public void actionPerformed(AnActionEvent e) {
        log.debug("On selected sql event action {}", e);

        var project = e.getProject();
        if (isNull(project)) {
            return;
        }

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (isNull(editor)) {
            return;
        }

        int startOffset = editor.logicalPositionToOffset(sqlPosition.getFirst());
        int endOffset = editor.logicalPositionToOffset(sqlPosition.getSecond());



        var targetSql = editor.getDocument()
                                .getText(new TextRange(startOffset, endOffset));

        var formattedSql = SQLQueryIterator.streamOf(targetSql)
                                                .map(SqlParser::parseAndFormat)
                                                .collect(Collectors.joining());

        //todo: need to check if break line needs to be added after/before sql

        CommandProcessor.getInstance().executeCommand(
                e.getProject(),
                () ->  WriteAction.run(() -> editor.getDocument().replaceString(startOffset, endOffset, formattedSql)),
                "Format Selected SQL",
                "Format Selected Group"
        );

        log.debug("SQl formatting:\n before {}\n after{}", targetSql, formattedSql);
    }

    @Override
    public void beforeActionPerformedUpdate(@NotNull AnActionEvent e) {
        Optional.ofNullable(extractPosition(e.getDataContext()))
                .ifPresent(positionPair ->
                        this.sqlPosition = positionPair
                );
    }

    @SuppressWarnings("unchecked")
    private Pair<LogicalPosition, LogicalPosition> extractPosition(DataContext context) {
        return (Pair<LogicalPosition, LogicalPosition>) context.getData(QUERY_POSITION_KEY);
    }
}
