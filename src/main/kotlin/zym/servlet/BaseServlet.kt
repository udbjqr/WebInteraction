package zym.servlet

import org.apache.logging.log4j.LogManager
import zym.common.readFile
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse



/**
 *作为所有servlet的基类,此类提供Json传输的处理与返回。
 *
 * create by 17/11/6.
 * @author zhengyimin
 */
abstract class BaseServlet : HttpServlet() {

}

private val log = LogManager.getLogger("zym.servlet.BaseServlet")


@WebServlet(name = "test", urlPatterns = arrayOf("/"))
class test : HttpServlet() {
	override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
		resp.characterEncoding = "UTF-8"
		resp.setHeader("content-type", "text/html;charset=UTF-8")
		resp.sendError(65535, "你请求的地址:${req.servletPath},现在进行一个测试")
	}
}

fun main(args: Array<String>) {
	val a = Class.forName("zym.servlet.A")

	log.trace(a.getConstructor(Int::class.java).newInstance(28))
	log.trace(readFile("db.config"))
}

class A(val a: Int) {
	override fun toString(): String {
		return "A 这是一个a值:$a"
	}
}