package org.bowlerframework

import util.matching.Regex
import collection.mutable.HashMap
import org.scalatra.util.{MultiMapHeadView, MapWithIndifferentAccess}
import org.apache.commons.fileupload.FileItem
import javax.servlet.http.HttpServletRequest

/**
 * Created by IntelliJ IDEA.
 * User: wfaler
 * Date: Dec 10, 2010
 * Time: 2:49:20 AM
 * To change this template use File | Settings | File Templates.
 */

trait ApplicationRouter{
  def addApplicationRoute(protocol: String, routeMatchers: String, routeExecutor: RouteExecutor)
  def addApplicationRoute(protocol: String, routeMatchers: Regex, routeExecutor: RouteExecutor)


  def flattenParameters(request: HttpServletRequest, params: MultiMapHeadView[String, String] with MapWithIndifferentAccess[String],
        multiParams: Map[String, Seq[String]], fileParams: collection.Map[String, FileItem], fileMultiParams: collection.Map[String, Seq[FileItem]]): Map[String, Any] ={

    val map = new HashMap[String, Any]

    params.foreach(f => {map += f._1 -> f._2})
    val nameEnum = request.getParameterNames
    while(nameEnum.hasMoreElements){
      val name = nameEnum.nextElement.asInstanceOf[String]
      val reqParams = request.getParameterValues(name)
      if(reqParams.length == 1){
        map += name -> reqParams(0)
      }else{
         map += name -> reqParams.toList
      }
    }

    if(multiParams("splat") != null)
      map += "splat" -> multiParams("splat").toList
    if(multiParams("captures") != null)
      map += "captures" -> multiParams("captures").toList

    fileParams.foreach(f => {map += f._1 -> f._2})
    fileMultiParams.foreach(f => {map += f._1 -> f._2.toList})
    return map.toMap
  }
}