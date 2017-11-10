package zym.common

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.util.Loader
import java.io.File
import java.net.URI

private val log = LogManager.getLogger("zym.common.common")
fun readFile(fileName: String): String {
	val uri = getResource(fileName)
	if (uri == null) {
		log.error("未找到对应文件名的文件:" + fileName)
		return ""
	}

	return File(uri).readText()
}

fun getResource(resource: String): URI? {
	var classLoader: ClassLoader? = Thread.currentThread().contextClassLoader
	if (classLoader != null) {
		val url = classLoader.getResource(resource)
		if (url != null) {
			return url.toURI()
		}
	}

	classLoader = ClassLoader.getSystemClassLoader()
	if (classLoader != null) {
		val url = classLoader.getResource(resource)
		if (url != null) {
			return url.toURI()
		}
	}

	classLoader = Loader::class.java.classLoader
	if (classLoader != null) {
		val url = classLoader.getResource(resource)
		if (url != null) {
			return url.toURI()
		}
	}

	return ClassLoader.getSystemResource(resource).toURI()
}

/**
 * 在资源链当中查找指定名称的内容,可使用'/'指定目录
 * 并且返回此资源的String内容。
 * 如果未找到,或者此指定为目录,将返回"" 而不是直接出现异常
 */
fun readResourceFileContext(name: String): String {
	val uri = getResource(name)
	if (uri == null) {
		log.warn("指定的文件名在资源链当中未找到!直接返回无内容,名称:" + name)
		return ""
	}

	val file = File(uri)
	if (file.isFile) {
		return file.readText()
	} else { //当此链接是目录的情况
		log.warn("读取的文件为目录,将直接返回无内容,名称:{}", name)
		return ""
	}
}