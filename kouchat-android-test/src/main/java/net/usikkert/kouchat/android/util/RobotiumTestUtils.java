
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.android.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.robotium.solo.Solo;

import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Utilities for tests.
 *
 * @author Christian Ihle
 */
public final class RobotiumTestUtils {

    private RobotiumTestUtils() {

    }

    /**
     * Adds a line of text to the first edittext field, and presses enter.
     *
     * @param solo The solo tester.
     * @param text The line of text to write.
     */
    public static void writeLine(final Solo solo, final String text) {
        solo.enterText(0, text);
        solo.sendKey(KeyEvent.KEYCODE_ENTER);
    }

    /**
     * Gets all the lines of text from a textview.
     *
     * @param fullText The full text from the textview. This is a separate parameter to avoid memory issues
     *                 with repeated calls to {@link TextView#getText()}.
     * @param textView The textview to get all the lines of text from.
     * @return All the lines if text in the textview. Each list item is one line.
     */
    public static List<String> getAllLinesOfText(final String fullText, final TextView textView) {
        final List<String> allLines = new ArrayList<String>();

        final Layout layout = textView.getLayout();
        final int lineCount = layout.getLineCount();

        for (int currentLineNumber = 0; currentLineNumber < lineCount; currentLineNumber++) {
            allLines.add(getLineOfText(fullText, currentLineNumber, layout));
        }

        return allLines;
    }

    /**
     * Gets the text on the given line from the full text of a textview.
     *
     * @param fullText The full text from a textview.
     * @param lineNumber The line number in the textview to get the text from.
     * @param layout The layout of the textview.
     * @return The text found on the given line.
     */
    public static String getLineOfText(final String fullText, final int lineNumber, final Layout layout) {
        final int lineStart = layout.getLineStart(lineNumber);
        final int lineEnd = layout.getLineEnd(lineNumber);

        return fullText.substring(lineStart, lineEnd);
    }

    /**
     * Checks if the text is currently visible in the scrollview.
     *
     * @param solo The solo tester.
     * @param textViewId Id of the textview with the text to check.
     * @param scrollViewId Id of the scrollview that contains the textview.
     * @param textToFind The text to check if it's visible.
     * @return If the text is currently visible.
     * @throws IllegalArgumentException If the text is not not found.
     */
    public static boolean textIsVisible(final Solo solo, final int textViewId, final int scrollViewId, final String textToFind) {
        final TextView textView = (TextView) solo.getView(textViewId);
        final ScrollView scrollView = (ScrollView) solo.getView(scrollViewId);

        final Rect visibleScrollArea = getVisibleScrollArea(scrollView);
        final String fullText = textView.getText().toString();
        final List<String> allLinesOfText = getAllLinesOfText(fullText, textView);
        final List<Line> matchingLinesOfText = getMatchingLinesOfText(fullText, allLinesOfText, textToFind);

        for (final Line matchingLine : matchingLinesOfText) {
            final Point coordinatesForLine = getCoordinatesForLine(textView, matchingLine.getLineText(),
                    matchingLine.getLineNumber(), allLinesOfText.get(matchingLine.getLineNumber()));

            if (!visibleScrollArea.contains(coordinatesForLine.x, coordinatesForLine.y)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets all the lines of text matching text to find.
     *
     * @param fullText The full text to search in.
     * @param allLinesOfText The full text, split in lines.
     * @param textToFind The text to find the lines for.
     * @return List containing the text to find, with the exact part of the text found on each line,
     *         and which line number that part of the text was found at.
     * @throws IllegalArgumentException If the text to find is not located in the full text.
     */
    public static List<Line> getMatchingLinesOfText(final String fullText, final List<String> allLinesOfText, final String textToFind) {
        final int textToFindIndex = fullText.lastIndexOf(textToFind);

        if (textToFindIndex < 0) {
            throw new IllegalArgumentException("Could not find: " + textToFind);
        }

        final int startLine = findStartLine(allLinesOfText, textToFindIndex);
        final List<Line> matchingLines = new ArrayList<Line>();
        final List<String> wordsFromTextToFind = splitOnBoundaries(textToFind);
        removeEmptyFirstWord(wordsFromTextToFind);

        for (int currentLineNumber = startLine; currentLineNumber < allLinesOfText.size(); currentLineNumber++) {
            addMatchingLine(allLinesOfText.get(currentLineNumber), currentLineNumber, wordsFromTextToFind, matchingLines);

            if (wordsFromTextToFind.isEmpty()) {
                break;
            }
        }

        return matchingLines;
    }

    private static int findStartLine(final List<String> allLinesOfText, final int textToFindIndex) {
        int startLine = 0;
        int currentIndex = 0;

        for (final String line : allLinesOfText) {
            if (currentIndex + line.length() >= textToFindIndex) {
                break;
            }

            startLine++;
            currentIndex += line.length();
        }

        return startLine;
    }

    private static List<String> splitOnBoundaries(final String text) {
        return new ArrayList<String>(Arrays.asList(text.split("\\b")));
    }

    private static void removeEmptyFirstWord(final List<String> words) {
        if (words.get(0).equals("")) {
            words.remove(0);
        }
    }

    private static void addMatchingLine(final String currentLine, final int currentLineNumber,
                                        final List<String> wordsFromTextToFind, final List<Line> matchingLines) {
        String wordFromTextToFind = wordsFromTextToFind.get(0);

        if (!currentLine.contains(wordFromTextToFind)) {
            return;
        }

        final String currentLineStartingAtWord = currentLine.substring(currentLine.indexOf(wordFromTextToFind));
        final List<String> wordsFromCurrentLine = splitOnBoundaries(currentLineStartingAtWord);
        removeEmptyFirstWord(wordsFromCurrentLine);

        String wordFromCurrentLine = wordsFromCurrentLine.remove(0);
        wordFromTextToFind = wordsFromTextToFind.remove(0);
        final StringBuilder matchingLine = new StringBuilder();

        while (wordFromTextToFind.equals(wordFromCurrentLine)) {
            matchingLine.append(wordFromCurrentLine);

            if (wordsFromTextToFind.isEmpty() || wordsFromCurrentLine.isEmpty()) {
                break;
            }

            wordFromTextToFind = wordsFromTextToFind.remove(0);
            wordFromCurrentLine = wordsFromCurrentLine.remove(0);
        }

        matchingLines.add(new Line(currentLineNumber, matchingLine.toString()));
    }

    private static Rect getVisibleScrollArea(final ScrollView scrollView) {
        final int[] locationOnScreen = new int[2];

        scrollView.getLocationOnScreen(locationOnScreen);

        return new Rect(
                locationOnScreen[0], // left position
                locationOnScreen[1], // top position
                locationOnScreen[0] + scrollView.getWidth(), // right position
                locationOnScreen[1] + scrollView.getHeight()); // bottom position
    }

    private static Point getCoordinatesForLine(final TextView textView, final String textToFind,
                                               final int lineNumber, final String fullLine) {
        final Layout layout = textView.getLayout();
        final TextPaint paint = textView.getPaint();

        final int textIndex = fullLine.indexOf(textToFind);
        final String preText = fullLine.substring(0, textIndex);

        final int textWidth = (int) Layout.getDesiredWidth(textToFind, paint);
        final int preTextWidth = (int) Layout.getDesiredWidth(preText, paint);

        final int[] textViewXYLocation = new int[2];
        textView.getLocationOnScreen(textViewXYLocation);

        // Width: in the middle of the text
        final int xPosition = preTextWidth + (textWidth / 2);
        // Height: in the middle of the given line, plus the text view position from the top, minus the amount scrolled
        final int yPosition = layout.getLineBaseline(lineNumber) + textViewXYLocation[1] - textView.getScrollY();

        return new Point(xPosition, yPosition);
    }
}
