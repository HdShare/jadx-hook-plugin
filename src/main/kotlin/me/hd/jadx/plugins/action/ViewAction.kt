package me.hd.jadx.plugins.action

import jadx.api.JadxDecompiler
import jadx.api.plugins.JadxPluginContext
import jadx.api.plugins.gui.JadxGuiContext

object ViewAction {
	fun addMenu(context: JadxPluginContext) {
		val guiContext = context.guiContext ?: return
		val decompiler = context.decompiler ?: return
		addPopupMenu(guiContext, decompiler)
	}

	private fun addPopupMenu(guiContext: JadxGuiContext, decompiler: JadxDecompiler) {
		DescriptorAction.addPopupMenu(guiContext, decompiler)
		KavaRefCodeAction.addPopupMenu(guiContext, decompiler)
	}
}
