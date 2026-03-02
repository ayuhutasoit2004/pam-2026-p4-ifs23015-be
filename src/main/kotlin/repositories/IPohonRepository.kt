package org.delcom.repositories

import org.delcom.entities.Pohon

interface IPohonRepository {
    suspend fun getPohon(search: String): List<Pohon>
    suspend fun getPohonById(id: String): Pohon?
    suspend fun getPohonByName(name: String): Pohon?
    suspend fun addPohon(pohon: Pohon): String
    suspend fun updatePohon(id: String, newPohon: Pohon): Boolean
    suspend fun removePohon(id: String): Boolean
}