package dao

import domain.{Categories, CategoryEntity}
import slick.lifted.TableQuery

class CategoriesRepository extends BaseRepository[Categories, CategoryEntity](TableQuery[Categories])