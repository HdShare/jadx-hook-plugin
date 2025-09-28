package me.hd.jadx.plugins.action

import jadx.api.plugins.JadxPluginContext

object ViewAction {
	fun addPopupMenu(context: JadxPluginContext) {
		val guiContext = context.guiContext ?: return
		val decompiler = context.decompiler ?: return
		DescriptorAction.addMenu(guiContext, decompiler)
		KavaRefCodeAction.addMenu(guiContext, decompiler)
	}
}
