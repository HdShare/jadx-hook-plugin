package me.hd.jadx.plugins

import jadx.api.plugins.options.impl.BasePluginOptionsBuilder

class HookPluginOptions : BasePluginOptionsBuilder() {
	var isEnabled: Boolean = false

	override fun registerOptions() {
		boolOption(HookPlugin.PLUGIN_ID + ".enable")
			.description("启用")
			.defaultValue(true)
			.setter { v: Boolean -> isEnabled = v }
	}
}
