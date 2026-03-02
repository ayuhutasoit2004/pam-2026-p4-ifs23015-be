package org.delcom.repositories

import org.delcom.dao.PohonDAO
import org.delcom.entities.Pohon
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.PohonTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class PohonRepository : IPohonRepository {
    override suspend fun getPohon(search: String): List<Pohon> = suspendTransaction {
        if (search.isBlank()) {
            PohonDAO.all()
                .orderBy(PohonTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            PohonDAO
                .find { PohonTable.nama.lowerCase() like keyword }
                .orderBy(PohonTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getPohonById(id: String): Pohon? = suspendTransaction {
        PohonDAO
            .find { PohonTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getPohonByName(name: String): Pohon? = suspendTransaction {
        PohonDAO
            .find { PohonTable.nama eq name }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addPohon(pohon: Pohon): String = suspendTransaction {
        val pohonDAO = PohonDAO.new {
            nama = pohon.nama
            jenis = pohon.jenis
            asalDaerah = pohon.asalDaerah
            tinggiBatang = pohon.tinggiBatang
            pathGambar = pohon.pathGambar
            deskripsi = pohon.deskripsi
            createdAt = pohon.createdAt
            updatedAt = pohon.updatedAt
        }
        pohonDAO.id.value.toString()
    }

    override suspend fun updatePohon(id: String, newPohon: Pohon): Boolean = suspendTransaction {
        val pohonDAO = PohonDAO
            .find { PohonTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (pohonDAO != null) {
            pohonDAO.nama = newPohon.nama
            pohonDAO.jenis = newPohon.jenis
            pohonDAO.asalDaerah = newPohon.asalDaerah
            pohonDAO.tinggiBatang = newPohon.tinggiBatang
            pohonDAO.pathGambar = newPohon.pathGambar
            pohonDAO.deskripsi = newPohon.deskripsi
            pohonDAO.updatedAt = newPohon.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removePohon(id: String): Boolean = suspendTransaction {
        val rowsDeleted = PohonTable.deleteWhere {
            PohonTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}

// Helper mapping DAO ke Entity
fun daoToModel(dao: PohonDAO) = Pohon(
    id = dao.id.value.toString(),
    nama = dao.nama,
    jenis = dao.jenis,
    asalDaerah = dao.asalDaerah,
    tinggiBatang = dao.tinggiBatang,
    pathGambar = dao.pathGambar,
    deskripsi = dao.deskripsi,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt,
)