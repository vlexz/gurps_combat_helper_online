import javax.inject.Inject

import filters.LoggingFilter
import play.api.http.HttpFilters
import play.filters.cors.CORSFilter

class Filters @Inject()(
                         corsFilter: CORSFilter,
                         log: LoggingFilter
                       ) extends HttpFilters {

  val filters = Seq(log)
}
