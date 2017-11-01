// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package niceJump.src.re.dnl.nicejump;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindResult;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.FoldingModelImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class MoveAction extends AnAction {

  protected Project project;
  protected Editor editor;
  protected FindModel findModel;
  protected FindManager findManager;
  protected DocumentImpl document;
  protected FoldingModelImpl foldingModel;
  protected DataContext dataContext;
  protected CaretModel caretModel;

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
    dataContext = e.getDataContext();
    caretModel = editor.getCaretModel();
    findManager = FindManager.getInstance(project);
    findModel = createFindModel(findManager);
  }

  @Override
  public void actionPerformed(AnActionEvent actionEvent) {
    initializeFields(actionEvent);
    int offset = caretModel.getOffset();
    String searchText =
      Messages.showInputDialog(project, "What do you want to search for?", "Input Search Query", Messages.getQuestionIcon());
    if (searchText != null) {
      findModel.setStringToFind(searchText);
      FindResult result = findManager.findString(document.getCharsSequence(), offset, findModel);
      int resultStartOffset = result.getStartOffset();
      caretModel.moveToOffset(resultStartOffset);
    }
  }

  protected FindModel createFindModel(FindManager findManager) {
    FindModel clone = findManager.getFindInFileModel().clone();
    clone.setFindAll(true);
    clone.setFromCursor(true);
    clone.setForward(true);
    clone.setRegularExpressions(false);
    clone.setWholeWordsOnly(false);
    clone.setCaseSensitive(false);
    clone.setSearchHighlighters(true);
    clone.setPreserveCase(false);

    return clone;
  }
}
