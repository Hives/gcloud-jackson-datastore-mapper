package datastore_mapper.to_entity_jackson

import com.google.api.gax.retrying.RetrySettings
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.http.HttpTransportOptions
import org.threeten.bp.Duration

fun createDatastoreClient(project: String, namespace: String, timeoutMillis: Int = 1000): Datastore =
    DatastoreOptions
        .newBuilder()
        .setTransportOptions(HttpTransportOptions.newBuilder()
            .setConnectTimeout(timeoutMillis)
            .setReadTimeout(timeoutMillis)
            .build())
        .setRetrySettings(RetrySettings.newBuilder()
            .setTotalTimeout(Duration.ofMillis(timeoutMillis.toLong() * 5))
            .build())
        .setProjectId(project)
        .setNamespace(namespace)
        .build()
        .service
