package dao

import test._
import models._
import dao.{DAO,QueryBuilder => Q}
import utils.TestData
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Engin Yoeyen on 22/09/14.
 */
class DaoSpec extends Specification{


  "Table.getTables" should {
    "return list of tables" in new App {
      val result = DAO.getTables  map {
        case list:List[Table] => list.exists( i => i.table_name == "books") mustEqual true
        case _ =>  failure("Could not fetch the tables")
      }
      sync { result }
    }
  }


  "Table.execute" should {

    "return some table content" in new App {
      val result = DAO.execute(Q.select("books")) map {
        case a:List[Row]   => {
          a.flatMap(b => List(b.toJson())) mustEqual TestData.jsonList
          success
        }
        case _ => failure("Table should exist and there should be some content" )
      }
      sync { result }
    }


    "return Error when there is no table" in new App {
      val result = DAO.execute(Q.select("wrongTableName")) map {
        case e:Error => success
        case _ => failure("There should be no table named wrongTableName")
      }
      sync { result }
    }

    "return Error when there is no column" in new App {
      val result = DAO.execute(Q.selectWhere("books","id2","2")) map {
        case e:Error => success
        case _ => failure("There should be no column called id2")
      }
      sync { result }
    }

    "return Error when there is no content as result of execution" in new App {
      val result = DAO.execute(Q.selectWhere("books","id","2")) map {
        case a:List[Row] => {
          val list = a.flatMap(b => List(b.toJson()))
          list.size mustEqual  0
          success
        }
        case _ => failure("There should be no id 2")
      }
      sync { result }
    }


  }
}
