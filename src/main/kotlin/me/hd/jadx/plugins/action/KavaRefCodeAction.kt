package me.hd.jadx.plugins.action

import jadx.api.*
import jadx.api.metadata.ICodeAnnotation
import jadx.api.plugins.gui.JadxGuiContext
import jadx.core.dex.instructions.args.ArgType
import jadx.core.dex.instructions.args.PrimitiveType

object KavaRefCodeAction {
	fun addPopupMenu(guiContext: JadxGuiContext, decompiler: JadxDecompiler) {
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
				val code = getCode(node)
				guiContext.copyToClipboard(code)
			}
		)
	}

	private fun getCode(javaNode: JavaNode?): String {
		return when (javaNode) {
			is JavaClass -> getClassCode(javaNode)
			is JavaMethod -> getMethodCode(javaNode)
			is JavaField -> getFieldCode(javaNode)
			else -> ""
		}
	}

	private fun getClassCode(javaNode: JavaClass): String {
		val node = javaNode.classNode
		val rawName = node.rawName
		return """
			"$rawName".toClass()
		"""
	}

	private fun getMethodCode(javaNode: JavaMethod): String {
		val node = javaNode.methodNode
		val returnType = getKavaRefType(node.returnType)
		val name = node.name
		val argTypes = node.argTypes.map(::getKavaRefType)
		return if (node.isConstructor) {
			"""
				firstConstructor {
					${if (argTypes.isEmpty()) "emptyParameters()" else "parameters(${argTypes.joinToString(", ")})"}
				}
			""".trimIndent()
		} else {
			"""
				firstMethod {
					returnType = $returnType
					name = "$name"
					${if (argTypes.isEmpty()) "emptyParameters()" else "parameters(${argTypes.joinToString(", ")})"}
				}
			""".trimIndent()
		}
	}

	private fun getFieldCode(javaNode: JavaField): String {
		val node = javaNode.fieldNode
		val type = getKavaRefType(node.type)
		val name = node.name
		return """
			firstField {
				type = $type
				name = "$name"
			}.get()
		""".trimIndent()
	}

	private fun getKavaRefType(type: ArgType): String {
		return when {
			type.isPrimitive -> when (type) {
				ArgType.BOOLEAN -> "Boolean::class"
				ArgType.CHAR -> "Char::class"
				ArgType.BYTE -> "Byte::class"
				ArgType.SHORT -> "Short::class"
				ArgType.INT -> "Int::class"
				ArgType.FLOAT -> "Float::class"
				ArgType.LONG -> "Long::class"
				ArgType.DOUBLE -> "Double::class"
				ArgType.VOID -> "Void.TYPE"
				else -> throw IllegalArgumentException("Unknown primitive type: $type")
			}

			type.isObject -> {
				when {
					type.isGeneric -> getKavaRefType(ArgType.`object`(type.`object`))
					type.isGenericType -> "Any::class"
					else -> when (type) {
						PrimitiveType.BOOLEAN.boxType -> "JBoolean::class"
						PrimitiveType.CHAR.boxType -> "JCharacter::class"
						PrimitiveType.BYTE.boxType -> "JByte::class"
						PrimitiveType.SHORT.boxType -> "JShort::class"
						PrimitiveType.INT.boxType -> "JInteger::class"
						PrimitiveType.FLOAT.boxType -> "JFloat::class"
						PrimitiveType.LONG.boxType -> "JLong::class"
						PrimitiveType.DOUBLE.boxType -> "JDouble::class"
						PrimitiveType.VOID.boxType -> "JVoid::class"
						ArgType.OBJECT -> "Any::class"
						ArgType.CLASS -> "Class::class"
						ArgType.STRING -> "String::class"
						ArgType.ENUM -> "Enum::class"
						ArgType.THROWABLE -> "Throwable::class"
						ArgType.ERROR -> "Error::class"
						ArgType.EXCEPTION -> "Exception::class"
						ArgType.RUNTIME_EXCEPTION -> "RuntimeException::class"
						ArgType.`object`(List::class.java.name) -> "List::class"
						ArgType.`object`(Map::class.java.name) -> "Map::class"
						else -> "\"$type\""
					}
				}
			}

			type.isArray -> when (type.arrayElement) {
				ArgType.BOOLEAN -> "BooleanArray::class"
				ArgType.CHAR -> "CharArray::class"
				ArgType.BYTE -> "ByteArray::class"
				ArgType.SHORT -> "ShortArray::class"
				ArgType.INT -> "IntArray::class"
				ArgType.FLOAT -> "FloatArray::class"
				ArgType.LONG -> "LongArray::class"
				ArgType.DOUBLE -> "DoubleArray::class"
				else -> "ArrayClass(${getKavaRefType(type.arrayElement)})"
			}

			else -> throw IllegalArgumentException("Unsupported type: $type")
		}
	}
}
