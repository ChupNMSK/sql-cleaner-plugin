package plugin.listener;

import static plugin.util.DocumentUtil.isSqlDocument;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditorListener implements EditorFactoryListener {

    @Override
    public void editorCreated(EditorFactoryEvent event) {
        Editor editor = event.getEditor();

        if(isSqlDocument(editor.getDocument())) {
            SqlHighlightingListener.listen(editor);
        }
    }
}