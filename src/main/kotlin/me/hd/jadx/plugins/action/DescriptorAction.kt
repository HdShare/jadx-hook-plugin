package me.hd.jadx.plugins.action

import jadx.api.*
import jadx.api.metadata.ICodeAnnotation
import jadx.api.plugins.gui.JadxGuiContext
import jadx.core.dex.instructions.args.ArgType

object DescriptorAction {
	fun addPopupMenu(guiContext: JadxGuiContext, decompiler: JadxDecompiler) {
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
		return getClassNameDesc(rawName)
	}

	private fun getMethodDesc(javaNode: JavaMethod): String {
		val node = javaNode.methodNode
		val classRawName = node.declaringClass.rawName
		val returnType = node.returnType
		val name = node.name
		val argTypes = node.argTypes
		return if (node.isConstructor) {
			buildString {
				append(getClassNameDesc(classRawName))
				append("->")
				append("<init>")
				append(buildString {
					append("(")
					append(argTypes.joinToString("") { type -> getTypeDesc(type) })
					append(")V")
				})
			}
		} else {
			buildString {
				append(getClassNameDesc(classRawName))
				append("->")
				append(name)
				append(buildString {
					append("(")
					append(argTypes.joinToString("") { type -> getTypeDesc(type) })
					append(")")
					append(getTypeDesc(returnType))
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
			append(getClassNameDesc(classRawName))
			append("->")
			append(name)
			append(":")
			append(getTypeDesc(type))
		}
	}

	private fun getClassNameDesc(name: String): String {
		return "L" + name.replace('.', '/') + ";"
	}

	private fun getTypeDesc(type: ArgType): String {
		return when {
			type.isPrimitive -> when (type) {
				ArgType.BOOLEAN -> "Z"
				ArgType.CHAR -> "C"
				ArgType.BYTE -> "B"
				ArgType.SHORT -> "S"
				ArgType.INT -> "I"
				ArgType.FLOAT -> "F"
				ArgType.LONG -> "J"
				ArgType.DOUBLE -> "D"
				ArgType.VOID -> "V"
				else -> throw IllegalArgumentException("Unknown primitive type: $type")
			}

			type.isObject -> {
				when {
					type.isGeneric -> getTypeDesc(ArgType.`object`(type.`object`))
					type.isGenericType -> getClassNameDesc(Object::class.java.name)
					else -> getClassNameDesc(type.toString())
				}
			}

			type.isArray -> "[" + getTypeDesc(type.arrayElement)

			else -> throw IllegalArgumentException("Unsupported type: $type")
		}
	}
}
