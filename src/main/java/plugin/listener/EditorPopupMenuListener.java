package plugin.listener;

import static com.intellij.openapi.actionSystem.IdeActions.GROUP_EDITOR_POPUP;
import static plugin.config.PluginConstants.FORMATTING_EDITOR_POPUP_GROUP;
import static plugin.util.DocumentUtil.isSqlDocument;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;

public class EditorPopupMenuListener implements EditorMouseListener {

    @Override
    public void mouseReleased(@NotNull EditorMouseEvent event) {
        if (!event.getMouseEvent().isPopupTrigger()) {
            return;
        }

        var editor = event.getEditor();
        var editorPopup = (DefaultActionGroup) ActionManager.getInstance().getAction(GROUP_EDITOR_POPUP);
        var formattingGroup = (DefaultActionGroup) ActionManager.getInstance().getAction(FORMATTING_EDITOR_POPUP_GROUP);

        if (!isSqlDocument(editor.getDocument()) && editorPopup.containsAction(formattingGroup)) {
            editorPopup.remove(formattingGroup);
            return;
        }

        if(isSqlDocument(editor.getDocument()) && !editorPopup.containsAction(formattingGroup)) {
            editorPopup.add(formattingGroup, Constraints.FIRST);
        }
    }
}
