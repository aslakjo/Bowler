package org.bowlerframework.http

import org.bowlerframework.ApplicationRouter
import javax.servlet.http.HttpServletRequest
import org.scalatra.util.{MultiMapHeadView, MapWithIndifferentAccess}
import org.apache.commons.fileupload.FileItem
import collection.mutable.HashMap


trait BowlerHttpApplicationRouter extends ApplicationRouter {
  def flattenParameters(request: HttpServletRequest, params: MultiMapHeadView[String, String] with MapWithIndifferentAccess[String],
                        multiParams: Map[String, Seq[String]], fileParams: collection.Map[String, FileItem] = Map[String, FileItem](), fileMultiParams: collection.Map[String, Seq[FileItem]] = Map[String, Seq[FileItem]]()): Map[String, Any] = {

    val map = new HashMap[String, Any]

    params.foreach(f => {
      map += f._1.replace("[]", "") -> f._2
    })
    val nameEnum = request.getParameterNames
    while (nameEnum.hasMoreElements) {
      val name = nameEnum.nextElement.asInstanceOf[String]
      val reqParams = request.getParameterValues(name)
      if (reqParams.length == 1) {
        map += name.replace("[]", "") -> reqParams(0)
      } else {
        map += name.replace("[]", "") -> reqParams.toList
      }
    }

    if (multiParams("splat") != null)
      map += "splat" -> multiParams("splat").toList
    if (multiParams("captures") != null)
      map += "captures" -> multiParams("captures").toList

    try {
      fileParams.foreach(f => {
        map += f._1.replace("[]", "") -> f._2
      })
    } catch {
      case e: Exception => {}
    }

    try {
      fileMultiParams.foreach(f => {
        map += f._1 -> f._2.toList
      })
    } catch {
      case e: Exception => {}
    }



    return map.toMap
  }
}