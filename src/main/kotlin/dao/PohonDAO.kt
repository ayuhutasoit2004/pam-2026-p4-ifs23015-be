package org.delcom.dao

import org.delcom.tables.PohonTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class PohonDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, PohonDAO>(PohonTable)

    var nama by PohonTable.nama
    var jenis by PohonTable.jenis
    var asalDaerah by PohonTable.asalDaerah
    var tinggiBatang by PohonTable.tinggiBatang
    var pathGambar by PohonTable.pathGambar
    var deskripsi by PohonTable.deskripsi
    var createdAt by PohonTable.createdAt
    var updatedAt by PohonTable.updatedAt
}