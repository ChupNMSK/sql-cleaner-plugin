package plugin.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentUtil {

    private static final String SQL_EXTENSION = "sql";

    public static boolean isSqlDocument(Document document) {
        return SQL_EXTENSION.equals(getExtension(document));
    }

    private static String getExtension(Document document) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

        if (virtualFile == null) {
            return "";
        }

        return virtualFile.getExtension();
    }
}
