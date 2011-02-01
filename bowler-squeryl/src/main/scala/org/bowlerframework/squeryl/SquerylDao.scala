package org.bowlerframework.squeryl

import org.squeryl.{KeyedEntity, Table}
import org.squeryl.PrimitiveTypeMode._
import com.recursivity.commons.bean.{GenericsParser}
import org.squeryl.dsl.QueryYield

/**
 * Created by IntelliJ IDEA.
 * User: wfaler
 * Date: 30/01/2011
 * Time: 03:50
 * To change this template use File | Settings | File Templates.
 */

abstract class SquerylDao[T <: KeyedEntity[K], K](table: Table[T])(implicit m : scala.Predef.Manifest[T], k: Manifest[K]){
  private val typeString = m.toString.replace("[", "<").replace("]", ">")
  private val keyString = k.toString.replace("[", "<").replace("]", ">")
  private val typeDef = GenericsParser.parseDefinition(typeString)
  private val keyDef = GenericsParser.parseDefinition(keyString)
  def entityType = Class.forName(typeDef.clazz)


  var fieldCls: Class[_] = null
    keyDef.clazz match {
      case "Long" => fieldCls = classOf[Long]
      case "Int" => fieldCls = classOf[java.lang.Integer]
      case "Float" => fieldCls = classOf[java.lang.Float]
      case "Double" => fieldCls = classOf[java.lang.Double]
      case "Boolean" => fieldCls = classOf[Boolean]
      case "Short" => fieldCls = classOf[java.lang.Short]
      case _ => fieldCls = Class.forName(keyDef.clazz)
    }

  def keyType = fieldCls

  def create(entity: T) = table.insert(entity)

  def update(entity: T) = table.update(entity)

  def findAll(offset: Int = 0, results: Int = Integer.MAX_VALUE) = from(table)(a => select(a)).page(offset, results).toList

  //def orderByClause: QueryYield[_]

  def findById(id: K): Option[T]

  def delete(entity: T) = table.delete(entity.id)
}


