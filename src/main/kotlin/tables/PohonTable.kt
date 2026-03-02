package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object PohonTable : UUIDTable("pohon") {
    val nama = varchar("nama", 100)
    val jenis = varchar("jenis", 100)
    val asalDaerah = varchar("asal_daerah", 255)
    val tinggiBatang = double("tinggi_batang")
    val pathGambar = varchar("path_gambar", 255)
    val deskripsi = text("deskripsi")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}