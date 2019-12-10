package app.cash.backfila.client.misk.embedded

import app.cash.backfila.client.misk.Backfill
import app.cash.backfila.client.misk.internal.BatchSnapshot
import app.cash.backfila.client.misk.internal.InstanceCursor
import app.cash.backfila.protos.clientservice.GetNextBatchRangeResponse
import app.cash.backfila.protos.clientservice.PrepareBackfillResponse
import okio.ByteString

/**
 * Get an instance if this from [Backfila].
 */
interface BackfillRun<B : Backfill<*, *>> {
  val backfill: B
  val dryRun: Boolean
  val parameters: Map<String, ByteString>
  val rangeStart: String?
  val rangeEnd: String?

  var batchSize: Long
  var scanSize: Long
  var computeCountLimit: Long

  val prepareBackfillResponse: PrepareBackfillResponse

  val precomputeMatchingCount: Long
  val precomputeScannedCount: Long

  val instanceProgressSnapshot: Map<String, InstanceCursor>

  val batchesToRunSnapshot: List<BatchSnapshot>

  /** Prepares, scans and runs the whole backfill. */
  fun execute() {
    precomputeRemaining()
    scanRemaining()
    runAllScanned()
    check(complete()) { "$this failed to run everything. Probably a bug." }
  }

  fun precomputeScan(): GetNextBatchRangeResponse

  fun precomputeRemaining()

  fun finishedPrecomputing(): Boolean

  /** Does a single scan for batches on the instance provided. */
  fun instanceScan(instanceName: String): GetNextBatchRangeResponse

  /** Does a single scan for batches on any instance. */
  fun singleScan(): GetNextBatchRangeResponse

  /** Scans all the remaining batches and places them in the queue to run. */
  fun scanRemaining()

  fun finishedScanning(): Boolean

  /** Throws an error if there is no batch to run. */
  fun runBatch()

  /** Runs all the batches that are scanned and ready to run. */
  fun runAllScanned()

  fun complete(): Boolean
}