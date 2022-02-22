package com.lanshifu.plugin.transform

import com.didiglobal.booster.transform.Transformer
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.lanshifu.plugin.asmtransformer.BaseAsmTransformer
import com.lanshifu.plugin.classtransformer.ImageViewClassTransformer
import com.lanshifu.plugin.classtransformer.ThreadTransformer
import org.gradle.api.Project

/**
 * @author lanxiaobin
 * @date 2021/11/13
 */
class CommonTransform(androidProject: Project) : BaseTransform(androidProject) {

    private val classTransformerList = mutableListOf<ClassTransformer>()
    init {
//        if (project.isReleaseTask()){
            classTransformerList.add(ThreadTransformer())
            classTransformerList.add(ImageViewClassTransformer())
//        }
    }

    override val transformers = listOf<Transformer>(
        BaseAsmTransformer(classTransformerList)
    )

    override fun getName(): String {
        return "CommonTransform"
    }

}
