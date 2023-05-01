package app.cash.backfila.client.sqldelight

import app.cash.backfila.client.BackfillConfig
import app.cash.backfila.client.PrepareBackfillConfig
import app.cash.backfila.client.sqldelight.hockeydata.HockeyDataDatabase
import app.cash.backfila.client.sqldelight.hockeydata.SelectMinMax
import app.cash.backfila.protos.clientservice.KeyRange
import app.cash.sqldelight.Query
import com.squareup.wire.internal.newMutableList
import okio.ByteString.Companion.encodeUtf8
import javax.inject.Inject

class PlayerOriginBackfill @Inject constructor(
  val hockeyDataDatabase: HockeyDataDatabase,
) : SqlDelightDatasourceBackfill<HockeyDataDatabase, String, PlayerOriginBackfill.PlayerOriginParameters, Int>(hockeyDataDatabase) {
  val backfilledPlayers = newMutableList<Pair<String, String>>()

  override fun prepareAndValidateBackfill(config: PrepareBackfillConfig<PlayerOriginParameters>): Map<String, HockeyDataDatabase> {
    check(config.parameters.validate) { "Validate failed" }
    return mapOf("only" to hockeyDataDatabase)
  }

  override fun runOne(record: String, config: BackfillConfig<PlayerOriginParameters>) {
    if (record.contains(config.parameters.originRegex)) {
      backfilledPlayers.add(config.partitionName to record)
    }
  }

  data class PlayerOriginParameters(
    val originRegex: String = "CAN",
    val validate: Boolean = true,
  )

  override fun computeOverallMinMax(requestRange: KeyRange): Pair<Int?, Int?> {
    val minMax: SelectMinMax = transacter.transactionWithResult {
      transacter.hockeyPlayerQueries.selectMinMax().executeAsOne()
    }
    return Pair(minMax.min, minMax.max)

  }
}
