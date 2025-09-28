package me.hd.jadx.plugins.action

import jadx.api.*
import jadx.api.metadata.ICodeAnnotation
import jadx.api.plugins.gui.JadxGuiContext

object DescriptorAction {
	fun addMenu(guiContext: JadxGuiContext, decompiler: JadxDecompiler) {
		guiContext.addPopupMenuAction(
			"复制为 Jvm 类型签名",
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
				val code = getDesc(node)
				guiContext.copyToClipboard(code)
			}
		)
	}

	private fun getDesc(javaNode: JavaNode?): String {
		return when (javaNode) {
			is JavaClass -> getClassDesc(javaNode)
			is JavaMethod -> getMethodDesc(javaNode)
			is JavaField -> getFieldDesc(javaNode)
			else -> ""
		}
	}

	private fun getClassDesc(javaNode: JavaClass): String {
		val node = javaNode.classNode
		val rawName = node.rawName
		return getTypeDesc(rawName)
	}

	private fun getMethodDesc(javaNode: JavaMethod): String {
		val node = javaNode.methodNode
		val classRawName = node.declaringClass.rawName
		val returnType = node.returnType
		val name = node.name
		val argTypes = node.argTypes
		return if (node.isConstructor) {
			buildString {
				append(getTypeDesc(classRawName))
				append("->")
				append("<init>")
				append(buildString {
					append("(")
					append(argTypes.joinToString("") { type -> getTypeDesc(type.toString()) })
					append(")V")
				})
			}
		} else {
			buildString {
				append(getTypeDesc(classRawName))
				append("->")
				append(name)
				append(buildString {
					append("(")
					append(argTypes.joinToString("") { type -> getTypeDesc(type.toString()) })
					append(")")
					append(getTypeDesc(returnType.toString()))
				})
			}
		}
	}

	private fun getFieldDesc(javaNode: JavaField): String {
		val node = javaNode.fieldNode
		val classRawName = node.declaringClass.rawName
		val name = node.name
		val type = node.type
		return buildString {
			append(getTypeDesc(classRawName))
			append("->")
			append(name)
			append(":")
			append(getTypeDesc(type.toString()))
		}
	}

	private fun getTypeDesc(name: String): String {
		if (name.endsWith("[]")) return "[" + getTypeDesc(name.substring(0, name.length - 2))
		val primitiveMap = mapOf(
			"boolean" to "Z",
			"char" to "C",
			"byte" to "B",
			"short" to "S",
			"int" to "I",
			"float" to "F",
			"long" to "J",
			"double" to "D",
			"void" to "V"
		)
		return primitiveMap[name] ?: ("L" + name.replace('.', '/') + ";")
	}
}
