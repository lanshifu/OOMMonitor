package com.lanshifu.plugin.classtransformer

import com.didiglobal.booster.kotlinx.file
import com.didiglobal.booster.kotlinx.touch
import com.didiglobal.booster.transform.TransformContext
import org.objectweb.asm.tree.ClassNode
import java.io.PrintWriter


/**
 * @author lanxiaobin
 * @date 2021/11/11
 */
class ImageViewClassTransformer : AbsClassTransformer() {
    private lateinit var logger: PrintWriter

    override fun onPreTransform(context: TransformContext) {
        super.onPreTransform(context)
        this.logger = context.reportsDir.file("ImageViewClassTransformer").file(context.name)
            .file("report.txt").touch().printWriter()
        logger.println("--start-- ${System.currentTimeMillis()}")

    }

    override fun onPostTransform(context: TransformContext) {
        logger.println("\n --end-- ${System.currentTimeMillis()}")
        this.logger.close()
    }

    override fun transform(context: TransformContext, klass: ClassNode) = klass.also {

        if (onCommInterceptor(context, klass)) {
            return klass
        }

        if (!klass.name.equals(MONITOR_SUPER_CLASS_NAME) && klass.superName.equals(IMAGE_VIEW_CLASS_NAME)) {
            klass.superName = MONITOR_SUPER_CLASS_NAME
            logger.println("\n ImageViewClassTransformer hook")
            print("\n ImageViewClassTransformer hook ${System.currentTimeMillis()}")
        }
    }

}

private const val MONITOR_SUPER_CLASS_NAME = "com/lanshifu/asm_plugin_library/imageview/MonitorImageView"
private const val IMAGE_VIEW_CLASS_NAME = "android/widget/ImageView"