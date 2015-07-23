package com.cleverCloud.cleverIdea.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import git4idea.GitVcs

public class cloneGitRepository : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // TODO: insert action logic here
    }

    override fun update(e: AnActionEvent?) {
        val plvm = ProjectLevelVcsManager.getInstance(e!!.getProject())

        if (plvm.checkVcsIsActive(GitVcs.NAME) == false)
            e.getPresentation().setEnabled(false)
        else
            e.getPresentation().setEnabled(true)

        // Todo : adapt text (clone/pull) depending of the remote
    }
}
