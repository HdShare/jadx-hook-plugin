package me.hd.jadx.plugins.action

import jadx.api.metadata.ICodeAnnotation
import jadx.api.plugins.JadxPluginContext

object ViewAction {
	fun addPopupMenu(context: JadxPluginContext) {
		val guiContext = context.guiContext ?: return
		val decompiler = context.decompiler ?: return
		guiContext.addPopupMenuAction(
			"复制为 KavaRef 片段",
			{ nodeRef ->
				when (nodeRef?.annType) {
					ICodeAnnotation.AnnType.CLASS,
					ICodeAnnotation.AnnType.METHOD,
					ICodeAnnotation.AnnType.FIELD -> true

					else -> false
				}
			},
			null,
			{ nodeRef ->
				val node = decompiler.getJavaNodeByRef(nodeRef)
				val code = KavaRefCodeAction.getCode(node)
				guiContext.copyToClipboard(code)
			}
		)
	}
}
