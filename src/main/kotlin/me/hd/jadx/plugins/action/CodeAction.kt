package me.hd.jadx.plugins.action

import com.highcapable.kavaref.extension.*
import jadx.api.JavaClass
import jadx.api.JavaField
import jadx.api.JavaMethod
import jadx.api.JavaNode
import jadx.core.dex.instructions.args.ArgType
import jadx.core.dex.instructions.args.PrimitiveType

object CodeAction {
	fun getHookCode(javaNode: JavaNode?): String {
		return when (javaNode) {
			is JavaClass -> getClassCode(javaNode)
			is JavaMethod -> getMethodCode(javaNode)
			is JavaField -> getFieldCode(javaNode)
			else -> ""
		}
	}

	private fun getClassCode(javaNode: JavaClass): String {
		val node = javaNode.classNode
		val className = node.name
		val classRawName = node.rawName
		return """
			val ${className}Clazz = "$classRawName".toClass()
		"""
	}

	private fun getMethodCode(javaNode: JavaMethod): String {
		val node = javaNode.methodNode
		val returnType = getKavaRefType(node.returnType)
		val name = node.name
		val argTypes = node.argTypes.map(::getKavaRefType)
		return if (node.isConstructor) {
			"""
				resolve().firstConstructor {
					${if (argTypes.isEmpty()) "emptyParameters()" else "parameters(${argTypes.joinToString(", ")})"}
				}
			""".trimIndent()
		} else {
			"""
				resolve().firstMethod {
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
			val field = resolve().firstField {
				type = $type
				name = "$name"
			}.get()
		""".trimIndent()
	}

	private fun getPrimitiveType(type: ArgType): String {
		return when (val name = type.primitiveType) {
			PrimitiveType.BOOLEAN -> "JBoolean.TYPE"
			PrimitiveType.CHAR -> "JCharacter.TYPE"
			PrimitiveType.BYTE -> "JByte.TYPE"
			PrimitiveType.SHORT -> "JShort.TYPE"
			PrimitiveType.INT -> "JInteger.TYPE"
			PrimitiveType.FLOAT -> "JFloat.TYPE"
			PrimitiveType.LONG -> "JLong.TYPE"
			PrimitiveType.DOUBLE -> "JDouble.TYPE"
			PrimitiveType.VOID -> "JVoid.TYPE"
			else -> "\"$name\""
		}
	}

	private fun getObjectType(type: ArgType): String {
		return when (val name = type.`object`) {
			JBoolean::class.java.name -> "JBoolean::class"
			JCharacter::class.java.name -> "JCharacter::class"
			JByte::class.java.name -> "JByte::class"
			JShort::class.java.name -> "JShort::class"
			JInteger::class.java.name -> "JInteger::class"
			JFloat::class.java.name -> "JFloat::class"
			JLong::class.java.name -> "JLong::class"
			JDouble::class.java.name -> "JDouble::class"
			JVoid::class.java.name -> "JVoid::class"
			Any::class.java.name -> "Any::class"
			String::class.java.name -> "String::class"
			CharSequence::class.java.name -> "CharSequence::class"
			else -> "\"$name\""
		}
	}

	private fun getKavaRefType(type: ArgType): String {
		return when {
			type.isPrimitive -> getPrimitiveType(type)
			type.isObject -> getObjectType(type)
			type.isArray -> {
				val dimension = type.arrayDimension
				val rootElement = type.arrayRootElement
				val rootType = when {
					rootElement.isPrimitive -> getPrimitiveType(rootElement)
					rootElement.isObject -> getObjectType(rootElement)
					else -> "ErrorType"
				}
				if (dimension > 0) "Array<".repeat(dimension) + rootType + ">".repeat(dimension) else rootType
			}

			else -> "ErrorType"
		}
	}
}
