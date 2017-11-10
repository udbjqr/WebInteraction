package zym.servlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *此类提供Json传输的处理与返回。
 *
 * 此类以utf-8格式处理,application/json类型的数据。
 * 并且允许任何形式的跨域访问
 *
 * create by 17/11/6.
 * @author zhengyimin
 */
abstract class JsonInteractionServlet:BaseServlet() {
	override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
		req.characterEncoding = "UTF-8"
		resp.characterEncoding = "UTF-8"
		resp.setHeader("content-type", "text/html;charset=UTF-8")
		resp.sendError(404,"这是我乱写的")
		resp.writer.println("我随便写的.你的请求地址是: ${req.requestURL},使用 的方法是:${req.method}")
	}

	/**
	 * 向前端发送一个错误信息,此信息将直接使用http的错误代码,如200 ,304,404,504等。
	 * 建议此代码以1000起步。
	 */
//	fun sendError(id:Int,message:String){
//		TODO("这里需要一个共享的Response对象。")
//	}
}