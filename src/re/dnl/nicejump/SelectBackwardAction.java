// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package niceJump.src.re.dnl.nicejump;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindResult;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.FoldingModelImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class SelectBackwardAction extends AnAction {

  protected Project project;
  protected Editor editor;
  protected FindModel findModel;
  protected FindManager findManager;
  protected DocumentImpl document;
  protected FoldingModelImpl foldingModel;
  protected CaretModel caretModel;
  protected SelectionModel selectionModel;

  @Override
  public void update(AnActionEvent e) {
    final Project project = e.getData(CommonDataKeys.PROJECT);
    final Editor editor = e.getData(CommonDataKeys.EDITOR);
    e.getPresentation().setVisible(project != null && editor != null);
  }

  private void initializeFields(AnActionEvent e) {
    project = e.getData(CommonDataKeys.PROJECT);
    editor = e.getData(CommonDataKeys.EDITOR);
    document = (DocumentImpl)editor.getDocument();
    foldingModel = (FoldingModelImpl)editor.getFoldingModel();
    caretModel = editor.getCaretModel();
    findManager = FindManager.getInstance(project);
    selectionModel = editor.getSelectionModel();
  }

  @Override
  public void actionPerformed(AnActionEvent actionEvent) {
    initializeFields(actionEvent);

    String searchText = getSearchText();
    if (searchText != null) {
      FindResult result = findManager.findString(
        document.getCharsSequence(),
        caretModel.getOffset(),
        createFindModel(searchText, false));

      performCaretAction(result);
    }
  }

  private void performCaretAction(FindResult result) {
    selectionModel.setSelection(caretModel.getOffset(), result.getStartOffset());
  }

  private FindModel createFindModel(String searchText, boolean searchForward) {
    FindModel clone = findManager.getFindInFileModel().clone();

    clone.setForward(searchForward);
    clone.setStringToFind(searchText);

    clone.setFindAll(true);
    clone.setFromCursor(true);
    clone.setRegularExpressions(false);
    clone.setWholeWordsOnly(false);
    clone.setCaseSensitive(false);
    clone.setSearchHighlighters(true);
    clone.setPreserveCase(false);

    return clone;
  }

  private String getSearchText() {
    return Messages.showInputDialog(project, "What do you want to search for?", "Input Search Query", Messages.getQuestionIcon());
  }
}
