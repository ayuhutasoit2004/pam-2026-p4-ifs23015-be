package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Pohon

@Serializable
data class PohonRequest(
    var nama: String = "",
    var jenis: String = "",
    var asalDaerah: String = "",
    var tinggiBatang: Double = 0.0,
    var pathGambar: String = "",
    var deskripsi: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "jenis" to jenis,
            "asalDaerah" to asalDaerah,
            "tinggiBatang" to tinggiBatang,
            "pathGambar" to pathGambar,
            "deskripsi" to deskripsi,
        )
    }

    fun toEntity(): Pohon {
        return Pohon(
            nama = nama,
            jenis = jenis,
            asalDaerah = asalDaerah,
            tinggiBatang = tinggiBatang,
            pathGambar = pathGambar,
            deskripsi = deskripsi,
        )
    }
}