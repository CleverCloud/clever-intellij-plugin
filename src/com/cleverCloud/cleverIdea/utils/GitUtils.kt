/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Clever Cloud, SAS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cleverCloud.cleverIdea.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import git4idea.config.GitVcsApplicationSettings
import git4idea.config.GitVersion

/**
 * Utilities to get informations about Git
 */
object GitUtils {
    /**
     * @param project <code>project</code> which contains the git repository.
     * @return <code>true</code> if executable is valid, otherwise <code>false</code>.
     */
    fun testGitExecutable(project: Project): Boolean {
        val executable: String
        try {
            executable = GitVcsApplicationSettings.getInstance().pathToGit
        } catch (e: IllegalStateException) {
            Notification("Vcs Important Messages", "Git executable can't be found",
                    "The git executable cannot be found. Please check your settings.", NotificationType.INFORMATION).notify(project)
            return false
        }

        if (GitVersion.identifyVersion(executable).isSupported.not()) {
            Notification("Vcs Important Messages", "Git version unsupported",
                    "Your version of git is not supported.", NotificationType.INFORMATION).notify(project)
            return false
        }

        return true
    }
}
