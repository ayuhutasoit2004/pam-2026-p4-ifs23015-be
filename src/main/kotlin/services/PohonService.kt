package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.PohonRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IPohonRepository
import java.io.File
import java.util.UUID

class PohonService(private val pohonRepository: IPohonRepository) {

    suspend fun getAllPohon(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val pohon = pohonRepository.getPohon(search)
        call.respond(DataResponse("success", "Berhasil mengambil daftar pohon",
            mapOf("pohon" to pohon)))
    }

    suspend fun getPohonById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID pohon tidak boleh kosong!")
        val pohon = pohonRepository.getPohonById(id)
            ?: throw AppException(404, "Data pohon tidak tersedia!")
        call.respond(DataResponse("success", "Berhasil mengambil data pohon",
            mapOf("pohon" to pohon)))
    }

    private suspend fun getPohonRequest(call: ApplicationCall): PohonRequest {
        val pohonReq = PohonRequest()
        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> pohonReq.nama = part.value.trim()
                        "jenis" -> pohonReq.jenis = part.value.trim()
                        "asalDaerah" -> pohonReq.asalDaerah = part.value.trim()
                        "tinggiBatang" -> pohonReq.tinggiBatang = part.value.toDoubleOrNull() ?: 0.0
                        "deskripsi" -> pohonReq.deskripsi = part.value
                    }
                }
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/pohon/$fileName"
                    val file = File(filePath)
                    file.parentFile.mkdirs()
                    part.provider().copyAndClose(file.writeChannel())
                    pohonReq.pathGambar = filePath
                }
                else -> {}
            }
            part.dispose()
        }
        return pohonReq
    }

    private fun validatePohonRequest(pohonReq: PohonRequest) {
        val validator = ValidatorHelper(pohonReq.toMap())
        validator.required("nama", "Nama pohon tidak boleh kosong")
        validator.required("jenis", "Jenis pohon tidak boleh kosong")
        validator.required("asalDaerah", "Asal daerah tidak boleh kosong")
        validator.required("deskripsi", "Deskripsi tidak boleh kosong")
        validator.required("pathGambar", "Gambar tidak boleh kosong")
        validator.validate()

        val file = File(pohonReq.pathGambar)
        if (!file.exists()) throw AppException(400, "Gambar pohon gagal diupload!")
    }

    suspend fun createPohon(call: ApplicationCall) {
        val pohonReq = getPohonRequest(call)
        validatePohonRequest(pohonReq)

        val existPohon = pohonRepository.getPohonByName(pohonReq.nama)
        if (existPohon != null) {
            File(pohonReq.pathGambar).takeIf { it.exists() }?.delete()
            throw AppException(409, "Pohon dengan nama ini sudah terdaftar!")
        }

        val pohonId = pohonRepository.addPohon(pohonReq.toEntity())
        call.respond(DataResponse("success", "Berhasil menambahkan data pohon",
            mapOf("pohonId" to pohonId)))
    }

    suspend fun updatePohon(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID pohon tidak boleh kosong!")
        val oldPohon = pohonRepository.getPohonById(id)
            ?: throw AppException(404, "Data pohon tidak tersedia!")

        val pohonReq = getPohonRequest(call)
        if (pohonReq.pathGambar.isEmpty()) pohonReq.pathGambar = oldPohon.pathGambar

        validatePohonRequest(pohonReq)

        if (pohonReq.nama != oldPohon.nama) {
            val existPohon = pohonRepository.getPohonByName(pohonReq.nama)
            if (existPohon != null) {
                File(pohonReq.pathGambar).takeIf { it.exists() }?.delete()
                throw AppException(409, "Pohon dengan nama ini sudah terdaftar!")
            }
        }

        if (pohonReq.pathGambar != oldPohon.pathGambar) {
            File(oldPohon.pathGambar).takeIf { it.exists() }?.delete()
        }

        val isUpdated = pohonRepository.updatePohon(id, pohonReq.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data pohon!")

        call.respond(DataResponse("success", "Berhasil mengubah data pohon", null))
    }

    suspend fun deletePohon(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID pohon tidak boleh kosong!")
        val oldPohon = pohonRepository.getPohonById(id)
            ?: throw AppException(404, "Data pohon tidak tersedia!")

        val isDeleted = pohonRepository.removePohon(id)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data pohon!")

        File(oldPohon.pathGambar).takeIf { it.exists() }?.delete()
        call.respond(DataResponse("success", "Berhasil menghapus data pohon", null))
    }

    suspend fun getPohonImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)
        val pohon = pohonRepository.getPohonById(id)
            ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(pohon.pathGambar)
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}