package dao

import domain.{ProductEntity, Products}
import slick.lifted.TableQuery

class ProductsRepository extends BaseRepository[Products, ProductEntity](TableQuery[Products])
