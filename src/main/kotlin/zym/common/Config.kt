package zym.common

import com.alibaba.fastjson.JSONObject
import org.apache.logging.log4j.LogManager
import java.util.*


private val configs = HashMap<String, Config>()
private val logger = LogManager.getLogger(Config::class.java.name)

/**
 * 读取配置文件的公用方法.
 *
 *
 * 目前配置文件支持json格式的文件。
 * 查找文件的方式:
 * 应用程序资源目录->引用包内资源目录(多级)->本包内资源目录
 *
 * @author yimin
 */

class Config private constructor(private val configName: String, fileName: String) {
	private val separator = '.'
	private val content: JSONObject = JSONObject.parseObject(readResourceFileContext(fileName))

	override fun toString(): String {
		return content.toString()
	}

	/**
	 * 返回指定名称的配置项.
	 *
	 * @param name         指定的配置名称.
	 * @param defaultValue 当未定义值时使用的默认值
	 * @return 配置项的值
	 */
	operator fun <T> get(name: String, defaultValue: T): T {
		val value = get<T>(name)

		return value ?: defaultValue
	}


	/**
	 * 返回指定名称的配置项.
	 * 允许当未指定配置项时执行回调方法获得值
	 * 回调方法可做为返回异常时使用
	 *
	 * @param name     指定的配置名称.
	 * @param notFound 当名称在配置文件中未定义时，回调。可在此回调当中返回一个值
	 * @return 配置项的值
	 */
	operator fun <T> get(name: String, notFound: (name: String) -> T?): T? {
		val value = get<T>(name)

		if (value == null) {
			logger.debug("未找到配置的名称项，配置名:{}，项目名:{}", configName, name)
			val result = notFound(name)
			if (result != null) return result
		}

		return value
	}

	/**
	 * 返回指定名称的配置项.
	 *
	 * @param name 指定的配置名称.
	 * @return 配置项的值
	 */
	private operator fun <T> get(name: String): T? {
		val value = locate(name)[getLastName(name)]
		if (value == null) {
			logger.trace("未找到指定名称:{}的配置项。")
			return null
		}

		return try {
			@Suppress("UNCHECKED_CAST")
			value as T
		} catch (e: Exception) {
			logger.error("配置项值与请求配置值不相符。名称:{}.返回空", name)
			null
		}

	}

	/**
	 * 校验值是否存在.
	 *
	 * @param names 需要校验的名称
	 * @return 当找到第一个未存在的名称时返回。
	 */
	fun checkContent(vararg names: String): String? {
		return names.firstOrNull { !locate(it).containsKey(getLastName(it)) }
	}


	private fun locate(key: String): JSONObject {
		//如果未有"."分隔符
		if (!key.contains(separator)) return content

		var temp = content
		var index = 0
		var endIndex = key.indexOf(separator, index)

		while (endIndex > 0) {
			temp = getMap(key.substring(index, endIndex), temp)
			index = endIndex + 1

			endIndex = key.indexOf(separator, index)
		}

		return temp
	}

	private fun getMap(key: String, content: JSONObject): JSONObject {
		val node = content[key]
		return if (node != null && node is JSONObject) node else throw NullPointerException()
	}

	private fun getLastName(key: String): String {
		val index = key.lastIndexOf(separator)
		return if (index == -1) key else key.substring(index + 1, key.length)
	}

	/**
	 * 从指定的文件名读一个配置文件，并根据指定名称存放.
	 *
	 *
	 * 如多次调用此方法，会重新读指定文件名内容。
	 *
	 *
	 * 如每次指定name 不同，将会多次存放文件的多个副本.
	 *
	 * @param name     在此配置文件内的名称
	 * @param fileName 需要读取的文件名
	 * @return 新创建的配置文件
	 */
	fun createConfig(name: String, fileName: String): Config {
		logger.trace("新增加一个配置文件，名称:{}，读取文件名:{}", name, fileName)
		val config = Config(name, fileName)

		synchronized(configs) {
			configs.put(name, config)
		}

		return config
	}

	/**
	 * 找到指定名称的配置对角
	 *
	 * @param name 指定的名称，此名称为创建对象时指定的名称
	 * @return Confing对象。如果未找到返回null
	 */
	fun getConfig(name: String): Config {
		return configs[name]!!
	}
}
