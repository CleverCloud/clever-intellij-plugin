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

package com.cleverCloud.cleverIdea.settings

import com.cleverCloud.cleverIdea.api.json.Application
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

import java.util.ArrayList

/**
 * Project related configuration.
 */
@State(name = "CleverIdeaProjectSettings")
class ProjectSettings : PersistentStateComponent<ProjectSettings> {
    /**
     * Applications associated with the projet.
     */
    var applications = ArrayList<Application>()

    private var _lastUsedApplication: Application? = null

    /**
     * Last used application.
     */
    var lastUsedApplication: Application?
        get() {
            if (applications.contains(_lastUsedApplication))
                return _lastUsedApplication
            else {
                _lastUsedApplication = null
                return _lastUsedApplication
            }
        }
        set(value) {
            _lastUsedApplication = value
        }

    /**
     * @see PersistentStateComponent.getState
     */
    override fun getState(): ProjectSettings {
        return this
    }

    /**
     * @see PersistentStateComponent.loadState
     */
    override fun loadState(state: ProjectSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
