package com.gabriel4k2.myRandNotes.data

import android.content.Context
import com.gabriel4k2.myRandNotes.R
import com.gabriel4k2.myRandNotes.domain.IInstrumentRepository
import com.gabriel4k2.myRandNotes.domain.model.Instrument
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InstrumentRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val moshiInstance: Moshi
) : IInstrumentRepository {
    override fun retrieveInstrumentList(): List<Instrument> {
        val instrumentsJson =
            context.resources.openRawResource(R.raw.instruments).bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
        val listType =
            Types.newParameterizedType(List::class.java, Instrument::class.java)
        val instrumentListAdapter: JsonAdapter<List<Instrument>> =
            moshiInstance.adapter(listType)

        return instrumentListAdapter.fromJson(instrumentsJson) ?: emptyList()
    }
}
