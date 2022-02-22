package com.lanshifu.plugin

import com.android.build.gradle.AppExtension
import com.didiglobal.booster.gradle.getAndroid
import com.lanshifu.plugin.extension.println
import com.lanshifu.plugin.transform.CommonTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author lanxiaobin
 * @date 2021/11/13
 */
class Plugin : Plugin<Project> {
    override fun apply(project: Project) {

        when {
            project.plugins.hasPlugin("com.android.application") ||
                    project.plugins.hasPlugin("com.android.dynamic-feature") -> {

                project.getAndroid<AppExtension>().let { androidExt ->

                    androidExt.registerTransform(
                        CommonTransform(project)
                    )

                    /**
                     * 所有项目的build.gradle执行完毕
                     * wiki:https://juejin.im/post/6844903607679057934
                     *
                     * **/
                    project.gradle.projectsEvaluated {
                        "===projectsEvaluated===".println()
                        androidExt.applicationVariants.forEach { variant ->
//                            PluginConfigProcessor(project).process(variant)
                        }

                    }
                }

            }

            project.plugins.hasPlugin("com.android.library") -> {

            }
        }


    }
}