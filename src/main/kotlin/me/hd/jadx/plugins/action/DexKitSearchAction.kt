package me.hd.jadx.plugins.action

import jadx.api.JadxDecompiler
import jadx.api.plugins.gui.JadxGuiContext
import me.hd.jadx.plugins.ui.DexKitSearchDialog

object DexKitSearchAction {
	fun addMenuAction(guiContext: JadxGuiContext, decompiler: JadxDecompiler) {
		guiContext.addMenuAction("DexKit Search") {
			DexKitSearchDialog.showDialog(guiContext.mainFrame, decompiler)
		}
	}
}
