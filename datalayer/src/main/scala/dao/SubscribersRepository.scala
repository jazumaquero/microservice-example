package dao

import domain.{SubscriberEntity, Subscribers}
import slick.lifted.TableQuery

class SubscribersRepository extends BaseRepository[Subscribers, SubscriberEntity](TableQuery[Subscribers])
