package plugin.listener;

import static com.intellij.openapi.actionSystem.ActionPlaces.EDITOR_TAB;
import static com.intellij.openapi.editor.markup.EffectType.BOXED;
import static java.awt.event.MouseEvent.BUTTON3;
import static plugin.action.FormatSelectedSqlAction.FORMAT_SELECTED_ACTION;
import static plugin.config.PluginConfig.HOVER_BORDER_COLOR;
import static plugin.config.PluginConfig.SELECTED_BORDER_COLOR;
import static plugin.config.PluginConstants.FORMATTING_EDITOR_POPUP_GROUP;
import static plugin.config.PluginConstants.QUERY_POSITION_KEY;

import java.awt.Font;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.editor.event.EditorMouseEventArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import plugin.service.SqlHighlightService;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlHighlightingListener implements EditorMouseMotionListener, EditorMouseListener {

    private static final TextAttributes HOVER_ATTRIBUTE = new TextAttributes(
            null, null, HOVER_BORDER_COLOR, BOXED, Font.PLAIN);

    private static final TextAttributes SELECTED_ATTRIBUTE = new TextAttributes(
            null, null, SELECTED_BORDER_COLOR, BOXED, Font.PLAIN);

    private final SqlHighlightService highlightService = ApplicationManager.getApplication()
            .getService(SqlHighlightService.class);

    private TextRange highlightedRange;
    private LogicalPosition previousStart;
    private LogicalPosition previousEnd;

    public static void listen(Editor editor) {
        var highlighter = new SqlHighlightingListener();

        editor.addEditorMouseMotionListener(highlighter);
        editor.addEditorMouseListener(highlighter);
    }


    @Override
    public void mouseMoved(@NotNull EditorMouseEvent event) {
        if (event.isOverText() && isInEditingArea(event)) {
            var startOpt = highlightService.findStartPointOfSQLQuery(event);
            var endOpt = highlightService.findEndPointOfSQLQuery(event);

            if (startOpt.isEmpty() || endOpt.isEmpty()) {
                clearCurrentHighlight(event);
                return;
            }

            var start = startOpt.get();
            var end = endOpt.get();

            if (isWithinPreviousBoundaries(start, end)) {
                return;
            }

            clearCurrentHighlight(event);

            highlightedRange = highlightService.highlightSQLQueryBoundary(start, end, HOVER_ATTRIBUTE, event.getEditor());

            // Update previous boundaries
            previousStart = start;
            previousEnd = end;
        }
    }

    private boolean isWithinPreviousBoundaries(LogicalPosition start, LogicalPosition end) {
        if (previousStart == null || previousEnd == null) {
            return false;
        }
        return start.line >= previousStart.line
                && end.line <= previousEnd.line
                && end.column <= previousEnd.column;
    }


    @Override
    public void mouseReleased(@NotNull EditorMouseEvent event) {
        removeAction();

        if (!event.isOverText()) {
            clearCurrentHighlight(event);
        }

        if (previousStart == null && previousEnd == null) {
            return;
        }

        if (event.getMouseEvent().getButton() == BUTTON3 && isInEditingArea(event)) {
            var start = previousStart;
            var end = previousEnd;

            highlightedRange = highlightService.highlightSQLQueryBoundary(start, end, SELECTED_ATTRIBUTE, event.getEditor());

            addAction(event, start, end);
        }
    }

    private boolean isInEditingArea(EditorMouseEvent event) {
        return event.getArea() == EditorMouseEventArea.EDITING_AREA;
    }

    //adds action to editor popup
    private void addAction(EditorMouseEvent event, LogicalPosition start, LogicalPosition end) {
        if (event.getMouseEvent().isPopupTrigger()) {
            var editorMenu = (DefaultActionGroup) ActionManager.getInstance().getAction(FORMATTING_EDITOR_POPUP_GROUP);
            var action = ActionManager.getInstance().getAction(FORMAT_SELECTED_ACTION);

            if (editorMenu.containsAction(action)) {
                return;
            }

            DataContext dataContext = SimpleDataContext.builder()
                    .add(DataKey.create(QUERY_POSITION_KEY), Pair.create(start, end))
                    .build();

            // Pass the DataContext to the state
            var actionEvent = AnActionEvent.createFromDataContext(EDITOR_TAB, null, dataContext);

            action.beforeActionPerformedUpdate(actionEvent);
            editorMenu.add(action);
        }
    }

    //remove action from editor popup
    private void removeAction() {
        var editorMenu = (DefaultActionGroup) ActionManager.getInstance().getAction(FORMATTING_EDITOR_POPUP_GROUP);
        var action = ActionManager.getInstance().getAction(FORMAT_SELECTED_ACTION);

        if (editorMenu.containsAction(action)) {
            editorMenu.remove(action);
            log.info("Action removed");
        }
    }

    private void clearCurrentHighlight(EditorMouseEvent event) {
        var currentEditor = event.getEditor();
        if (highlightedRange != null) {
            currentEditor.getMarkupModel().removeAllHighlighters();
            highlightedRange = null;
            previousStart = null;
            previousEnd = null;
        }
    }
}