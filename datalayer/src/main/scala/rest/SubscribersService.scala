package rest

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.{Directives, Route}
import dao.BaseRepository
import domain.{Subscriber, SubscriberEntity, Subscribers}
import spray.json.DefaultJsonProtocol


object SubscribersProtocol extends DefaultJsonProtocol {
  implicit val subscribersFormat = jsonFormat3(SubscriberEntity)
  implicit val simpleSubscribersFormat = jsonFormat2(Subscriber)
}

class SubscribersService extends BaseCrudService[SubscriberEntity, Subscribers]{

  override val basePath: String = "subscribers"
  override val repository: BaseRepository[Subscribers, SubscriberEntity] = new SubscribersRepository

  // It's not nice but allows to reduce boiler plate
  override abstract def entityToResponse(entity: SubscriberEntity): ToResponseMarshallable = ???

  // It's not nice but allows to reduce boiler plate
  override abstract def entitiesToResponse(entities: Seq[SubscriberEntity]): ToResponseMarshallable = ???
}
