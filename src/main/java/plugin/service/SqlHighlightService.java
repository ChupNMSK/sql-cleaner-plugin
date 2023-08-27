package plugin.service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public final class SqlHighlightService {

    public TextRange highlightSQLQueryBoundary(LogicalPosition start,
                                                LogicalPosition end,
                                                TextAttributes textAttributes,
                                                Editor editor) {

        int startOffset = editor.logicalPositionToOffset(start);
        int endOffset = editor.logicalPositionToOffset(end);
        var highlightedRange = new TextRange(startOffset, endOffset);

        MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter = markupModel.addRangeHighlighter(
                highlightedRange.getStartOffset(),
                highlightedRange.getEndOffset(),
                HighlighterLayer.SELECTION - 1,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE
        );

        highlighter.setGreedyToLeft(true);
        highlighter.setGreedyToRight(true);

        return highlightedRange;
    }

    //let's assume the SQL query is enclosed between keywords and ends with a semicolon.
    public Optional<LogicalPosition> findStartPointOfSQLQuery(EditorMouseEvent event) {
        var document = event.getEditor().getDocument();

        int eventLine = event.getLogicalPosition().line;
        int endOffset = event.getOffset();

        for (int currentLine = eventLine; currentLine >= 0; currentLine--) {
            String line = getLineText(currentLine, document);

            if (line.isEmpty()) {
                return Optional.empty();
            }

            if (currentLine == eventLine) {
                line = document.getText(new TextRange(document.getLineStartOffset(currentLine), endOffset));
            }

            if (isSQLQueryBoundary(line)) {
                return Optional.of(new LogicalPosition(currentLine, 0));
            }

            // Reached previous query (probably syntax error)
            if (isEndOfSQLQuery(line)) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public Optional<LogicalPosition> findEndPointOfSQLQuery(EditorMouseEvent event) {
        var document = event.getEditor().getDocument();

        int eventLine = event.getLogicalPosition().line;
        int startOffset = event.getOffset();

        for (int currentLine = eventLine; currentLine <= document.getLineCount(); currentLine++) {
            String fullLine = getLineText(currentLine, document);

            if (fullLine.isEmpty()) {
                return Optional.empty();
            }

            String line = fullLine;

            if (currentLine == eventLine) {
                line = document.getText(new TextRange(startOffset, document.getLineEndOffset(currentLine)));
            }

            if (isEndOfSQLQuery(line)) {
                int editorTabSize = EditorUtil.getTabSize(event.getEditor());
                return Optional.of(new LogicalPosition(currentLine, indexOfSemicolon(fullLine, editorTabSize)));
            }

            // Reached next query (probably missing semicolon or syntax error)
            if (isSQLQueryBoundary(line)) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private String getLineText(int line, Document document) {
        if (line >= document.getLineCount()) {
            return "";
        }

        return document.getText(new TextRange(document.getLineStartOffset(line),
                document.getLineEndOffset(line)));
    }

    //todo: need to refactor
    private boolean isSQLQueryBoundary(String lineText) {
        return containsIgnoreCase(lineText, "INSERT")
                || containsIgnoreCase(lineText, "DELETE")
                || containsIgnoreCase(lineText, "CREATE TABLE");
    }

    private boolean isEndOfSQLQuery(String lineText) {
        return lineText.contains(";");
    }


    private int indexOfSemicolon(String string, int editorTabSize) {
        int currentTabCount = 0;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c == '\t') {
                currentTabCount++;
            } else if (c == ';') {
                // Found the semicolon
                return (currentTabCount * editorTabSize) - currentTabCount + i + 1;
            }
        }
        return -1; // Semicolon not found
    }

    private boolean containsIgnoreCase(String mainString, String subString) {
        Pattern pattern = Pattern.compile(Pattern.quote(subString), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(mainString);
        return matcher.find();
    }
}
