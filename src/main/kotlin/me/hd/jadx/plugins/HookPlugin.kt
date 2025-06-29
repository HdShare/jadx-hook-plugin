package me.hd.jadx.plugins

import jadx.api.plugins.JadxPlugin
import jadx.api.plugins.JadxPluginContext
import jadx.api.plugins.JadxPluginInfo
import jadx.api.plugins.JadxPluginInfoBuilder
import me.hd.jadx.plugins.action.ViewAction

class HookPlugin : JadxPlugin {
	companion object {
		const val PLUGIN_ID = "jadx-hook-plugin"
	}

	override fun getPluginInfo(): JadxPluginInfo {
		return JadxPluginInfoBuilder.pluginId(PLUGIN_ID)
			.name("Jadx Hook Plugin")
			.description("Jadx Hook Plugin")
			.build()
	}

	override fun init(context: JadxPluginContext) {
		val options = HookPluginOptions().apply { context.registerOptions(this) }
		if (options.isEnabled) {
			ViewAction.addPopupMenu(context)
		}
	}
}
